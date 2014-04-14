package soot.jimple.infoflow.test.methodSummary.junit;

import java.util.List;
import java.util.Set;

import junit.framework.Assert;

import org.junit.internal.runners.statements.Fail;

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
		String[] fieldSource;
		if(apSource != null){
			fieldSource = new String[2];
			fieldSource[0] = field;
			fieldSource[1] = apSource;
		}else{
			fieldSource = new String[1];
			fieldSource[0] = field;
		}
		
		String[] fieldSink;
		if(apSink != null){
			fieldSink = new String[2];
			fieldSink[1] = apSink;
		}else{
			fieldSink = new String[0];
		}
		fieldToReturnFlow(res, fieldSource, fieldSink);
		return true;
	}

	protected boolean containsFieldToFieldFlow(Set<AbstractMethodFlow> res, String sourceField, String apSource,
			String sinkField, String apSink) {
		
		String[] fieldSource;
		if(apSource != null){
			fieldSource = new String[2];
			fieldSource[0] = sourceField;
			fieldSource[1] = apSource;
		}else{
			fieldSource = new String[1];
			fieldSource[0] = sourceField;
		}
		
		String[] fieldSink;
		if(apSink != null){
			fieldSink = new String[2];
			fieldSink[0] = sinkField;
			fieldSink[1] = apSink;
		}else{
			fieldSink = new String[1];
			fieldSink[0] = sinkField;
		}
		fieldToFieldFlow(res, fieldSource, fieldSink);
		return true;
	}

	protected boolean containsFieldToParaFlow(Set<AbstractMethodFlow> res, String fieldName, String apSource,
			int paraIdx, String paraTyp2, String apSink) {
		String[] fieldSource;
		if(apSource != null){
			fieldSource = new String[2];
			fieldSource[0] = fieldName;
			fieldSource[1] = apSource;
		}else{
			fieldSource = new String[1];
			fieldSource[0] = fieldName;
		}
		
		String[] fieldSink;
		if(apSink != null){
			fieldSink = new String[1];
			//fieldSink[0] = paraTyp2;
			fieldSink[0] = apSink;
		}else{
			fieldSink = new String[0];
			//fieldSink[0] = paraTyp2;
		}
		fieldToParaFlow(res, fieldSource, paraIdx, fieldSink);
		return true;
	}

	protected boolean containsParaToReturn(Set<AbstractMethodFlow> res, int paraIdx, String typ, String apSource, String apSink) {
		String[] fieldSource;
		if(apSource != null){
			fieldSource = new String[1];
			//fieldSource[0] = typ;
			fieldSource[0] = apSource;
		}else{
			fieldSource = new String[0];
			//fieldSource[0] = typ;
		}
		
		String[] fieldSink;
		if(apSink != null){
			fieldSink = new String[1];			
			fieldSink[0] = apSink;
		}else{
			fieldSink = new String[0];
		}
		paramterToReturnFlow(res, paraIdx, fieldSource, fieldSink);		
		return true;
	}


	protected boolean containsParaToFieldFlow(Set<AbstractMethodFlow> res, int paraIdx, String type, String apSource, String field, String apSink) {
		String[] fieldSource;
		if(apSource != null){
			fieldSource = new String[2];
			fieldSource[0] = type;
			fieldSource[1] = apSource;
		}else{
			fieldSource = new String[1];
			fieldSource[0] = type;
		}
		
		String[] fieldSink;
		if(apSink != null){
			fieldSink = new String[2];
			fieldSink[0] = field;
			fieldSink[1] = apSink;
		}else{
			fieldSink = new String[1];
			fieldSink[0] = field;
		}
		paramterToFieldFlow(res, paraIdx, fieldSource, fieldSink);
		return true;
	}

	protected boolean containsParaToParaFlow(Set<AbstractMethodFlow> res, int paraIdx1, String paraType1, String apSource, int paraIdx2,
			String paraTyp2, String apSink) {
		String[] fieldSource;
		if(apSource != null){
			fieldSource = new String[1];
			fieldSource[0] = apSource;
		}else{
			fieldSource = new String[0];
		}
		
		String[] fieldSink;
		if(apSink != null){
			fieldSink = new String[1];
			fieldSink[0] = apSink;
		}else{
			fieldSink = new String[0];
		}
		paramterToParmater(res, paraIdx1, fieldSource, paraIdx2, fieldSink);
		return true;
	}
	
	
	protected void fieldToReturnFlow(Set<AbstractMethodFlow> res, String[] fieldSource, String[] fieldSink) {
		for (AbstractMethodFlow mf : res) {
			if (mf.source().isField() && mf.source().getFirstField().contains(fieldSource[0]) && testAP(mf.source(), fieldSource)) {
				if (mf.sink().isReturn() && testAP(mf.sink(), fieldSink))
					return ;
			}
		}
		Assert.fail();
	}

	protected void fieldToFieldFlow(Set<AbstractMethodFlow> res, String[] fieldSource, String[] fieldSink) {
		for (AbstractMethodFlow mf : res) {
			if (mf.source().isField() && mf.source().getFirstField().contains(fieldSource[0]) && testAP(mf.source(), fieldSource)) {
				if (mf.sink().isField() && mf.sink().getFirstField().contains(fieldSink[0])&& testAP(mf.sink(),fieldSink)) {
					return ;

				}
			}
		}
		Assert.fail();
	}

	protected void fieldToParaFlow(Set<AbstractMethodFlow> res, String[] fieldSource, int paraIdx, String[] fieldSink) {
		for (AbstractMethodFlow mf : res) {
			if (mf.source().isField() && mf.source().getFirstField().contains(fieldSource[0]) && testAP(mf.source(), fieldSource)) {
				if (mf.sink().isParamter() && mf.sink().getParamterIndex() == paraIdx
						 && testAP(mf.sink(), fieldSink))
					return;
			}
		}
		Assert.fail();
	}

	protected void paramterToReturnFlow(Set<AbstractMethodFlow> res, int paraIdx,  String[] fieldSource, String[] fieldSink) {
		for (AbstractMethodFlow mf : res) {
			if (mf.source().isParamter() && mf.source().getParamterIndex() == paraIdx
					&& testAP(mf.source(), fieldSource)) {
				if (mf.sink().isReturn() && testAP(mf.sink(), fieldSink))
					return ;
			}
		}
		Assert.fail();
	}


	protected void paramterToFieldFlow(Set<AbstractMethodFlow> res, int paraIdx,  String[] fieldSource, String[] fieldSink) {
		for (AbstractMethodFlow mf : res) {
			if (mf.source().isParamter() && mf.source().getParamterIndex() == paraIdx
					& testAP(mf.source(), fieldSource)) {
				if (mf.sink().isField() && mf.sink().getFirstField().contains(fieldSink[0])&& testAP(mf.sink(), fieldSink))
					return ;
			}
		}
		Assert.fail();
	}

	protected void paramterToParmater(Set<AbstractMethodFlow> res, int paraIdx1, String[] fieldSource, int paraIdx2,
			 String[] fieldSink) {
		for (AbstractMethodFlow mf : res) {
			if (mf.source().isParamter() && mf.source().getParamterIndex() == paraIdx1
					&& testAP(mf.source(), fieldSource)) {
				if (mf.sink().isParamter() && mf.sink().getParamterIndex() == paraIdx2
						&&  testAP(mf.sink(), fieldSink))
					return;
			}
		}
		Assert.fail();
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

	private boolean testAP(IFlowSinkSource sourceSink, String[] fields) {
		if(!sourceSink.hasAccessPath() && fields.length < 2)
			return true;
		if( sourceSink.getFieldCount() != (fields.length))
			return false;
		for(int i = 0; i < fields.length; i++){
			if(!sourceSink.getFields().get(i).replaceAll("[<>]", "").equals(fields[i].replaceAll("[<>]", "")))
				return false;
		}
		return true;
	}
	
	
	
}
