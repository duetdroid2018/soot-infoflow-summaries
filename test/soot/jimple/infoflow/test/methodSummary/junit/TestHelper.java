package soot.jimple.infoflow.test.methodSummary.junit;

import java.util.Set;

import soot.jimple.infoflow.methodSummary.SummaryGenerator;
import soot.jimple.infoflow.methodSummary.data.AbstractMethodFlow;
import soot.jimple.infoflow.methodSummary.data.IFlowSinkSource;

public class TestHelper {
	//protected static Map<String, Set<AbstractMethodFlow>> flows;

	protected final static String INT_TYPE = "int";
	protected final static String OBJECT_TYPE = "java.lang.Object";
	protected final static String NO_ACCESS_PATH = null;
	protected static final String OBJECT = "java.lang.Object";
	protected static final String OBJECT_ARRAY = "java.lang.Object[]";
	protected static final String INT = "int";
	protected static final String INT_ARRAY = "int[]";
	protected static final String LIST ="java.util.List";
	protected static final String STRING = "java.lang.String";
	protected static final String DATA = "soot.jimple.infoflow.test.methodSummary.Data";
	protected final static String DATACLASS_SIG = DATA;
	protected final static String DATACLASS_INT_FIELD = "<" + DATACLASS_SIG + ": int value>";
	protected final static String DATACLASS_OBJECT_FIELD = "<" + DATACLASS_SIG + ": " + OBJECT_TYPE + " data>";
	protected final static String DATACLASS_STRING_FIELD = "<" + DATACLASS_SIG + ": " + STRING + " i>";
	protected final static String LINKEDLIST_FIRST = "<java.util.LinkedList: java.util.LinkedList$Node first>";
	protected final static String LINKEDLIST_LAST = "<java.util.LinkedList: java.util.LinkedList$Node last>";

	
	protected boolean containsFieldToReturn(Set<AbstractMethodFlow> res, String field, String apSource, String apSink) {
		for (AbstractMethodFlow mf : res) {
			if (mf.source().isField() && mf.source().getField().contains(field) && testAP(mf.source(), apSource)) {
				if (mf.sink().isReturn() && testAP(mf.sink(), apSink))
					return true;
			}
		}
		return false;
	}

	protected boolean containsFieldToFieldFlow(Set<AbstractMethodFlow> res, String sourceField, String apSource,
			String sinkField, String apSink) {
		for (AbstractMethodFlow mf : res) {
			if (mf.source().isField() && mf.source().getField().contains(sourceField) && testAP(mf.source(), apSource)) {
				if (mf.sink().isField() && mf.sink().getField().contains(sinkField)&& testAP(mf.sink(),apSink)) {
					return true;

				}
			}
		}
		return false;
	}

	protected boolean containsFieldToParaFlow(Set<AbstractMethodFlow> res, String fieldName, String apSource,
			int paraIdx, String paraTyp2, String apSink) {
		for (AbstractMethodFlow mf : res) {
			if (mf.source().isField() && mf.source().getField().contains(fieldName) && testAP(mf.source(), apSource)) {
				if (mf.sink().isParamter() && mf.sink().getParamterIndex() == paraIdx
						&& mf.sink().getParaType().contains(paraTyp2) && testAP(mf.sink(), apSink))
					return true;
			}
		}
		return false;
	}

	protected boolean containsParaToReturn(Set<AbstractMethodFlow> res, int paraIdx, String typ, String apSource, String apSink) {
		for (AbstractMethodFlow mf : res) {
			if (mf.source().isParamter() && mf.source().getParamterIndex() == paraIdx
					&& mf.source().getParaType().contains(typ)&& testAP(mf.source(), apSource)) {
				if (mf.sink().isReturn() && testAP(mf.sink(), apSink))
					return true;
			}
		}
		return false;
	}


	protected boolean containsParaToFieldFlow(Set<AbstractMethodFlow> res, int paraIdx, String type, String apSource, String field, String apSink) {
		for (AbstractMethodFlow mf : res) {
			if (mf.source().isParamter() && mf.source().getParamterIndex() == paraIdx
					&& mf.source().getParaType().contains(type)&& testAP(mf.source(), apSource)) {
				if (mf.sink().isField() && mf.sink().getField().contains(field)&& testAP(mf.sink(), apSink))
					return true;
			}
		}
		return false;
	}

	protected boolean containsParaToParaFlow(Set<AbstractMethodFlow> res, int paraIdx1, String paraType1, String apSource, int paraIdx2,
			String paraTyp2, String apSink) {
		for (AbstractMethodFlow mf : res) {
			if (mf.source().isParamter() && mf.source().getParamterIndex() == paraIdx1
					&& mf.source().getParaType().contains(paraType1)&& testAP(mf.source(), apSource)) {
				if (mf.sink().isParamter() && mf.sink().getParamterIndex() == paraIdx2
						&& mf.sink().getParaType().contains(paraTyp2)&& testAP(mf.sink(), apSink))
					return true;
			}
		}
		return false;
	}
//
//	protected Set<AbstractMethodFlow> getMethodFlows(String method) {
//		for (String s : flows.keySet()) {
//			if (s.contains(method))
//				return flows.get(s);
//		}
//		return java.util.Collections.emptySet();
//	}

	protected SummaryGenerator getSummary() {
		return new SummaryGenerator();
	}

	private boolean testAP(IFlowSinkSource sourceSink, String ap) {
		if (ap == null || ap.length() == 0)
			return true;
		if (sourceSink.hasAccessPath() && sourceSink.getAccessPath().toString().equals(ap))
			return true;
		return false;
	}
}
