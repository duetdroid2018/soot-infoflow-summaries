package soot.jimple.infoflow.methodSummary.xml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import soot.jimple.infoflow.methodSummary.data.MethodSummaries;
import soot.jimple.infoflow.methodSummary.data.impl.DefaultMethodFlow;
import soot.jimple.infoflow.methodSummary.data.impl.FlowSinkFromXML;
import soot.jimple.infoflow.methodSummary.data.impl.FlowSourceFromXML;



public class XMLReader implements ISummaryReader{

	public MethodSummaries processXMLFile(String fileName) throws XMLStreamException, FileNotFoundException{
		return processXMLFile(new File(fileName));
	}
	
	/**
	 * Reads a summary file and returns a map<method sig s, all flows of method s> 
	 * @param fileName
	 * @return
	 * @throws XMLStreamException
	 * @throws FileNotFoundException
	 */
	public MethodSummaries processXMLFile(File fileName) throws XMLStreamException, FileNotFoundException{
		//TODO reimplement functionality 
		MethodSummaries summary = new MethodSummaries();
		
		InputStream in = new FileInputStream(fileName);
		XMLStreamReader reader = XMLInputFactory.newInstance().createXMLStreamReader(in);
		State state = State.init;
		String currentMethod = "";
		Map<String, String> fromAttributes = new HashMap<String,String>();
		Map<String, String> toAttributes = new HashMap<String,String>();
		//List<String> path = null;
		
		while(reader.hasNext()){
			reader.next();
			if(!reader.hasName())
				continue;
			if (reader.getLocalName().equals("method") && reader.isStartElement() ){
				state = State.method;
				currentMethod = getAttributeByName(reader, "id");
			}
			else if(state == State.method && reader.getLocalName().equals("flows") && reader.isStartElement())
				state = State.flows;
			else if(state == State.method && reader.getLocalName().equals("flows") && reader.isEndElement())
				state = State.method;
			else if(state == State.flows && reader.getLocalName().equals("flow") && reader.isStartElement()) {
				fromAttributes.clear();
				toAttributes.clear();
				state =  State.flow;
			}
			else if(state == State.flow && reader.getLocalName().equals("from") && reader.isStartElement()){
				for (int i = 0; i < reader.getAttributeCount(); i++)
					fromAttributes.put(reader.getAttributeLocalName(i), reader.getAttributeValue(i));
					state = State.to;
			}
			else if(state == State.to && reader.getLocalName().equals("to") && reader.isStartElement()){
				for (int i = 0; i < reader.getAttributeCount(); i++)
					toAttributes.put(reader.getAttributeLocalName(i), reader.getAttributeValue(i));
				state =  State.flow;
			}
			else if(state == State.flow && reader.getLocalName().equals("flow") && reader.isEndElement()){
				summary.addFlowForMethod(currentMethod, new DefaultMethodFlow(currentMethod,
						new FlowSourceFromXML(fromAttributes), new FlowSinkFromXML(toAttributes)));
				state = State.flows;
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

	private enum State {
		init, methods, method, flows, flow,to,from;
	}
	
}


