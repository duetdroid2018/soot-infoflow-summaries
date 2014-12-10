package soot.jimple.infoflow.methodSummary.data;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import soot.jimple.infoflow.methodSummary.xml.XMLConstants;

/**
 * Representation of a flow source
 * 
 * @author Steven Arzt
 */
public class FlowSource extends AbstractFlowSinkSource {

	public FlowSource(SourceSinkType type) {
		super(type, -1, null);
	}
	
	public FlowSource(SourceSinkType type, int parameterIdx) {
		super(type, parameterIdx, null);
	}
	
	public FlowSource(SourceSinkType type, int parameterIdx, String[] fields) {
		super(type, parameterIdx, fields);
	}
	
	@Override
	public Map<String, String> xmlAttributes() {
		Map<String, String> res = new HashMap<String, String>();
		if (isParameter()) {
			res.put(XMLConstants.ATTRIBUTE_FLOWTYPE, XMLConstants.VALUE_PARAMETER);
			res.put(XMLConstants.ATTRIBUTE_PARAMTER_INDEX, getParameterIndex() + "");
		}
		else if (isField()) {
			res.put(XMLConstants.ATTRIBUTE_FLOWTYPE, XMLConstants.VALUE_FIELD);
			res.put(XMLConstants.ATTRIBUTE_FIELD, "(this)");
		}
		else
			throw new RuntimeException("Invalid source type");
		
		if(hasAccessPath())
			res.put(XMLConstants.ATTRIBUTE_ACCESSPATH, getAccessPath().toString());
		
		return res;
	}
	
	@Override
	public String toString(){
		if(isParameter())
			return "Parameter " + getParameterIndex() + (accessPath == null ? "" : " "
					+  Arrays.toString(accessPath));
		
		if(isField())
			return "Field" + (accessPath == null ? "" : " " + Arrays.toString(accessPath));
		
		if(isThis())
			return "THIS";
		
		return "<unknown>";
	}

	//a this source is a field source with apl = 0
	public boolean isThis()
	{
		return type().equals(SourceSinkType.Field) && !hasAccessPath();
	}
}
