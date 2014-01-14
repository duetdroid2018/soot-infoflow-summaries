package soot.jimple.infoflow.methodSummary.util;

import java.util.HashSet;
import java.util.Set;

import soot.Scene;
import soot.SootMethod;
import soot.Unit;
import soot.jimple.ReturnStmt;
import soot.jimple.infoflow.data.Abstraction;
import soot.jimple.infoflow.handlers.TaintPropagationHandler;
import soot.jimple.toolkits.ide.icfg.BiDiInterproceduralCFG;

public class SummaryTaintPropagationHandler implements TaintPropagationHandler {
	private String methodSig;
	private SootMethod method = null;
	private Set<Abstraction> result = new HashSet<Abstraction>();

	public SummaryTaintPropagationHandler(String m) {
		methodSig = m;
	}

	@Override
	public void notifyFlowIn(Unit stmt, Set<Abstraction> result, BiDiInterproceduralCFG<Unit, SootMethod> cfg,
			FlowFunctionType type) {
		if (type.equals(TaintPropagationHandler.FlowFunctionType.ReturnFlowFunction)
				&& stmt instanceof ReturnStmt) {
			if (method == null)
				method = Scene.v().getMethod(methodSig);
			ReturnStmt retStmt = (ReturnStmt) stmt;
						
			SootMethod m = cfg.getMethodOf(stmt);
			if (method.equals(m))
				for (Abstraction abs : result)
					if (retStmt.getOp() == abs.getAccessPath().getPlainValue())
						this.result.add(abs);
		}
	}

	public Set<Abstraction> getResult() {
		return result;
	}
}
