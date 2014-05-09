package soot.jimple.infoflow.methodSummary.sourceSinkFactory;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import soot.SootField;
import soot.SootMethod;
import soot.jimple.infoflow.methodSummary.data.DefaultFlowSink;
import soot.jimple.infoflow.methodSummary.data.DefaultFlowSource;
import soot.jimple.infoflow.methodSummary.data.IFlowSink;
import soot.jimple.infoflow.methodSummary.data.SourceSinkType;

public class SourceSinkFactory {

	public static DefaultFlowSource createParamterSource(SootMethod m, int parameterIdx, List<SootField> ap) {
		return new DefaultFlowSource(SourceSinkType.Parameter,parameterIdx, stootFieldsToString(ap));
	}
	

	public static DefaultFlowSource createFieldSource(List<SootField> f) {
		return new DefaultFlowSource(SourceSinkType.Field, -1, stootFieldsToString(f));
	}

	
	public static DefaultFlowSource createThisSource() {
		return new DefaultFlowSource(SourceSinkType.Field,-1,null);
	}

	public static IFlowSink createParamterSink(SootMethod m, int paraIdx, List<SootField> fields, boolean taintSubF) {
		return new DefaultFlowSink(SourceSinkType.Parameter, paraIdx, stootFieldsToString(fields), taintSubF);
	}
	public static IFlowSink createParamterSink(SootMethod m, int paraIdx, SootField[] fields, boolean taintSubF) {
		return new DefaultFlowSink(SourceSinkType.Parameter,paraIdx, stootFieldsToString(Arrays.asList(fields)), taintSubF);
	}

	public static IFlowSink createReturnSink(List<SootField> fields, boolean taintSubF) {
		return new DefaultFlowSink(SourceSinkType.Return,-1,stootFieldsToString(fields),taintSubF);
	}
	public static IFlowSink createReturnSink(SootField[] fields, boolean taintSubF) {
		return new DefaultFlowSink(SourceSinkType.Return,-1,stootFieldsToString(Arrays.asList(fields)),taintSubF);
	}
	
	public static IFlowSink createReturnSink(boolean taintSubF) {
		return new DefaultFlowSink(SourceSinkType.Return,-1,null,taintSubF);
	}

	public static IFlowSink createFieldSink(SootField[] fields, boolean taintSubF) {
		return new DefaultFlowSink(SourceSinkType.Field,-1,stootFieldsToString(Arrays.asList(fields)),taintSubF);
	}
	
	public static IFlowSink createFieldSink(List<String> fields, boolean taintSubF) {
		return new DefaultFlowSink(SourceSinkType.Field,-1,fields,taintSubF);
	}


	public static IFlowSink createFlowFieldSink(List<SootField> fields, boolean taintSubF) {
		return new DefaultFlowSink(SourceSinkType.Field,-1,stootFieldsToString(fields),taintSubF);
	}
	
	private static List<String> stootFieldsToString(List<SootField> ap){
		List<String> res = new LinkedList<String>();
		if(ap != null){
			for(SootField f : ap)
				res.add(f.toString());
		}
		return res;
	}
}
