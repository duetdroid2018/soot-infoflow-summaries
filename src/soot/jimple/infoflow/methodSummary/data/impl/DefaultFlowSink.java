package soot.jimple.infoflow.methodSummary.data.impl;

import java.util.HashMap;
import java.util.Map;

import soot.SootField;
import soot.SootMethod;
import soot.jimple.infoflow.methodSummary.data.IFlowSink;
import soot.jimple.infoflow.methodSummary.xml.XMLConstants;

public class DefaultFlowSink implements IFlowSink {
	private final int paraIdx;
	private final boolean isReturn;
	private final String paraTyp;
	private final String field;
	private final String returnLocal;
	private final SummaryAccessPath accessPath;
	private final boolean taintSubFields;
	
	public DefaultFlowSink(SootField ap, boolean taintSubFields) {
		this.field = null;
		accessPath = new SummaryAccessPath(ap);
		isReturn = true;
		paraIdx = -1;
		paraTyp = null;
		returnLocal = null;
		this.taintSubFields = taintSubFields;
	}

	public DefaultFlowSink(SootField field2, SootField ap, boolean taintSubFields) {
		this.field = field2.toString();
		accessPath = new SummaryAccessPath(ap);
		isReturn = false;
		paraIdx = -1;
		paraTyp = null;
		returnLocal = null;
		this.taintSubFields = taintSubFields;
	}

	public DefaultFlowSink(SootMethod m, int paraIdx2, SootField ap, boolean taintSubFields) {
		this.field = null;
		accessPath = new SummaryAccessPath(ap);
		isReturn = false;
		paraIdx = paraIdx2;
		paraTyp = m.getParameterType(getParamterIndex()).toString();
		returnLocal = null;
		this.taintSubFields = taintSubFields;
	}

	@Override
	public boolean isParamter() {
		return paraIdx >= 0;
	}

	@Override
	public int getParamterIndex() {
		return paraIdx;
	}

	@Override
	public boolean isField() {
		return !isParamter() && !isReturn() && field != null;
	}

	@Override
	public String getParaType() {
		if (getParamterIndex() == -1)
			return "failed";
		return paraTyp;
	}

	@Override
	public String getField() {
		return field;
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
			res.put(XMLConstants.ATTRIBUTE_PARAMTER_TYPE, getParaType());		
		} else if (isField()) {
			res.put(XMLConstants.ATTRIBUTE_FLOWTYPE, XMLConstants.VALUE_FIELD);
			res.put(XMLConstants.ATTRIBUTE_FIELD, getField());
		} else {
			res.put(XMLConstants.ATTRIBUTE_FLOWTYPE, XMLConstants.VALUE_RETURN);
			if(returnLocal != null)
				res.put(XMLConstants.ATTRIBUTE_RETURN_LOCAL, returnLocal);
		}
		if(hasAccessPath()){
			res.put(XMLConstants.ATTRIBUTE_ACCESSPATH, accessPath.toString());
		}
		res.put(XMLConstants.ATTRIBUTE_TAINT_SUB_FIELDS, taintSubFields + "");
		return res;
	}


	@Override
	public boolean isThis() {
		if (isField() && getField().equals(XMLConstants.VALUE_THIS_FIELD))
			return true;
		return false;
	}
	
	@Override
	public String toString(){
		StringBuffer buf = new StringBuffer();
		for(String t : xmlAttributes().values()){
			buf.append(t + " ");
		}
		return buf.toString();
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((accessPath == null) ? 0 : accessPath.hashCode());
		result = prime * result + ((field == null) ? 0 : field.hashCode());
		result = prime * result + (isReturn ? 1231 : 1237);
		result = prime * result + paraIdx;
		result = prime * result + ((paraTyp == null) ? 0 : paraTyp.hashCode());
		result = prime * result + ((returnLocal == null) ? 0 : returnLocal.hashCode());
		result = prime * result + (taintSubFields ? 1231 : 1237);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
//		if (!super.equals(obj))
//			return false;
		if (getClass() != obj.getClass())
			return false;
		DefaultFlowSink other = (DefaultFlowSink) obj;
		if (accessPath == null) {
			if (other.accessPath != null)
				return false;
		} else if (!accessPath.equals(other.accessPath))
			return false;
		if (field == null) {
			if (other.field != null)
				return false;
		} else if (!field.equals(other.field))
			return false;
		if (isReturn != other.isReturn)
			return false;
		if (paraIdx != other.paraIdx)
			return false;
		if (paraTyp == null) {
			if (other.paraTyp != null)
				return false;
		} else if (!paraTyp.equals(other.paraTyp))
			return false;
		if (returnLocal == null) {
			if (other.returnLocal != null)
				return false;
		} else if (!returnLocal.equals(other.returnLocal))
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
	public SummaryAccessPath getAccessPath() {
		return accessPath;
	}

	@Override
	public boolean taintSubFields() {
		return taintSubFields;
	}
	
}
