package soot.jimple.infoflow.methodSummary.util;

import java.util.HashSet;
import java.util.Set;

import soot.Scene;
import soot.SootMethod;
import soot.Unit;
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
		if (type.equals(TaintPropagationHandler.FlowFunctionType.ReturnFlowFunction)) {
			if(method == null){
				method = Scene.v().getMethod(methodSig);
			}
			try{
				SootMethod m = cfg.getMethodOf(stmt);
				if (method.equals(m)) {
					this.result.addAll(result);
				}
				
			}catch(Exception e){
				
			}
		}
	}

	public Set<Abstraction> getResult() {
		return result;
	}
}
