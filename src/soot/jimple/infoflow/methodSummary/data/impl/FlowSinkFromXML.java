package soot.jimple.infoflow.methodSummary.data.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import soot.jimple.infoflow.methodSummary.data.IFlowSink;
import soot.jimple.infoflow.methodSummary.xml.XMLConstants;

public class FlowSinkFromXML implements IFlowSink {
	private Map<String, String> attributes;
	private List<String> fields = new ArrayList<String>();
	
	public FlowSinkFromXML(Map<String, String> s) {
		this.attributes = s;
		if(isField()){
			fields.add(getFirstField());
		}
		if(hasAccessPath()){
			fields.addAll(Arrays.asList(attributes.get(XMLConstants.ATTRIBUTE_ACCESSPATH).split(".")));
		}
		
	}

	@Override
	public boolean isParamter() {
		return attributes.get(XMLConstants.ATTRIBUTE_FLOWTYPE).equals(XMLConstants.VALUE_PARAMETER); 
	}
	
	@Override
	public int getParamterIndex() {
		return Integer.parseInt(attributes.get(XMLConstants.ATTRIBUTE_PARAMTER_INDEX));
	}

	@Override
	public boolean isField() {
		return attributes.get(XMLConstants.ATTRIBUTE_FLOWTYPE).equals(XMLConstants.VALUE_FIELD); 
	}

	@Override
	public String getFirstField() {
		return attributes.get(XMLConstants.ATTRIBUTE_FIELD);
	}
	
	@Override
	public Map<String, String> xmlAttributes() {
		return attributes;
	}
	
	@Override
	public String toString(){
		String res = "";
		for(Entry<String, String> t : attributes.entrySet())
			res = res + "(" + t.getKey() + ";" + t.getValue() +")";
		return res;
	}

	@Override
	public boolean isThis() {
		return isField() && getFirstField().equals(XMLConstants.VALUE_THIS_FIELD);
	}

	@Override
	public boolean isReturn() {
		return attributes.get(XMLConstants.ATTRIBUTE_FLOWTYPE).equals(XMLConstants.VALUE_RETURN); 
	}

	@Override
	public boolean hasAccessPath() {
		return attributes.containsKey(XMLConstants.ATTRIBUTE_ACCESSPATH);
	}

//	@Override
//	public SummaryAccessPath getAccessPath() {
//		return new SummaryAccessPath(attributes.get(XMLConstants.ATTRIBUTE_ACCESSPATH));
//	}

	@Override
	public boolean taintSubFields() {
		String val = attributes.get(XMLConstants.ATTRIBUTE_TAINT_SUB_FIELDS);
		return val == null || !val.equals("false");
	}

	@Override
	public List<String> getFields() {
		return fields;
	}

	@Override
	public int getFieldCount() {
		return fields.size();
	}


}
