package soot.jimple.infoflow.methodSummary.data;

import java.util.HashMap;
import java.util.List;
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
			SummaryAccessPath accessPath, boolean taintSubFields) {
		super(type, paramterIdx, accessPath);
		this.taintSubFields = taintSubFields;
	}
	
	public FlowSink(SourceSinkType type, int paramterIdx,
			List<String> fields, boolean taintSubFields) {
		super(type, paramterIdx, fields);
		this.taintSubFields = taintSubFields;
	}
	
	public FlowSink(SourceSinkType type, int paramterIdx,
			boolean taintSubFields) {
		super(type, paramterIdx, (SummaryAccessPath) null);
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
		if(isParameter()){
			return "Parameter " + getParameterIndex() + (accessPath == null ? "" : " "
					+ accessPath.toString()) + " " + taintSubFields();
		}else if(isField()){
			return "Field" + (accessPath == null ? "" : " " + accessPath.toString()) + " " + taintSubFields();
		}else if(isReturn()){
			return "Return" + (accessPath == null ? "" : " " + getAccessPath().toString()) + " " + taintSubFields();
		}else{
			return "invalid sink";
		}
	}	
	
}
