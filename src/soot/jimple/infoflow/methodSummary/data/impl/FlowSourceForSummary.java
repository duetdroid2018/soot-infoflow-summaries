package soot.jimple.infoflow.methodSummary.data.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import soot.SootField;
import soot.SootMethod;
import soot.jimple.infoflow.methodSummary.data.IFlowSource;
import soot.jimple.infoflow.methodSummary.xml.XMLConstants;

public class FlowSourceForSummary implements IFlowSource {

	private final int parameterIdx;
	private final String field;
	private final String paraTyp;
	private final boolean thisFlow;
	private final SummaryAccessPath accessPath;


	public FlowSourceForSummary(SootMethod m, int parameterIdx2, SootField ap) {
		parameterIdx = parameterIdx2;
		field = null;
		paraTyp = m.getParameterTypes().get(getParamterIndex()).toString();
		thisFlow = false;
		accessPath = new SummaryAccessPath(ap);
	}

	public FlowSourceForSummary(SootField f, SootField ap) {
		parameterIdx = -1;
		field = f.toString();
		paraTyp = null;
		thisFlow = false;
		accessPath = new SummaryAccessPath(ap);
	}
	
	public FlowSourceForSummary() {
		parameterIdx = -1;
		field = null;
		paraTyp = null;
		thisFlow = true;
		accessPath = null;
	}
	private FlowSourceForSummary(int paraIdx, String field, String paraTyp, boolean thisFlow, SummaryAccessPath ap){
		this.parameterIdx = paraIdx;
		this.field = field;
		this.paraTyp = paraTyp;
		this.thisFlow = thisFlow;
		this.accessPath = ap;
	}
	
	public FlowSourceForSummary createNewSource(SootField extension){
		
		SummaryAccessPath ap;
		if(accessPath == null){
			ap = new SummaryAccessPath(extension);
		}else{
			ap = accessPath.extend(extension);
		}
		if(isThis()){
			return new FlowSourceForSummary(this.parameterIdx,extension.toString(), this.paraTyp, false, new SummaryAccessPath());
		}else{
			return new FlowSourceForSummary(this.parameterIdx,this.field, this.paraTyp, this.thisFlow, ap);
		}
		
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
		return paraTyp;
	}
	@Override
	public String getField() {
		return field;
	}

	@Override
	public Map<String, String> xmlAttributes() {
		Map<String, String> res = new HashMap<String, String>();
		if (isParamter()) {
			res.put(XMLConstants.ATTRIBUTE_FLOWTYPE, XMLConstants.VALUE_PARAMETER);
			res.put(XMLConstants.ATTRIBUTE_PARAMTER_INDEX, getParamterIndex() + "");
			res.put(XMLConstants.ATTRIBUTE_PARAMTER_TYPE, getParaType());
		} else {
			res.put(XMLConstants.ATTRIBUTE_FLOWTYPE, XMLConstants.VALUE_FIELD);
			res.put(XMLConstants.ATTRIBUTE_FIELD, getField());
		}
		
		if(hasAccessPath())
			res.put(XMLConstants.ATTRIBUTE_ACCESSPATH, accessPath.toString());
		
		return res;
	}

	@Override
	public boolean isThis() {
		return thisFlow;
	}
	@Override
	public String toString(){
		StringBuffer buf = new StringBuffer();
		if(isParamter()){
			buf.append("Paramter " + getParamterIndex() + " " + getParaType());
		}
		if(isField()){
			buf.append("Field " + getField());
		}
		if(isThis()){
			buf.append("THIS");
		}
		if(hasAccessPath()){
			buf.append(" " + accessPath.toString());
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
		FlowSourceForSummary other = (FlowSourceForSummary) obj;
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
		return accessPath != null && accessPath.hasAP();
	}

	@Override
	public SummaryAccessPath getAccessPath() {
		return accessPath;
	}

}
