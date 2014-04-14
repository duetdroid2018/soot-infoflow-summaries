package soot.jimple.infoflow.methodSummary.data.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import soot.SootField;
import soot.SootMethod;
import soot.jimple.infoflow.methodSummary.data.IFlowSink;
import soot.jimple.infoflow.methodSummary.data.IFlowSource;

public class FlowSinkAndSourceFactory {

	public static FlowSourceForSummary createFlowParamterSource(SootMethod m, int parameterIdx, List<SootField> ap) {
		return new FlowSourceForSummary(m, parameterIdx, ap);
	}

	public static FlowSourceForSummary createFlowFieldSource(List<SootField> f) {
		return new FlowSourceForSummary(f);
	}
	
	public static FlowSourceForSummary createFlowThisSource() {
		return new FlowSourceForSummary();
	}

	public static IFlowSink createFlowParamterSink(SootMethod m, int paraIdx, List<SootField> fields, boolean taintSubF) {
		return new DefaultFlowSink(m, paraIdx, fields, taintSubF);
	}
	public static IFlowSink createFlowParamterSink(SootMethod m, int paraIdx, SootField[] fields, boolean taintSubF) {
		return new DefaultFlowSink(m, paraIdx, Arrays.asList(fields), taintSubF);
	}

	public static IFlowSink createFlowReturnSink(List<SootField> fields, boolean taintSubF) {
		return new DefaultFlowSink(true,fields,taintSubF);
	}
	public static IFlowSink createFlowReturnSink(SootField[] fields, boolean taintSubF) {
		return new DefaultFlowSink(true,Arrays.asList(fields),taintSubF);
	}
	
	public static IFlowSink createFlowReturnSink(boolean taintSubF) {
		return new DefaultFlowSink(true,null,taintSubF);
	}

	public static IFlowSink createFlowFieldSink(SootField[] fields, boolean taintSubF) {
		return new DefaultFlowSink(false,Arrays.asList(fields),taintSubF);
	}

	public static IFlowSink createFlowFieldSink(List<SootField> fields, boolean taintSubF) {
		return new DefaultFlowSink(false,fields,taintSubF);
	}
}
