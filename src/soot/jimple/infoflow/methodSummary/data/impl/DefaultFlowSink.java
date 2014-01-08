package soot.jimple.infoflow.methodSummary.data.impl;

import java.util.LinkedList;
import java.util.List;

import soot.SootField;
import soot.SootMethod;
import soot.jimple.infoflow.methodSummary.data.AbstractFlowSink;
import soot.jimple.infoflow.methodSummary.data.Tuple;
import soot.jimple.infoflow.methodSummary.xml.XMLConstants;

public class DefaultFlowSink extends AbstractFlowSink {
	private final int paraIdx;
	private final boolean isReturn;
	private final String paraTyp;
	private final String field;
	private final String returnLocal;
	private final String accessPath;
	private final boolean taintSubFields;
	



	public DefaultFlowSink(SootField ap, boolean taintSubFields) {
		this.field = null;
		if(ap == null)
			accessPath = null;
		else
			accessPath = ap.toString();
		isReturn = true;
		paraIdx = -1;
		paraTyp = null;
		returnLocal = null;
		this.taintSubFields = taintSubFields;
	}

	public DefaultFlowSink(SootField field2, SootField ap, boolean taintSubFields) {
		this.field = field2.toString();
		if(ap == null)
			accessPath = null;
		else
			accessPath = ap.toString();
		isReturn = false;
		paraIdx = -1;
		paraTyp = null;
		returnLocal = null;
		this.taintSubFields = taintSubFields;
	}

	public DefaultFlowSink(SootMethod m, int paraIdx2, SootField ap, boolean taintSubFields) {
		this.field = null;
		if(ap == null)
			accessPath = null;
		else
			accessPath = ap.toString();
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
	public List<Tuple<String, String>> xmlAttributes() {
		List<Tuple<String, String>> res = new LinkedList<Tuple<String, String>>();
		if (isParamter()) {
			res.add(new Tuple<String, String>(XMLConstants.ATTRIBUTE_FLOWTYPE, XMLConstants.VALUE_PARAMETER));
			res.add(new Tuple<String, String>(XMLConstants.ATTRIBUTE_PARAMTER_INDEX, getParamterIndex() + ""));
			res.add(new Tuple<String, String>(XMLConstants.ATTRIBUTE_PARAMTER_TYPE, getParaType()));		
		} else if (isField()) {
			res.add(new Tuple<String, String>(XMLConstants.ATTRIBUTE_FLOWTYPE, XMLConstants.VALUE_FIELD));
			res.add(new Tuple<String, String>(XMLConstants.ATTRIBUTE_FIELD, getField()));
		} else {
			res.add(new Tuple<String, String>(XMLConstants.ATTRIBUTE_FLOWTYPE, XMLConstants.VALUE_RETURN));
			if(returnLocal == null){
				res.add(new Tuple<String, String>(XMLConstants.ATTRIBUTE_RETURN_LOCAL, ""));
			}else{
				res.add(new Tuple<String, String>(XMLConstants.ATTRIBUTE_RETURN_LOCAL, returnLocal));
			}
						
		}
		if(hasAccessPath()){
			res.add(new Tuple<String, String>(XMLConstants.ATTRIBUTE_ACCESSPATH, getAccessPath()));
		}
		res.add(new Tuple<String, String>(XMLConstants.ATTRIBUTE_TAINT_SUB_FIELDS, taintSubFields + ""));
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
		for(Tuple<String,String> t : xmlAttributes()){
			buf.append(t._2 + " ");
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
		return accessPath != null && accessPath.length() >0; 
	}

	@Override
	public String getAccessPath() {
		return accessPath;
	}

	@Override
	public boolean taintSubFields() {
		return taintSubFields;
	}


	
}
