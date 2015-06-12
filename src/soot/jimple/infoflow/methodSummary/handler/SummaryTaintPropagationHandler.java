package soot.jimple.infoflow.methodSummary.handler;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import soot.Scene;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.jimple.DefinitionStmt;
import soot.jimple.InstanceInvokeExpr;
import soot.jimple.InvokeExpr;
import soot.jimple.ReturnStmt;
import soot.jimple.Stmt;
import soot.jimple.ThrowStmt;
import soot.jimple.infoflow.data.Abstraction;
import soot.jimple.infoflow.data.AccessPath;
import soot.jimple.infoflow.handlers.TaintPropagationHandler;
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
		// Initialize the method we are interested in
		if(method == null)
			method = Scene.v().getMethod(methodSig);
		
		// Handle the flow function
		if (type.equals(TaintPropagationHandler.FlowFunctionType.ReturnFlowFunction)) {
			// We only record leaving flows for those methods that we actually
			// want to generate a summary for
			SootMethod m = cfg.getMethodOf(stmt);
			if (!isMethodToSummarize(m))
				return;
			
			// Record the flow which leaves the method
			handleReturnFlow(stmt, result, cfg);
		}
		else if (type.equals(TaintPropagationHandler.FlowFunctionType.CallToReturnFlowFunction))
			handleCallToReturnFlow(stmt, result, cfg);
	}
	
	/**
	 * Handles a taint that leaves a method at an exit node
	 * @param stmt The statement at which the taint leaves the method
	 * @param abs The taint abstraction that leaves the method
	 * @param cfg The control flow graph
	 */
	private void handleReturnFlow(Unit stmt,
			Abstraction abs,
			BiDiInterproceduralCFG<Unit, SootMethod> cfg) {
		// Check whether we must register the abstraction for post-processing
		// We ignore inactive abstractions
		if (!abs.isAbstractionActive())
			return;
		
		// If this a taint on a field of a gap object, we need to report it as
		// well. Code can obtain references to library objects are store data in
		// there.
		boolean isGapField = abs.getAccessPath().isInstanceFieldRef()
				&& gapManager.isLocalReferencedInGap(abs.getAccessPath().getPlainValue());
		
		if (isValueReturnedFromCall(stmt, abs) || isGapField)
			this.result.put(abs, (Stmt) stmt);
	}
	
	/**
	 * Checks whether the given value is used in the given statement
	 * @param stmt The statement to check
	 * @param abs The value to check
	 * @return True if the given value is used in the given statement, otherwise
	 * false
	 */
	private boolean isValueUsedInStmt(Stmt stmt, Abstraction abs) {
		if (!stmt.containsInvokeExpr())
			return false;
		InvokeExpr iexpr = stmt.getInvokeExpr();
		
		// If this value is a parameter, we take it
		for (int i = 0; i < iexpr.getArgCount(); i++)
			if (abs.getAccessPath().getPlainValue() == iexpr.getArg(i))
				return true;
		
		// If this is the base local, we take it
		return iexpr instanceof InstanceInvokeExpr
				&& ((InstanceInvokeExpr) iexpr).getBase() == abs.getAccessPath().getPlainValue();
	}
	
	/**
	 * Checks whether the given value is returned from inside the callee at the
	 * given call site
	 * @param stmt The statement to check
	 * @param abs The value to check
	 * @return True if the given value is returned from inside the given callee
	 * at the given call site, otherwise false
	 */
	private boolean isValueReturnedFromCall(Unit stmt, Abstraction abs) {
		// If the value is returned, we save it
		if (stmt instanceof ReturnStmt) {
			ReturnStmt retStmt = (ReturnStmt) stmt;
			if (retStmt.getOp() == abs.getAccessPath().getPlainValue())
				return true;
		}
		
		// If the value is thrown, we save it
		if (stmt instanceof ThrowStmt) {
			ThrowStmt throwStmt = (ThrowStmt) stmt;
			if (throwStmt.getOp() == abs.getAccessPath().getPlainValue())
				return true;
		}
		
		// If the value corresponds to a parameter, we save it
		for (Value param : method.getActiveBody().getParameterLocals())
			if (abs.getAccessPath().getPlainValue() == param)
				return true;
		
		// If the value is a field, we save it
		return (!method.isStatic()
				&& abs.getAccessPath().getPlainValue() == method.getActiveBody().getThisLocal());
	}
	
	private void handleCallToReturnFlow(Unit u,
			Abstraction abs,
			BiDiInterproceduralCFG<Unit, SootMethod> cfg) {
		// Do not report inactive flows into gaps
		if (!abs.isAbstractionActive())
			return;
		
		// If we have callees, we analyze them as usual
		Collection<SootMethod> callees = cfg.getCalleesOfCallAt(u);
		if (callees != null && !callees.isEmpty())
			return;
				
		// Do not create gaps for constructors or static initializers
		final Stmt stmt = (Stmt) u;
		final SootMethod targetMethod = stmt.getInvokeExpr().getMethod();
		if (targetMethod.isConstructor()
				|| targetMethod.isStaticInitializer()
				|| targetMethod.isNative())
			return;
		
		// Do not produce flows from one statement to itself
		if (abs.getSourceContext() != null
				&& abs.getSourceContext().getStmt() == u)
			return;
		
		// If we don't have any callees, we need to build a gap into our
		// summary. The taint wrapper takes care of continuing the analysis
		// after the gap.
		if (isValueUsedInStmt(stmt, abs))
			if (hasFlowSource(abs.getAccessPath(), stmt))
				this.result.put(abs, stmt);
	}
	
	@Override
	public Set<Abstraction> notifyFlowOut(Unit u,
			Abstraction d1,
			Abstraction incoming,
			Set<Abstraction> outgoing,
			BiDiInterproceduralCFG<Unit, SootMethod> cfg,
			FlowFunctionType type) {
		// Do not propagate through excluded methods
		SootMethod sm = cfg.getMethodOf(u);
		if (excludedMethods.contains(sm.getSignature()))
			return Collections.emptySet();
		
		return outgoing;
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
	
	public GapManager getGapManager() {
		return this.gapManager;
	}
		
}
