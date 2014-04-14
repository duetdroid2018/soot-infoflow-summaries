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
	private final SummaryAccessPath accessPath;
	private final boolean thisFlow;


	/**
	 * creates a parameter source
	 * @param m
	 * @param parameterIdx2
	 * @param fields
	 */
	public FlowSourceForSummary(SootMethod m, int parameterIdx2, List<SootField> fields) {
		parameterIdx = parameterIdx2;
		thisFlow = false;
		accessPath = new SummaryAccessPath(fields);
	}

	/**
	 * creates a field source
	 * obj.f
	 * @param f
	 * @param ap
	 */
	public FlowSourceForSummary(List<SootField> f) {
		parameterIdx = -1;
		thisFlow = false;
		accessPath = new SummaryAccessPath(f);
	}

	/**
	 * creates a this flow source
	 */
	public FlowSourceForSummary() {
		parameterIdx = -1;
		thisFlow = true;
		accessPath = new SummaryAccessPath();
	}
	
	private FlowSourceForSummary(int paramterIdx, SummaryAccessPath ap, boolean thisFlow){
		this.parameterIdx = paramterIdx;
		this.accessPath = ap;
		this.thisFlow = thisFlow;
	}

	
	public FlowSourceForSummary createNewSource(SootField extension){
		SummaryAccessPath ap;
		ap = accessPath.extend(extension);
		return new FlowSourceForSummary(this.parameterIdx,ap, this.thisFlow);
		
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
		return !isParamter() && accessPath != null && accessPath.hasAP();
	}

	
	@Override
	public Map<String, String> xmlAttributes() {
		Map<String, String> res = new HashMap<String, String>();
		if (isParamter()) {
			res.put(XMLConstants.ATTRIBUTE_FLOWTYPE, XMLConstants.VALUE_PARAMETER);
			res.put(XMLConstants.ATTRIBUTE_PARAMTER_INDEX, getParamterIndex() + "");
		} else {
			res.put(XMLConstants.ATTRIBUTE_FLOWTYPE, XMLConstants.VALUE_FIELD);
			res.put(XMLConstants.ATTRIBUTE_FIELD, getFirstField());
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
			buf.append("Paramter " + getParamterIndex() + " " +  accessPath.toString());
		}
		if(isField()){
			buf.append("Field " + accessPath.toString());
		}
		if(isThis()){
			buf.append("THIS");
		}
		return buf.toString();
	}
	


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((accessPath == null) ? 0 : accessPath.hashCode());
		result = prime * result + parameterIdx;
		result = prime * result + (thisFlow ? 1231 : 1237);
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
		FlowSourceForSummary other = (FlowSourceForSummary) obj;
		if (accessPath == null) {
			if (other.accessPath != null)
				return false;
		} else if (!accessPath.equals(other.accessPath))
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
	public List<String> getFields() {
		return accessPath.getAP();
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
