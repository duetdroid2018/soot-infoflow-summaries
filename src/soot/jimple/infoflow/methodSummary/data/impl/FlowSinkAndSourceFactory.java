package soot.jimple.infoflow.methodSummary.data.impl;

import soot.SootField;
import soot.SootMethod;
import soot.jimple.infoflow.methodSummary.data.AbstractFlowSink;
import soot.jimple.infoflow.methodSummary.data.AbstractFlowSource;

public class FlowSinkAndSourceFactory {

	public static AbstractFlowSource createFlowParamterSource(SootMethod m, int parameterIdx, SootField ap) {
		return new DefaultFlowSource(m, parameterIdx, ap);
	}

	public static AbstractFlowSource createFlowFieldSource(SootField f, SootField ap) {
		return new DefaultFlowSource(f, ap);
	}

	
	public static AbstractFlowSource createFlowThisSource() {
		return new DefaultFlowSource();
	}

	public static AbstractFlowSink createFlowParamterSink(SootMethod m, int paraIdx, SootField ap, boolean taintSubF) {
		return new DefaultFlowSink(m, paraIdx, ap, taintSubF);
	}

	public static AbstractFlowSink createFlowReturnSink(SootField ap, boolean taintSubF) {
		return new DefaultFlowSink(ap,taintSubF);
	}
	public static AbstractFlowSink createFlowReturnSink(boolean taintSubF) {
		return new DefaultFlowSink(null,taintSubF);
	}

	public static AbstractFlowSink createFlowFieldSink(SootField field, SootField ap, boolean taintSubF) {
		return new DefaultFlowSink(field,ap,taintSubF);
	}

}
