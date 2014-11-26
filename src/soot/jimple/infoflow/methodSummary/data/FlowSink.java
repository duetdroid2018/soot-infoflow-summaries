package soot.jimple.infoflow.methodSummary.data;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import soot.jimple.infoflow.methodSummary.xml.XMLConstants;

/**
 * Representation of a flow sink.
 * 
 * @author Steven Arzt
 */
public class FlowSink extends AbstractFlowSinkSource {
	private final boolean taintSubFields;
	
	public FlowSink(SourceSinkType type, int paramterIdx,
			String[] fields, boolean taintSubFields) {
		super(type, paramterIdx, fields);
		this.taintSubFields = taintSubFields;
	}
	
	public FlowSink(SourceSinkType type, int paramterIdx,
			boolean taintSubFields) {
		super(type, paramterIdx, null);
		this.taintSubFields = taintSubFields;
	}
	
	public boolean isReturn(){
		return type().equals(SourceSinkType.Return);
	}
	
	public boolean taintSubFields(){
		return taintSubFields;
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
		if (isParameter())
			return "Parameter " + getParameterIndex() + (accessPath == null ? "" : " "
					+ accessPath.toString()) + " " + taintSubFields();
		
		if (isField())
			return "Field" + (accessPath == null ? "" : " "
					+ Arrays.toString(accessPath)) + " " + taintSubFields();
		
		if(isReturn())
			return "Return" + (accessPath == null ? "" : " "
					+ Arrays.toString(accessPath)) + " " + taintSubFields();
		
		return "invalid sink";
	}	
	
}
