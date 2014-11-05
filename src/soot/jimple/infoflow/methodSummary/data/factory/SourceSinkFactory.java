package soot.jimple.infoflow.methodSummary.data.factory;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import soot.SootField;
import soot.SootMethod;
import soot.jimple.infoflow.methodSummary.data.FlowSink;
import soot.jimple.infoflow.methodSummary.data.FlowSource;
import soot.jimple.infoflow.methodSummary.data.SourceSinkType;

public class SourceSinkFactory {

	public static FlowSource createParamterSource(SootMethod m, int parameterIdx, List<SootField> ap) {
		return new FlowSource(SourceSinkType.Parameter,parameterIdx, sootFieldsToString(ap));
	}
	
	public static FlowSource createFieldSource(List<SootField> f) {
		return new FlowSource(SourceSinkType.Field, -1, sootFieldsToString(f));
	}
	
	public static FlowSource createThisSource() {
		return new FlowSource(SourceSinkType.Field,-1);
	}

	public static FlowSink createParamterSink(SootMethod m, int paraIdx, List<SootField> fields, boolean taintSubF) {
		return new FlowSink(SourceSinkType.Parameter, paraIdx, sootFieldsToString(fields), taintSubF);
	}
	
	public static FlowSink createParamterSink(SootMethod m, int paraIdx, SootField[] fields, boolean taintSubF) {
		return new FlowSink(SourceSinkType.Parameter,paraIdx, sootFieldsToString(Arrays.asList(fields)), taintSubF);
	}

	public static FlowSink createReturnSink(List<SootField> fields, boolean taintSubF) {
		return new FlowSink(SourceSinkType.Return,-1,sootFieldsToString(fields),taintSubF);
	}
	
	public static FlowSink createReturnSink(SootField[] fields, boolean taintSubF) {
		return new FlowSink(SourceSinkType.Return,-1,sootFieldsToString(Arrays.asList(fields)),taintSubF);
	}
	
	public static FlowSink createReturnSink(boolean taintSubF) {
		return new FlowSink(SourceSinkType.Return, -1, taintSubF);
	}

	public static FlowSink createFieldSink(SootField[] fields, boolean taintSubF) {
		return new FlowSink(SourceSinkType.Field,-1,sootFieldsToString(Arrays.asList(fields)),taintSubF);
	}
	
	public static FlowSink createFieldSink(List<String> fields, boolean taintSubF) {
		return new FlowSink(SourceSinkType.Field,-1,fields,taintSubF);
	}
	
	public static FlowSink createFlowFieldSink(List<SootField> fields, boolean taintSubF) {
		return new FlowSink(SourceSinkType.Field,-1,sootFieldsToString(fields),taintSubF);
	}
	
	private static List<String> sootFieldsToString(List<SootField> ap){
		List<String> res = new LinkedList<String>();
		if(ap != null){
			for(SootField f : ap)
				res.add(f.toString());
		}
		return res;
	}
	
}
