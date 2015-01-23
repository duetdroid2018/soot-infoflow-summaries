package soot.jimple.infoflow.methodSummary.xml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import soot.jimple.infoflow.methodSummary.data.FlowSink;
import soot.jimple.infoflow.methodSummary.data.FlowSource;
import soot.jimple.infoflow.methodSummary.data.MethodFlow;
import soot.jimple.infoflow.methodSummary.data.SourceSinkType;
import soot.jimple.infoflow.methodSummary.data.summary.MethodSummaries;
import static soot.jimple.infoflow.methodSummary.xml.XMLConstants.*;


public class XMLReader {
	
	enum State{
		methods, method, flow
	}

	/**
	 * Reads a summary xml file and returns the MethodSummaries which are saved in that file 
	 */
	public MethodSummaries read(File fileName) throws XMLStreamException, FileNotFoundException, SummaryXMLException{
		MethodSummaries summary = new MethodSummaries();
		
		InputStream in = new FileInputStream(fileName);
		XMLStreamReader reader = XMLInputFactory.newInstance().createXMLStreamReader(in);
		String currentMethod = "";
		Map<String, String> sourceAttributes = new HashMap<String,String>();
		Map<String, String> sinkAttributes = new HashMap<String,String>();
		State state = State.methods;
		
		while(reader.hasNext()){
			reader.next();

			if(!reader.hasName())
				continue;
			if (reader.getLocalName().equals(TREE_METHOD) && reader.isStartElement() ){
				if(state == State.methods){
					currentMethod = getAttributeByName(reader, ATTRIBUT_METHOD_SIG);
					state = State.method;
				}			
				else
					throw new SummaryXMLException();
			}else if(reader.getLocalName().equals(TREE_METHOD) && reader.isEndElement() ){
				if(state == State.method)
					state = State.methods;
				else
					throw new SummaryXMLException();
			}
			else if(reader.getLocalName().equals(TREE_FLOW) && reader.isStartElement()) {
				if(state == State.method){
					sourceAttributes = new HashMap<String,String>();
					sinkAttributes = new HashMap<String,String>();
					state = State.flow;
				}else{
					throw new SummaryXMLException();
				}
					
			}
			else if(reader.getLocalName().equals(TREE_SOURCE) && reader.isStartElement()){
				if(state == State.flow){
				for (int i = 0; i < reader.getAttributeCount(); i++)
					sourceAttributes.put(reader.getAttributeLocalName(i), reader.getAttributeValue(i));
				}else{
					throw new SummaryXMLException();
				}
			}
			else if(reader.getLocalName().equals(TREE_SINK) && reader.isStartElement()){
				if(state == State.flow){
				for (int i = 0; i < reader.getAttributeCount(); i++)
					sinkAttributes.put(reader.getAttributeLocalName(i), reader.getAttributeValue(i));
				}else{
					throw new SummaryXMLException();
				}
			}
			else if(reader.getLocalName().equals(TREE_FLOW) && reader.isEndElement()){
				if(state == State.flow){
					state = State.method;
					summary.addFlowForMethod(currentMethod, new MethodFlow(currentMethod,					
						createSource(sourceAttributes), createSink(sinkAttributes)));
				
				}else{
					throw new SummaryXMLException();
				}
			}
		}
		return summary;
	}


	/**
	 * Gets the value of the XML attribute with the specified id
	 * @param reader The reader from which to get the XML data
	 * @param id The attribute id for which to get the data
	 * @return The data of the given attribute if it exists, otherwise an
	 * empty string
	 */
	private String getAttributeByName(XMLStreamReader reader, String id) {
		for (int i = 0; i < reader.getAttributeCount(); i++)
			if (reader.getAttributeLocalName(i).equals(id))
				return reader.getAttributeValue(i);
		return "";
	}
	
	public FlowSource createSource(Map<String, String> attributes) throws SummaryXMLException{
		if(isField(attributes)){
			return new FlowSource(SourceSinkType.Field ,getAccessPath(attributes));
		}else if(isParameter(attributes)){
			return new FlowSource(SourceSinkType.Parameter, paramterIdx(attributes), getAccessPath(attributes));
		}else if(isMethod(attributes)){
			//TODO not yet specified
		}
		throw new SummaryXMLException();
	}
	public FlowSink createSink(Map<String, String> attributes) throws SummaryXMLException{
		if(isField(attributes)){
			return new FlowSink(SourceSinkType.Field, getAccessPath(attributes),taintSubFields(attributes));
		}else if(isParameter(attributes)){
			return new FlowSink(SourceSinkType.Parameter, paramterIdx(attributes), getAccessPath(attributes), taintSubFields(attributes));
		}else if(isReturn(attributes)){
			return new FlowSink(SourceSinkType.Return, getAccessPath(attributes),taintSubFields(attributes));
		}else if(isMethod(attributes)){
			//TODO not yet specified
		}
		throw new SummaryXMLException();
	}
	
	private boolean isMethod(Map<String, String> attributes){
		return attributes.get(ATTRIBUTE_FLOWTYPE).equals(SourceSinkType.MethodCall.toString());
	}
	
	private boolean isReturn(Map<String, String> attributes){
		return attributes.get(ATTRIBUTE_FLOWTYPE).equals(SourceSinkType.Return.toString());
	}
	
	private boolean isField(Map<String, String> attributes){
		return attributes.get(ATTRIBUTE_FLOWTYPE).equals(SourceSinkType.Field.toString());
	}
	private String[] getAccessPath(Map<String, String> attributes){
		if(attributes.containsKey(XMLConstants.ATTRIBUTE_ACCESSPATH)){

			if(attributes.get(ATTRIBUTE_ACCESSPATH).length() > 3){
				 String[] res = attributes.get(ATTRIBUTE_ACCESSPATH).substring(1, attributes.get(ATTRIBUTE_ACCESSPATH).length()-1).split(",");
				 for(int i =0; i < res.length; i++)
					 res[i] = res[i].trim();
			}
				
		}
		return null;
	}
	
	private boolean isParameter(Map<String, String> attributes){
		return attributes.get(ATTRIBUTE_FLOWTYPE).equals(SourceSinkType.Parameter.toString());
	}
	
	private int paramterIdx(Map<String, String> attributes){
		return  Integer.parseInt(attributes.get(ATTRIBUTE_PARAMTER_INDEX));
	}
	private boolean taintSubFields(Map<String, String> attributes){
		String val = attributes.get(ATTRIBUTE_TAINT_SUB_FIELDS);
		return val != null && val.equals(VALUE_TRUE);
	}
}


