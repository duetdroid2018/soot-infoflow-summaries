package soot.jimple.infoflow.methodSummary.data.impl;

import soot.SootField;
import soot.SootMethod;
import soot.jimple.infoflow.methodSummary.data.IFlowSink;
import soot.jimple.infoflow.methodSummary.data.IFlowSource;

public class FlowSinkAndSourceFactory {

	public static FlowSourceForSummary createFlowParamterSource(SootMethod m, int parameterIdx, SootField ap) {
		return new FlowSourceForSummary(m, parameterIdx, ap);
	}

	public static FlowSourceForSummary createFlowFieldSource(SootField f, SootField ap) {
		return new FlowSourceForSummary(f, ap);
	}
	
	public static FlowSourceForSummary createFlowThisSource() {
		return new FlowSourceForSummary();
	}

	public static IFlowSink createFlowParamterSink(SootMethod m, int paraIdx, SootField ap, boolean taintSubF) {
		return new DefaultFlowSink(m, paraIdx, ap, taintSubF);
	}

	public static IFlowSink createFlowReturnSink(SootField ap, boolean taintSubF) {
		return new DefaultFlowSink(ap,taintSubF);
	}
	
	public static IFlowSink createFlowReturnSink(boolean taintSubF) {
		return new DefaultFlowSink(null,taintSubF);
	}

	public static IFlowSink createFlowFieldSink(SootField field, SootField ap, boolean taintSubF) {
		return new DefaultFlowSink(field,ap,taintSubF);
	}

}
