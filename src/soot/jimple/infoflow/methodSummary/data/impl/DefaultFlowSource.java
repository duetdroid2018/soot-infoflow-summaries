package soot.jimple.infoflow.methodSummary.data.impl;

import java.util.LinkedList;
import java.util.List;

import soot.SootField;
import soot.SootMethod;
import soot.jimple.infoflow.methodSummary.data.AbstractFlowSource;
import soot.jimple.infoflow.methodSummary.data.Tuple;
import soot.jimple.infoflow.methodSummary.xml.XMLConstants;

public class DefaultFlowSource extends AbstractFlowSource {

	private final int parameterIdx;
	private final String field;
	private final String paraTyp;
	private final boolean thisFlow;
	private final String accessPath;


	public DefaultFlowSource(SootMethod m, int parameterIdx2, SootField ap) {
		parameterIdx = parameterIdx2;
		field = null;
		paraTyp = m.getParameterTypes().get(getParamterIndex()).toString();
		thisFlow = false;
		if(ap == null)
			accessPath = null;
		else
			accessPath = ap.toString();
				
		
	}

	public DefaultFlowSource(SootField f, SootField ap) {
		parameterIdx = -1;
		field = f.toString();
		paraTyp = null;
		thisFlow = false;
		if(ap == null)
			accessPath = null;
		else
			accessPath = ap.toString();
	}
	
	public DefaultFlowSource() {
		parameterIdx = -1;
		field = "THIS";
		paraTyp = null;
		thisFlow = true;
		accessPath = null;
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
		return !isParamter() && field != null && !thisFlow;
	}

	@Override
	public String getParaType() {
		if (getParamterIndex() == -1)
			return "failed2";
		return paraTyp;
	}
	@Override
	public String getField() {
		return field;
	}

	@Override
	public List<Tuple<String, String>> xmlAttributes() {
		List<Tuple<String, String>> res = new LinkedList<Tuple<String, String>>();
		if (isParamter()) {
			res.add(new Tuple<String, String>(XMLConstants.ATTRIBUTE_FLOWTYPE, XMLConstants.VALUE_PARAMETER));
			res.add(new Tuple<String, String>(XMLConstants.ATTRIBUTE_PARAMTER_INDEX, getParamterIndex() + ""));
			res.add(new Tuple<String, String>(XMLConstants.ATTRIBUTE_PARAMTER_TYPE, getParaType()));
		} else {	
			res.add(new Tuple<String, String>(XMLConstants.ATTRIBUTE_FLOWTYPE, XMLConstants.VALUE_FIELD));
			res.add(new Tuple<String, String>(XMLConstants.ATTRIBUTE_FIELD, getField()));
		}
		
		if(hasAccessPath()){
			res.add(new Tuple<String, String>(XMLConstants.ATTRIBUTE_ACCESSPATH, getAccessPath()));
		}
		return res;
	}

	@Override
	public boolean isThis() {
		return thisFlow;
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
		result = prime * result + ((paraTyp == null) ? 0 : paraTyp.hashCode());
		result = prime * result + parameterIdx;
		result = prime * result + (thisFlow ? 1231 : 1237);
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
		DefaultFlowSource other = (DefaultFlowSource) obj;
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
		if (paraTyp == null) {
			if (other.paraTyp != null)
				return false;
		} else if (!paraTyp.equals(other.paraTyp))
			return false;
		if (parameterIdx != other.parameterIdx)
			return false;
		if (thisFlow != other.thisFlow)
			return false;
		return true;
	}

	@Override
	public boolean hasAccessPath() {
		return accessPath != null;
	}

	@Override
	public String getAccessPath() {
		return accessPath;
	}

	




	
}
