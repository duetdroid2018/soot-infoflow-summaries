package soot.jimple.infoflow.methodSummary.handler;

import java.util.Collections;
import java.util.Set;

import soot.Scene;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.jimple.ReturnStmt;
import soot.jimple.Stmt;
import soot.jimple.ThrowStmt;
import soot.jimple.infoflow.data.Abstraction;
import soot.jimple.infoflow.handlers.TaintPropagationHandler;
import soot.jimple.infoflow.methodSummary.generator.GapManager;
import soot.jimple.infoflow.solver.cfg.IInfoflowCFG;
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
			IInfoflowCFG cfg,
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
			handleCallToReturnFlow((Stmt) stmt, result, cfg);
	}
	
	/**
	 * Handles a taint that leaves a method at an exit node
	 * @param stmt The statement at which the taint leaves the method
	 * @param abs The taint abstraction that leaves the method
	 * @param cfg The control flow graph
	 */
	private void handleReturnFlow(Unit stmt,
			Abstraction abs,
			IInfoflowCFG cfg) {		
		// Check whether we must register the abstraction for post-processing
		// We ignore inactive abstractions
		if (!abs.isAbstractionActive())
			return;
				
		// If this a taint on a field of a gap object, we need to report it as
		// well. Code can obtain references to library objects are store data in
		// there.
		boolean isGapField = gapManager.isLocalReferencedInGap(
				abs.getAccessPath().getPlainValue());
		
		if (isValueReturnedFromCall(stmt, abs) || isGapField)
			addResult(abs, (Stmt) stmt);
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
	
	private void handleCallToReturnFlow(Stmt stmt, Abstraction abs,
			IInfoflowCFG cfg) {
		// Check whether we must construct a gap
		if (gapManager.needsGapConstruction(stmt, abs, cfg))
			addResult(abs, stmt);
	}
	
	/**
	 * Adds the given abstraction and statement to the result map
	 * @param abs The abstraction to be collected
	 * @param stmt The statement at which the abstraction was collected
	 */
	private void addResult(Abstraction abs, Stmt stmt) {
		// Add the abstraction to the map. If we already have an equal
		// abstraction, we must add the current one as a neighbor.
		if (!this.result.put(abs, stmt)) {
			for (Abstraction abs2 : result.keySet()) {
				if (abs.equals(abs2)) {
					abs2.addNeighbor(abs);
				}
			}
		}
	}

	@Override
	public Set<Abstraction> notifyFlowOut(Unit u,
			Abstraction d1,
			Abstraction incoming,
			Set<Abstraction> outgoing,
			IInfoflowCFG cfg,
			FlowFunctionType type) {
		// Do not propagate through excluded methods
		SootMethod sm = cfg.getMethodOf(u);
		if (excludedMethods.contains(sm.getSignature()))
			return Collections.emptySet();
		
		return outgoing;
	}
	
	public MultiMap<Abstraction, Stmt> getResult() {
		return result;
	}
	
	public GapManager getGapManager() {
		return this.gapManager;
	}
		
}
