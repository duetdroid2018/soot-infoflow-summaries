package soot.jimple.infoflow.methodSummary.util;

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

public class SummaryTaintPropagationHandler implements TaintPropagationHandler {
	private String methodSig;
	private SootMethod method = null;
	private Set<Abstraction> result = new ConcurrentHashSet<Abstraction>();

	public SummaryTaintPropagationHandler(String m) {
		methodSig = m;
	}

	@Override
	public void notifyFlowIn(Unit stmt, Set<Abstraction> result, BiDiInterproceduralCFG<Unit, SootMethod> cfg,
			FlowFunctionType type) {
		if (type.equals(TaintPropagationHandler.FlowFunctionType.ReturnFlowFunction)) {
			if (method == null)
				method = Scene.v().getMethod(methodSig);
			
			SootMethod m = cfg.getMethodOf(stmt);
			if (method.equals(m))
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
	}
	
	@Override
	public void notifyFlowOut(Unit stmt, Set<Abstraction> taints,
			BiDiInterproceduralCFG<Unit, SootMethod> cfg, FlowFunctionType type) {
	}
	
	public Set<Abstraction> getResult() {
		return result;
	}
	
}
