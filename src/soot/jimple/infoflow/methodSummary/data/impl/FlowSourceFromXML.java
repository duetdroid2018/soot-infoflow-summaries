package soot.jimple.infoflow.methodSummary.data.impl;

import java.util.List;

import soot.jimple.infoflow.methodSummary.data.AbstractFlowSource;
import soot.jimple.infoflow.methodSummary.data.Tuple;
import soot.jimple.infoflow.methodSummary.xml.XMLConstants;

public class FlowSourceFromXML extends AbstractFlowSource {
	private List<Tuple<String, String>> attribues;

	public FlowSourceFromXML(List<Tuple<String, String>> s) {
		this.attribues = s;
	}

	@Override
	public boolean isParamter() {
		return getAttribut(XMLConstants.ATTRIBUTE_FLOWTYPE)._2.equals(XMLConstants.VALUE_PARAMETER); 
	}

	private Tuple<String, String> getAttribut(String s) {
		for (Tuple<String, String> t : attribues) {
			if (t._1.equals(s)) {
				return t;
			}
		}
		return null;
	}

	@Override
	public int getParamterIndex() {
		return Integer.parseInt(getAttribut(XMLConstants.ATTRIBUTE_PARAMTER_INDEX)._2);
	}

	@Override
	public boolean isField() {
		return getAttribut(XMLConstants.ATTRIBUTE_FLOWTYPE)._2.equals(XMLConstants.VALUE_FIELD); 
	}

	@Override
	public String getField() {
		return getAttribut(XMLConstants.ATTRIBUTE_FIELD)._2;
	}


	@Override
	public String getParaType() {
		return getAttribut(XMLConstants.ATTRIBUTE_PARAMTER_TYPE)._2;
	}


	@Override
	public List<Tuple<String, String>> xmlAttributes() {
		return attribues;
	}
	
	
	@Override
	public String toString(){
		String res = "";
		for(Tuple<String, String> t : attribues){
			res = res + "(" + t._1 + ";" + t._2 +")";
		}
		return res;
		
	}

	@Override
	public boolean isThis() {
		return isField() && getField().equals(XMLConstants.VALUE_THIS_FIELD);
	}

	@Override
	public boolean hasAccessPath() {
		return getAttribut(XMLConstants.ATTRIBUTE_ACCESSPATH) != null;
	}

	@Override
	public String getAccessPath() {
		return getAttribut(XMLConstants.ATTRIBUTE_ACCESSPATH)._2;
	}


}
