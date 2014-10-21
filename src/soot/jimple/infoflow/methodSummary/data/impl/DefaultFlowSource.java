package soot.jimple.infoflow.methodSummary.data.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import soot.SootField;
import soot.jimple.infoflow.methodSummary.data.FlowSource;
import soot.jimple.infoflow.methodSummary.data.SourceSinkType;
import soot.jimple.infoflow.methodSummary.xml.XMLConstants;

public class DefaultFlowSource extends FlowSource {



	public DefaultFlowSource(SourceSinkType tpye, int parameterIdx2, List<String> fields) {
		super(tpye,parameterIdx2,new SummaryAccessPath(fields));
	}
	

	private DefaultFlowSource(SourceSinkType tpye, int parameterIdx2, SummaryAccessPath ap) {
		super(tpye,parameterIdx2,ap);
	}
	
	public DefaultFlowSource createNewSource(SootField extension){
		SummaryAccessPath ap;
		ap = accessPath.extend(extension);
		return new DefaultFlowSource(this.type, this.parameterIdx, ap);
	}

	
	@Override
	public Map<String, String> xmlAttributes() {
		Map<String, String> res = new HashMap<String, String>();
		if (isParameter()) {
			res.put(XMLConstants.ATTRIBUTE_FLOWTYPE, XMLConstants.VALUE_PARAMETER);
			res.put(XMLConstants.ATTRIBUTE_PARAMTER_INDEX, getParameterIndex() + "");
		} else { // isField
			res.put(XMLConstants.ATTRIBUTE_FLOWTYPE, XMLConstants.VALUE_FIELD);
			res.put(XMLConstants.ATTRIBUTE_FIELD, "(this)");
		}
		if(hasAccessPath()){
			res.put(XMLConstants.ATTRIBUTE_ACCESSPATH, getAccessPath().toString());
		}
		return res;
	}

	@Override
	public String toString(){
		if(isParameter())
			return "Parameter " + getParameterIndex() + " " +  accessPath.toString();
		else if(isField())
			return "Field " + accessPath.toString();
		else if(isThis())
			return "THIS";
		else
			return "<unknown>";
	}
	
	
}
