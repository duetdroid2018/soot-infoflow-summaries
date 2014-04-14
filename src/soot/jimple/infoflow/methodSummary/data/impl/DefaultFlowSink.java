package soot.jimple.infoflow.methodSummary.data.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import soot.SootField;
import soot.SootMethod;
import soot.jimple.infoflow.methodSummary.data.IFlowSink;
import soot.jimple.infoflow.methodSummary.xml.XMLConstants;

public class DefaultFlowSink implements IFlowSink {
	
	private final int parameterIdx;
	private final boolean isReturn;
	private final SummaryAccessPath accessPath;
	private final boolean taintSubFields;
	
	
	/**
	 * creates a return or fied sink sink
	 * @param ap
	 * @param taintSubF
	 * @param reSink
	 */
	public DefaultFlowSink(boolean reSink, List<SootField> fields, boolean taintSubF)  {
		isReturn = reSink; 
		accessPath = new SummaryAccessPath(fields);
		parameterIdx = -1;
		this.taintSubFields = taintSubF;
	}

	/**
	 * creates a parameter sink
	 * @param m
	 * @param paraIdx2
	 * @param ap
	 * @param taintSubFields
	 */
	public DefaultFlowSink(SootMethod m, int paraIdx, List<SootField> fields, boolean taintSubFields) {
		accessPath = new SummaryAccessPath(fields);
		isReturn = false;
		this.parameterIdx = paraIdx;
		this.taintSubFields = taintSubFields;
	}

	@Override
	public boolean isParamter() {
		return parameterIdx >= 0;
	}

	@Override
	public int getParamterIndex() {
		return parameterIdx;
	}

	@Override
	public boolean isField() {
		return !isParamter() && !isReturn();
	}


	@Override
	public List<String> getFields() {
		return accessPath.getAP();
	}

	@Override
	public boolean isReturn() {
		return isReturn;
	}
	
	@Override
	public Map<String, String> xmlAttributes() {
		Map<String, String> res = new HashMap<String, String>();
		if (isParamter()) {
			res.put(XMLConstants.ATTRIBUTE_FLOWTYPE, XMLConstants.VALUE_PARAMETER);
			res.put(XMLConstants.ATTRIBUTE_PARAMTER_INDEX, getParamterIndex() + "");
		} else if (isField()) {
			res.put(XMLConstants.ATTRIBUTE_FLOWTYPE, XMLConstants.VALUE_FIELD);
			res.put(XMLConstants.ATTRIBUTE_FIELD, getFirstField());
		} else {
			res.put(XMLConstants.ATTRIBUTE_FLOWTYPE, XMLConstants.VALUE_RETURN);
		}
		if(hasAccessPath()){
			res.put(XMLConstants.ATTRIBUTE_ACCESSPATH, accessPath.toString());
		}
		res.put(XMLConstants.ATTRIBUTE_TAINT_SUB_FIELDS, taintSubFields + "");
		return res;
	}


	@Override
	public boolean isThis() {
		if (isField() && getFirstField().equals(XMLConstants.VALUE_THIS_FIELD))
			return true;
		return false;
	}
	
	@Override
	public String toString(){
		if(isParamter()){
			return "Parameter: " + getParamterIndex() + " " + accessPath.toString() + " " +taintSubFields();
		}else if(isField()){
			return "Field " + accessPath.toString() + " " +taintSubFields();
		}else if(isReturn){
			return "Return " + accessPath.toString() + " " + taintSubFields();
		}else{
			return "invalid sink";
		}
	}
	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((accessPath == null) ? 0 : accessPath.hashCode());
		result = prime * result + (isReturn ? 1231 : 1237);
		result = prime * result + parameterIdx;
		result = prime * result + (taintSubFields ? 1231 : 1237);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DefaultFlowSink other = (DefaultFlowSink) obj;
		if (accessPath == null) {
			if (other.accessPath != null)
				return false;
		} else if (!accessPath.equals(other.accessPath))
			return false;
		if (isReturn != other.isReturn)
			return false;
		if (parameterIdx != other.parameterIdx)
			return false;
		if (taintSubFields != other.taintSubFields)
			return false;
		return true;
	}

	@Override
	public boolean hasAccessPath() {
		return accessPath != null && accessPath.hasAP(); 
	}

	
	@Override
	public boolean taintSubFields() {
		return taintSubFields;
	}

	@Override
	public String getFirstField() {
		return accessPath.getAP().get(0);
	}

	@Override
	public int getFieldCount() {
		return accessPath.getAPLength();
	}
	
}
