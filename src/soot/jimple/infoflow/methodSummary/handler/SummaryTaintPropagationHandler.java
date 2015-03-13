package soot.jimple.infoflow.methodSummary.handler;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import soot.Scene;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.jimple.ReturnStmt;
import soot.jimple.Stmt;
import soot.jimple.infoflow.data.Abstraction;
import soot.jimple.infoflow.data.AccessPath;
import soot.jimple.infoflow.data.SourceContext;
import soot.jimple.infoflow.handlers.TaintPropagationHandler;
import soot.jimple.infoflow.methodSummary.data.FlowSource;
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
	private SootMethod method = null;
	
	private MultiMap<Abstraction, Stmt> result = new ConcurrentHashMultiMap<>();
	private MultiMap<Stmt, AccessPath> gapAccessPaths = null;
	
	public SummaryTaintPropagationHandler(String m, String parentClass) {
		this(m, parentClass, Collections.<String>emptySet());
	}
	
	public SummaryTaintPropagationHandler(String m, String parentClass,
			Set<String> excludedMethods) {
		this.methodSig = m;
		this.parentClass = parentClass;
		this.excludedMethods = excludedMethods;
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
		// If we have callees, we analyze them as usual
		Collection<SootMethod> callees = cfg.getCalleesOfCallAt(stmt);
		if (callees != null && !callees.isEmpty())
			return;
		
		// If we don't have any callees, we need to build a gap into our
		// summary. The taint wrapper takes care of continuing the analysis
		// after the gap.
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
		
		// If this is a gap access path, we remove the predecessor to cut the
		// propagation path at the gap
		if (gapAccessPaths != null)
			for (Abstraction outAbs : outgoing)
				if (outAbs.getCurrentStmt() != null
						&& outAbs.getCurrentStmt() == outAbs.getCorrespondingCallSite()) {
					Set<AccessPath> apSet = gapAccessPaths.get(outAbs.getCurrentStmt());
					if (apSet != null && apSet.contains(outAbs.getAccessPath())) {
						outAbs.setPredecessor(null);
						
						// Create the source information pointing to the gap
						FlowSource sourceInfo = null;
						
						// If no longer have a predecessor, we must fake a
						// source context
						outAbs.setSourceContext(new SourceContext(outAbs.getAccessPath(),
								outAbs.getCurrentStmt(), sourceInfo));
					}
				}
		
		return outgoing;
	}
	
	public MultiMap<Abstraction, Stmt> getResult() {
		return result;
	}
	
	public void setGapAccessPaths(MultiMap<Stmt, AccessPath> gapAccessPaths) {
		this.gapAccessPaths = gapAccessPaths;
	}
	
}
