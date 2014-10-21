package soot.jimple.infoflow.methodSummary.handler;

import java.util.Set;

import soot.Scene;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.jimple.ReturnStmt;
import soot.jimple.infoflow.data.Abstraction;
import soot.jimple.infoflow.handlers.TaintPropagationHandler;
import soot.jimple.infoflow.util.ConcurrentHashSet;
import soot.jimple.toolkits.ide.icfg.BiDiInterproceduralCFG;

/**
 * The SummaryTaintPropagationHandler collects all abstraction that reach the return statement of a specified method.
 * 
 */
public class SummaryTaintPropagationHandler implements TaintPropagationHandler {
	private final String methodSig;
	private SootMethod method = null;
	private Set<Abstraction> result = new ConcurrentHashSet<Abstraction>();

	public SummaryTaintPropagationHandler(String m) {
		methodSig = m;
	}

	@Override
	public void notifyFlowIn(Unit stmt, Set<Abstraction> result, BiDiInterproceduralCFG<Unit, SootMethod> cfg,
			FlowFunctionType type) {
		if (!type.equals(TaintPropagationHandler.FlowFunctionType.ReturnFlowFunction))
			return;
		
		// Get the method for which we should create the summary
		if (method == null)
			method = Scene.v().getMethod(methodSig);
			
		// Get the method containing the current statement. If this does
		// not match the method for which we shall create a summary, we
		// ignore it.
		SootMethod m = cfg.getMethodOf(stmt);
		if (!method.equals(m))
			return;
		
		if (m.getName().equals("addAll") && stmt.toString().contains("l5"))
			System.out.println("x");
		
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
				for (Value param : m.getActiveBody().getParameterLocals())
					if (abs.getAccessPath().getPlainValue() == param) {
						isValidResult = true;
						break;
					}
			
			// If the value is a field, we save it
			isValidResult |= (!m.isStatic()
					&& abs.getAccessPath().getPlainValue() == m.getActiveBody().getThisLocal());
			
			if (isValidResult)
				this.result.add(abs);
		}
	}
	
	@Override
	public void notifyFlowOut(Unit stmt, Set<Abstraction> taints,
			BiDiInterproceduralCFG<Unit, SootMethod> cfg, FlowFunctionType type) {
	}
	
	public Set<Abstraction> getResult() {
		return result;
	}
	
}
