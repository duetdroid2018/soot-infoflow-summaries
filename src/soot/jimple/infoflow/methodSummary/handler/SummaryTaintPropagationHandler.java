package soot.jimple.infoflow.methodSummary.handler;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import soot.Scene;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.jimple.ReturnStmt;
import soot.jimple.Stmt;
import soot.jimple.infoflow.data.Abstraction;
import soot.jimple.infoflow.handlers.TaintPropagationHandler;
import soot.jimple.toolkits.ide.icfg.BiDiInterproceduralCFG;

/**
 * The SummaryTaintPropagationHandler collects all abstraction that reach the return statement of a specified method.
 * 
 */
public class SummaryTaintPropagationHandler implements TaintPropagationHandler {
	
	private final String methodSig;
	private SootMethod method = null;
	
	private Map<Abstraction, Stmt> result = new ConcurrentHashMap<>();
	
	public SummaryTaintPropagationHandler(String m) {
		this.methodSig = m;
	}

	@Override
	public void notifyFlowIn(Unit stmt, Set<Abstraction> result,
			BiDiInterproceduralCFG<Unit, SootMethod> cfg, FlowFunctionType type) {
		// Get the method for which we should create the summary
		if (method == null)
			method = Scene.v().getMethod(methodSig);
		
		// Get the method containing the current statement. If this does
		// not match the method for which we shall create a summary, we
		// ignore it.
		SootMethod m = cfg.getMethodOf(stmt);
		if (!method.equals(m))
			return;

		// Handle the flow function
		if (type.equals(TaintPropagationHandler.FlowFunctionType.ReturnFlowFunction))
			handleReturnFlow(stmt, result, cfg);
		else if (type.equals(TaintPropagationHandler.FlowFunctionType.CallToReturnFlowFunction))
			handleCallToReturnFlow(stmt, result, cfg);
	}
	
	private void handleReturnFlow(Unit stmt, Set<Abstraction> result,
			BiDiInterproceduralCFG<Unit, SootMethod> cfg) {
		// Check all results to see whether we must register an entry for
		// post-processing
		for (Abstraction abs : result) {
			// We ignore inactive abstractions
			if (!abs.isAbstractionActive())
				continue;
			
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
	}
	
	private void handleCallToReturnFlow(Unit stmt, Set<Abstraction> result,
			BiDiInterproceduralCFG<Unit, SootMethod> cfg) {
		// If we have callees, we analyze them as usual
		Collection<SootMethod> callees = cfg.getCalleesOfCallAt(stmt);
		if (callees != null && !callees.isEmpty())
			return;
		
		for (Abstraction abs : result) {
			// If we don't have any callees, we need to build a gap into our
			// summary. The taint wrapper takes care of continuing the analysis
			// after the gap.
			this.result.put(abs, (Stmt) stmt);
		}
	}
	
	@Override
	public void notifyFlowOut(Unit stmt, Set<Abstraction> taints,
			BiDiInterproceduralCFG<Unit, SootMethod> cfg, FlowFunctionType type) {
	}
	
	public Map<Abstraction, Stmt> getResult() {
		return result;
	}
	
}
