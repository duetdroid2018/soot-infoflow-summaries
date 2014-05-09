package soot.jimple.infoflow.methodSummary.data.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import soot.jimple.infoflow.methodSummary.data.FlowSink;
import soot.jimple.infoflow.methodSummary.data.SourceSinkType;
import soot.jimple.infoflow.methodSummary.xml.XMLConstants;


public class DefaultFlowSink extends FlowSink {
	

	public DefaultFlowSink(SourceSinkType type, int paraIdx, List<String> fields, boolean taintSubFields) {
		super(type,paraIdx,new SummaryAccessPath(fields),taintSubFields);
	}

		
	@Override
	public Map<String, String> xmlAttributes() {
		Map<String, String> res = new HashMap<String, String>();
		if (isParameter()) {
			res.put(XMLConstants.ATTRIBUTE_FLOWTYPE, XMLConstants.VALUE_PARAMETER);
			res.put(XMLConstants.ATTRIBUTE_PARAMTER_INDEX, getParameterIndex() + "");
		} else if (isField()) {
			res.put(XMLConstants.ATTRIBUTE_FLOWTYPE, XMLConstants.VALUE_FIELD);
			res.put(XMLConstants.ATTRIBUTE_FIELD, "(this)");
		} else {
			res.put(XMLConstants.ATTRIBUTE_FLOWTYPE, XMLConstants.VALUE_RETURN);
		}
		if(hasAccessPath()){
			res.put(XMLConstants.ATTRIBUTE_ACCESSPATH, getAccessPath().toString());
		}
		res.put(XMLConstants.ATTRIBUTE_TAINT_SUB_FIELDS, taintSubFields() + "");
		return res;
	}


		
	@Override
	public String toString(){
		if(isParameter()){
			return "Parameter: " + getParameterIndex() + " " + getAccessPath().toString() + " " +taintSubFields();
		}else if(isField()){
			return "Field " + getAccessPath().toString() + " " +taintSubFields();
		}else if(isReturn()){
			return "Return " + getAccessPath().toString() + " " + taintSubFields();
		}else{
			return "invalid sink";
		}
	}	
}
