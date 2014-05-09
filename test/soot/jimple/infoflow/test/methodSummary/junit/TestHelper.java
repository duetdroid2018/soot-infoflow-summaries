package soot.jimple.infoflow.test.methodSummary.junit;

import java.util.List;
import java.util.Set;

import junit.framework.Assert;
import soot.jimple.infoflow.methodSummary.SummaryGenerator;
import soot.jimple.infoflow.methodSummary.data.AbstractFlowSinkSource;
import soot.jimple.infoflow.methodSummary.data.AbstractMethodFlow;
import soot.jimple.infoflow.methodSummary.data.IFlowSink;
import soot.jimple.infoflow.methodSummary.data.SourceSinkType;
import soot.jimple.infoflow.methodSummary.sourceSink.IFlowSource;
import soot.jimple.infoflow.methodSummary.util.ClassFileInformation;

public abstract class TestHelper {
	// protected static Map<String, Set<AbstractMethodFlow>> flows;
	protected final static String INT_TYPE = "int";
	protected final static String OBJECT_TYPE = "java.lang.Object";
	protected final static String NO_ACCESS_PATH = null;
	protected static final String OBJECT = "java.lang.Object";
	protected static final String OBJECT_ARRAY = "java.lang.Object[]";
	protected static final String INT = "int";
	protected static final String INT_ARRAY = "int[]";
	protected static final String LIST = "java.util.List";
	protected static final String STRING = "java.lang.String";
	protected static final String DATA = "soot.jimple.infoflow.test.methodSummary.Data";
	protected final static String DATACLASS_SIG = DATA;
	protected final static String DATACLASS_INT_FIELD = "<" + DATACLASS_SIG + ": int value>";
	protected final static String DATACLASS_OBJECT_FIELD = "<" + DATACLASS_SIG + ": " + OBJECT_TYPE + " data>";
	protected final static String DATACLASS_STRING_FIELD = "<" + DATACLASS_SIG + ": " + STRING + " i>";
	protected final static String LINKEDLIST_FIRST = "<java.util.LinkedList: java.util.LinkedList$Node first>";
	protected final static String LINKEDLIST_LAST = "<java.util.LinkedList: java.util.LinkedList$Node last>";
	protected final static String LINKEDLIST_ITEM = "<java.util.LinkedList$Node: java.lang.Object item>";

//	protected boolean containsFieldToReturn(Set<AbstractMethodFlow> res, String field, String apSource, String apSink) {
//		String[] fieldSource;
//		if (apSource != null) {
//			fieldSource = new String[2];
//			fieldSource[0] = field;
//			fieldSource[1] = apSource;
//		} else {
//			fieldSource = new String[1];
//			fieldSource[0] = field;
//		}
//
//		String[] fieldSink;
//		if (apSink != null) {
//			fieldSink = new String[1];
//			fieldSink[0] = apSink;
//		} else {
//			fieldSink = new String[0];
//		}
//		fieldToReturnFlow(res, fieldSource, fieldSink);
//		return true;
//	}
//
//	protected boolean containsFieldToFieldFlow(Set<AbstractMethodFlow> res, String sourceField, String apSource,
//			String sinkField, String apSink) {
//
//		String[] fieldSource;
//		if (apSource != null) {
//			fieldSource = new String[2];
//			fieldSource[0] = sourceField;
//			fieldSource[1] = apSource;
//		} else {
//			fieldSource = new String[1];
//			fieldSource[0] = sourceField;
//		}
//
//		String[] fieldSink;
//		if (apSink != null) {
//			fieldSink = new String[2];
//			fieldSink[0] = sinkField;
//			fieldSink[1] = apSink;
//		} else {
//			fieldSink = new String[1];
//			fieldSink[0] = sinkField;
//		}
//		fieldToFieldFlow(res, fieldSource, fieldSink);
//		return true;
//	}
//
//	protected boolean containsFieldToParaFlow(Set<AbstractMethodFlow> res, String fieldName, String apSource,
//			int paraIdx, String paraTyp2, String apSink) {
//		String[] fieldSource;
//		if (apSource != null) {
//			fieldSource = new String[2];
//			fieldSource[0] = fieldName;
//			fieldSource[1] = apSource;
//		} else {
//			fieldSource = new String[1];
//			fieldSource[0] = fieldName;
//		}
//
//		String[] fieldSink;
//		if (apSink != null) {
//			fieldSink = new String[1];
//			// fieldSink[0] = paraTyp2;
//			fieldSink[0] = apSink;
//		} else {
//			fieldSink = new String[0];
//			// fieldSink[0] = paraTyp2;
//		}
//		fieldToParaFlow(res, fieldSource, paraIdx, fieldSink);
//		return true;
//	}
//
//	protected boolean containsParaToReturn(Set<AbstractMethodFlow> res, int paraIdx, String typ, String apSource,
//			String apSink) {
//		String[] fieldSource;
//		if (apSource != null) {
//			fieldSource = new String[1];
//			// fieldSource[0] = typ;
//			fieldSource[0] = apSource;
//		} else {
//			fieldSource = new String[0];
//			// fieldSource[0] = typ;
//		}
//
//		String[] fieldSink;
//		if (apSink != null) {
//			fieldSink = new String[1];
//			fieldSink[0] = apSink;
//		} else {
//			fieldSink = new String[0];
//		}
//		paramterToReturnFlow(res, paraIdx, fieldSource, fieldSink);
//		return true;
//	}
//
//	protected boolean containsParaToFieldFlow(Set<AbstractMethodFlow> res, int paraIdx, String type, String apSource,
//			String field, String apSink) {
//		String[] fieldSource;
//		if (apSource != null) {
//			fieldSource = new String[1];
//			// fieldSource[0] = type;
//			fieldSource[0] = apSource;
//		} else {
//			fieldSource = new String[0];
//			// fieldSource[0] = type;
//		}
//
//		String[] fieldSink;
//		if (apSink != null) {
//			fieldSink = new String[2];
//			fieldSink[0] = field;
//			fieldSink[1] = apSink;
//		} else {
//			fieldSink = new String[1];
//			fieldSink[0] = field;
//		}
//		paramterToFieldFlow(res, paraIdx, fieldSource, fieldSink);
//		return true;
//	}
//
//	protected boolean containsParaToParaFlow(Set<AbstractMethodFlow> res, int paraIdx1, String paraType1,
//			String apSource, int paraIdx2, String paraTyp2, String apSink) {
//		String[] fieldSource;
//		if (apSource != null) {
//			fieldSource = new String[1];
//			fieldSource[0] = apSource;
//		} else {
//			fieldSource = new String[0];
//		}
//
//		String[] fieldSink;
//		if (apSink != null) {
//			fieldSink = new String[1];
//			fieldSink[0] = apSink;
//		} else {
//			fieldSink = new String[0];
//		}
//		paramterToParmater(res, paraIdx1, fieldSource, paraIdx2, fieldSink);
//		return true;
//	}

//	protected void fieldToReturnFlow(Set<AbstractMethodFlow> res, String[] fieldSource, String[] fieldSink) {
//		for (AbstractMethodFlow mf : res) {
//			if (mf.source().isField() && mf.source().getField(0).contains(fieldSource[0])
//					&& testAP(mf.source(), fieldSource)) {
//				if (mf.sink().isReturn() && testAP(mf.sink(), fieldSink))
//					return;
//			}
//		}
//		Assert.fail();
//	}
//
//	protected void fieldToFieldFlow(Set<AbstractMethodFlow> res, String[] fieldSource, String[] fieldSink) {
//		for (AbstractMethodFlow mf : res) {
//			if (mf.source().isField() && mf.source().getField(0).contains(fieldSource[0])
//					&& testAP(mf.source(), fieldSource)) {
//				if (mf.sink().isField() && mf.sink().getField(0).contains(fieldSink[0]) && testAP(mf.sink(), fieldSink)) {
//					return;
//
//				}
//			}
//		}
//		Assert.fail();
//	}
//
//	protected void fieldToParaFlow(Set<AbstractMethodFlow> res, String[] fieldSource, int paraIdx, String[] fieldSink) {
//		for (AbstractMethodFlow mf : res) {
//			if (mf.source().isField() && mf.source().getField(0).contains(fieldSource[0])
//					&& testAP(mf.source(), fieldSource)) {
//				if (mf.sink().isParamter() && mf.sink().getParamterIndex() == paraIdx && testAP(mf.sink(), fieldSink))
//					return;
//			}
//		}
//		Assert.fail();
//	}
//
//	protected void paramterToReturnFlow(Set<AbstractMethodFlow> res, int paraIdx, String[] fieldSource,
//			String[] fieldSink) {
//		for (AbstractMethodFlow mf : res) {
//			if (mf.source().isParamter() && mf.source().getParamterIndex() == paraIdx
//					&& testAP(mf.source(), fieldSource)) {
//				if (mf.sink().isReturn() && testAP(mf.sink(), fieldSink))
//					return;
//			}
//		}
//		Assert.fail();
//	}
	protected boolean containsFlow(Set<AbstractMethodFlow> flows, SourceSinkType sourceTyp, String[] sourceFields,
			SourceSinkType sinkTyp, String[] sinkFields) {
		return containsFlow(flows, sourceTyp, -1 ,sourceFields, sinkTyp, -1,sinkFields);
	}
	protected boolean containsFlow(Set<AbstractMethodFlow> flows, SourceSinkType sourceTyp, String[] sourceFields,
			SourceSinkType sinkTyp, int sinkParameterIdx, String[] sinkFields) {
		return containsFlow(flows, sourceTyp, -1 ,sourceFields, sinkTyp, sinkParameterIdx,sinkFields);
	}
	protected boolean containsFlow(Set<AbstractMethodFlow> flows, SourceSinkType sourceTyp, int sourceParamterIdx, String[] sourceFields,
			SourceSinkType sinkTyp, String[] sinkFields) {
		return containsFlow(flows, sourceTyp, sourceParamterIdx,sourceFields,  sinkTyp, -1, sinkFields);
	}

	protected boolean containsFlow(Set<AbstractMethodFlow> flows, SourceSinkType sourceTyp,int sourceParamterIdx, String[] sourceFields,
			SourceSinkType sinkTyp, int sinkParamterIdx,String[] sinkFields) {
		for (AbstractMethodFlow mf : flows) {
			IFlowSource source = mf.source();
			IFlowSink sink = mf.sink();
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
				return s.getParamterIndex() == parameterIdx;
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

	protected void paramterToFieldFlow(Set<AbstractMethodFlow> res, int paraIdx, String[] fieldSource,
			String[] fieldSink) {
		for (AbstractMethodFlow mf : res) {
			if (mf.source().isParamter() && mf.source().getParamterIndex() == paraIdx
					& testAP(mf.source(), fieldSource)) {
				if (mf.sink().isField() && mf.sink().getField(0).contains(fieldSink[0]) && testAP(mf.sink(), fieldSink))
					return;
			}
		}
		Assert.fail();
	}

	protected void paramterToParmater(Set<AbstractMethodFlow> res, int paraIdx1, String[] fieldSource, int paraIdx2,
			String[] fieldSink) {
		for (AbstractMethodFlow mf : res) {
			if (mf.source().isParamter() && mf.source().getParamterIndex() == paraIdx1
					&& testAP(mf.source(), fieldSource)) {
				if (mf.sink().isParamter() && mf.sink().getParamterIndex() == paraIdx2 && testAP(mf.sink(), fieldSink))
					return;
			}
		}
		Assert.fail();
	}

	//
	// protected Set<AbstractMethodFlow> getMethodFlows(String method) {
	// for (String s : flows.keySet()) {
	// if (s.contains(method))
	// return flows.get(s);
	// }
	// return java.util.Collections.emptySet();
	// }

	protected SummaryGenerator getSummary() {
		return new SummaryGenerator();
	}

	private boolean testAP(AbstractFlowSinkSource sourceSink, String[] fields) {
		if (!sourceSink.hasAccessPath() && fields.length < 2)
			return true;
		if (sourceSink.getFieldCount() != (fields.length))
			return false;
		for (int i = 0; i < fields.length; i++) {
			if (!sourceSink.getFields().get(i).replaceAll("[<>]", "").equals(fields[i].replaceAll("[<>]", "")))
				return false;
		}
		return true;
	}

	protected List<String> methods() {
		return ClassFileInformation.getMethodSignature(getClazz());
	}

	abstract Class getClazz();

}
