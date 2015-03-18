package soot.jimple.infoflow.methodSummary.handler;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import soot.Scene;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.jimple.DefinitionStmt;
import soot.jimple.InstanceInvokeExpr;
import soot.jimple.ReturnStmt;
import soot.jimple.Stmt;
import soot.jimple.infoflow.data.Abstraction;
import soot.jimple.infoflow.data.AccessPath;
import soot.jimple.infoflow.data.SourceContext;
import soot.jimple.infoflow.handlers.TaintPropagationHandler;
import soot.jimple.infoflow.methodSummary.data.FlowSource;
import soot.jimple.infoflow.methodSummary.data.GapDefinition;
import soot.jimple.infoflow.methodSummary.data.SourceSinkType;
import soot.jimple.infoflow.methodSummary.generator.GapManager;
import soot.jimple.toolkits.ide.icfg.BiDiInterproceduralCFG;
import soot.util.ConcurrentHashMultiMap;
import soot.util.MultiMap;

/**
 * The SummaryTaintPropagationHandler collects all abstraction that reach the return statement of a specified method.
 * 
 */
public class SummaryTaintPropagationHandler implements TaintPropagationHandler {
	
	private final String methodSig;
	private final String parentClass;
	private final Set<String> excludedMethods;
	private final GapManager gapManager;
	private SootMethod method = null;
	
	private MultiMap<Abstraction, Stmt> result = new ConcurrentHashMultiMap<>();
	
	public SummaryTaintPropagationHandler(String m, String parentClass,
			GapManager gapManager) {
		this(m, parentClass, Collections.<String>emptySet(), gapManager);
	}
	
	public SummaryTaintPropagationHandler(String m, String parentClass,
			Set<String> excludedMethods,
			GapManager gapManager) {
		this.methodSig = m;
		this.parentClass = parentClass;
		this.excludedMethods = excludedMethods;
		this.gapManager = gapManager;
	}
	
	private boolean isMethodToSummarize(SootMethod currentMethod) {
		// Initialize the method we are interested in
		if(method == null)
			method = Scene.v().getMethod(methodSig);
		
		// This must either be the method defined by signature or the
		// corresponding one in the parent class
		if (currentMethod == method)
			return true;
		
		return currentMethod.getDeclaringClass().getName().equals(parentClass)
					&& currentMethod.getSubSignature().equals(method.getSubSignature());
	}
	
	@Override
	public void notifyFlowIn(Unit stmt,
			Abstraction result,
			BiDiInterproceduralCFG<Unit, SootMethod> cfg,
			FlowFunctionType type) {
		// Get the method containing the current statement. If this does
		// not match the method for which we shall create a summary, we
		// ignore it.
		SootMethod m = cfg.getMethodOf(stmt);
		if (!isMethodToSummarize(m))
			return;

		// Handle the flow function
		if (type.equals(TaintPropagationHandler.FlowFunctionType.ReturnFlowFunction))
			handleReturnFlow(stmt, result, cfg);
		else if (type.equals(TaintPropagationHandler.FlowFunctionType.CallToReturnFlowFunction))
			handleCallToReturnFlow(stmt, result, cfg);
	}
	
	private void handleReturnFlow(Unit stmt,
			Abstraction abs,
			BiDiInterproceduralCFG<Unit, SootMethod> cfg) {
		// Check whether we must register the abstraction for post-processing
		// We ignore inactive abstractions
		if (!abs.isAbstractionActive())
			return;
		
		// If the value is returned, we save it
		boolean isValidResult = false;
		if (stmt instanceof ReturnStmt) {
			ReturnStmt retStmt = (ReturnStmt) stmt;
			isValidResult |= (retStmt.getOp() == abs.getAccessPath().getPlainValue());
		}
		
		// If the value corresponds to a parameter, we save it
		if (!isValidResult)
			for (Value param : method.getActiveBody().getParameterLocals())
				if (abs.getAccessPath().getPlainValue() == param) {
					isValidResult = true;
					break;
				}
		
		// If the value is a field, we save it
		isValidResult |= (!method.isStatic()
				&& abs.getAccessPath().getPlainValue() == method.getActiveBody().getThisLocal());
		
		if (isValidResult)
			this.result.put(abs, (Stmt) stmt);
	}
	
	private void handleCallToReturnFlow(Unit stmt,
			Abstraction abs,
			BiDiInterproceduralCFG<Unit, SootMethod> cfg) {
		// Do not report inactive flows into gaps
		if (!abs.isAbstractionActive())
			return;
		
		// If we have callees, we analyze them as usual
		Collection<SootMethod> callees = cfg.getCalleesOfCallAt(stmt);
		if (callees != null && !callees.isEmpty())
			return;
		
		// If we don't have any callees, we need to build a gap into our
		// summary. The taint wrapper takes care of continuing the analysis
		// after the gap.
		if (hasFlowSource(abs.getAccessPath(), (Stmt) stmt))
			this.result.put(abs, (Stmt) stmt);
	}
	
	@Override
	public Set<Abstraction> notifyFlowOut(Unit u,
			Abstraction incoming,
			Set<Abstraction> outgoing,
			BiDiInterproceduralCFG<Unit, SootMethod> cfg,
			FlowFunctionType type) {
		// Do not propagate through excluded methods
		SootMethod sm = cfg.getMethodOf(u);
		if (excludedMethods.contains(sm.getSignature()))
			return Collections.emptySet();
		
		Stmt stmt = (Stmt) u;
		if (!stmt.containsInvokeExpr())
			return outgoing;
		
		// If this is a gap access path, we remove the predecessor to cut the
		// propagation path at the gap
		GapDefinition gap = gapManager.getGapForCall(stmt);
		if (gap != null)
			for (Abstraction outAbs : outgoing)
				// We only need to look at call-to-return edges
				if (outAbs.getCurrentStmt() != null
						&& outAbs.getCurrentStmt() == outAbs.getCorrespondingCallSite()) {
					// Create the source information pointing to the gap. This may not
					// be unique
					outAbs.setPredecessor(null);
					
					// If no longer have a predecessor, we must fake a
					// source context
					outAbs.setSourceContext(new SourceContext(outAbs.getAccessPath(),
							outAbs.getCurrentStmt(), getFlowSource(outAbs.getAccessPath(),
									outAbs.getCurrentStmt(), gap)));
				}
		return outgoing;
	}
	
	/**
	 * Creates a flow source based on an access path and a gap invocation
	 * statement. The flow source need not necessarily be unique. For a
	 * call z=b.foo(a,a), the flow source for access path "a" can either be
	 * parameter 0 or parameter 1.   
	 * @param accessPath The access path for which to create the flow source
	 * @param stmt The statement that calls the sink with the given
	 * access path
	 * @param The definition of the gap from which the data flow originates
	 * @return The set of generated flow sources
	 */
	private Set<FlowSource> getFlowSource(AccessPath accessPath, Stmt stmt,
			GapDefinition gap) {
		Set<FlowSource> res = new HashSet<FlowSource>();
		
		// This can be a base object
		if (stmt.getInvokeExpr() instanceof InstanceInvokeExpr)
			if (((InstanceInvokeExpr) stmt.getInvokeExpr()).getBase() == accessPath.getPlainValue())
				res.add(new FlowSource(SourceSinkType.Field, accessPath.getBaseType().toString(),
						gap));
		
		// This can be a parameter
		for (int i = 0; i < stmt.getInvokeExpr().getArgCount(); i++)
			if (stmt.getInvokeExpr().getArg(i) == accessPath.getPlainValue())
				res.add(new FlowSource(SourceSinkType.Parameter, i, accessPath.getBaseType().toString(),
						gap));
		
		// This can be a return value
		if (stmt instanceof DefinitionStmt)
			if (((DefinitionStmt) stmt).getLeftOp() == accessPath.getPlainValue())
				res.add(new FlowSource(SourceSinkType.Return, accessPath.getBaseType().toString(),
						gap));				
		
		return res;
	}
	
	/**
	 * Checks whether the given access path can be a flow source at the given
	 * statement if the statement is interpreted as a gap   
	 * @param accessPath The access path for which to check the flow source
	 * @param stmt The statement that calls the sink with the given
	 * access path
	 * @return True if the given access path can lead to a gap at the given
	 * statement, otherwise false
	 */
	private boolean hasFlowSource(AccessPath accessPath, Stmt stmt) {
		// This can be a base object
		if (stmt.getInvokeExpr() instanceof InstanceInvokeExpr)
			if (((InstanceInvokeExpr) stmt.getInvokeExpr()).getBase() == accessPath.getPlainValue())
				return true;
		
		// This can be a parameter
		for (int i = 0; i < stmt.getInvokeExpr().getArgCount(); i++)
			if (stmt.getInvokeExpr().getArg(i) == accessPath.getPlainValue())
				return true;
		
		// This can be a return value
		if (stmt instanceof DefinitionStmt)
			if (((DefinitionStmt) stmt).getLeftOp() == accessPath.getPlainValue())
				return true;				
		
		return false;
	}
	
	public MultiMap<Abstraction, Stmt> getResult() {
		return result;
	}
		
}
