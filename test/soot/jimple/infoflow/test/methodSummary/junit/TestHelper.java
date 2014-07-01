package soot.jimple.infoflow.test.methodSummary.junit;

import java.util.List;
import java.util.Set;

import soot.jimple.infoflow.methodSummary.data.AbstractFlowSinkSource;
import soot.jimple.infoflow.methodSummary.data.FlowSink;
import soot.jimple.infoflow.methodSummary.data.FlowSource;
import soot.jimple.infoflow.methodSummary.data.MethodFlow;
import soot.jimple.infoflow.methodSummary.data.SourceSinkType;
import soot.jimple.infoflow.methodSummary.generator.SummaryGenerator;
import soot.jimple.infoflow.methodSummary.util.ClassFileInformation;

public abstract class TestHelper {

	protected final static String INT_TYPE = "int";
	protected final static String OBJECT_TYPE = "java.lang.Object";
	protected static final String OBJECT_ARRAY_TYPE = "java.lang.Object[]";
	protected static final String INT_ARRAY_TYPE = "int[]";
	protected static final String LIST_TYPE = "java.util.List";
	protected static final String STRING_TYPE = "java.lang.String";
	protected static final String DATA_TYPE = "soot.jimple.infoflow.test.methodSummary.Data";
	protected final static String DATACLASS_SIG = DATA_TYPE;
	protected final static String DATACLASS_INT_FIELD = "<" + DATACLASS_SIG + ": int value>";
	protected final static String DATACLASS_OBJECT_FIELD = "<" + DATACLASS_SIG + ": " + OBJECT_TYPE + " objectField>";
	protected final static String DATACLASS_STRING_FIELD = "<" + DATACLASS_SIG + ": " + STRING_TYPE + " stringField>";
	protected final static String LINKEDLIST_FIRST = "<java.util.LinkedList: java.util.LinkedList$Node first>";
	protected final static String LINKEDLIST_LAST = "<java.util.LinkedList: java.util.LinkedList$Node last>";
	protected final static String LINKEDLIST_ITEM = "<java.util.LinkedList$Node: java.lang.Object item>";

	protected boolean containsFlow(Set<MethodFlow> flows, SourceSinkType sourceTyp, String[] sourceFields,
			SourceSinkType sinkTyp, String[] sinkFields) {
		return containsFlow(flows, sourceTyp, -1 ,sourceFields, sinkTyp, -1,sinkFields);
	}
	protected boolean containsFlow(Set<MethodFlow> flows, SourceSinkType sourceTyp, String[] sourceFields,
			SourceSinkType sinkTyp, int sinkParameterIdx, String[] sinkFields) {
		return containsFlow(flows, sourceTyp, -1 ,sourceFields, sinkTyp, sinkParameterIdx,sinkFields);
	}
	protected boolean containsFlow(Set<MethodFlow> flows, SourceSinkType sourceTyp, int sourceParamterIdx, String[] sourceFields,
			SourceSinkType sinkTyp, String[] sinkFields) {
		return containsFlow(flows, sourceTyp, sourceParamterIdx,sourceFields,  sinkTyp, -1, sinkFields);
	}

	protected boolean containsFlow(Set<MethodFlow> flows, SourceSinkType sourceTyp,int sourceParamterIdx, String[] sourceFields,
			SourceSinkType sinkTyp, int sinkParamterIdx,String[] sinkFields) {
		for (MethodFlow mf : flows) {
			FlowSource source = mf.source();
			FlowSink sink = mf.sink();
			if (source.type().equals(sourceTyp) && sink.type().equals(sinkTyp)) {
				if (checkParamter(source, sourceTyp, sourceParamterIdx)
						&& checkParamter(sink, sinkTyp, sinkParamterIdx)) {
					if (checkFields(source, sourceFields) && checkFields(sink, sinkFields))
						return true;
				}
			}
		}
		
		return false;

	}

	private boolean checkParamter(AbstractFlowSinkSource s, SourceSinkType sType, int parameterIdx) {
		if (sType.equals(SourceSinkType.Parameter)) {
			if (s.type().equals(SourceSinkType.Parameter)) {
				return s.getParameterIndex() == parameterIdx;
			}
			return false;
		}
		return true;
	}

	private boolean checkFields(AbstractFlowSinkSource s, String[] fields) {
		if (fields == null || fields.length == 0) {
			if (!s.hasAccessPath())
				return true;
			return false;
		}
		if (s.getFieldCount() != fields.length)
			return false;
		for (int i = 0; i < fields.length; i++) {
			if (!s.getField(i).replaceAll("[<>]", "").equals(fields[i].replaceAll("[<>]", "")))
				return false;
		}

		return true;
	}

	abstract SummaryGenerator getSummary() ;

	protected List<String> methods() {
		return ClassFileInformation.getMethodSignatures(getClazz(),true);
	}

	abstract Class<?> getClazz();

}
