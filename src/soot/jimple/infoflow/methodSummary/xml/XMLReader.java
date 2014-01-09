package soot.jimple.infoflow.methodSummary.xml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import soot.jimple.infoflow.methodSummary.data.AbstractMethodFlow;
import soot.jimple.infoflow.methodSummary.data.impl.DefaultMethodFlow;
import soot.jimple.infoflow.methodSummary.data.impl.FlowSinkFromXML;
import soot.jimple.infoflow.methodSummary.data.impl.FlowSourceFromXML;



public class XMLReader implements ISummaryReader{

	public Map<String, Set<AbstractMethodFlow>> processXMLFile(String fileName) throws XMLStreamException, FileNotFoundException{
		return processXMLFile(new File(fileName));
	}
	
	/**
	 * Reads a summary file and returns a map<method sig s, all flows of method s> 
	 * @param fileName
	 * @return
	 * @throws XMLStreamException
	 * @throws FileNotFoundException
	 */
	public Map<String, Set<AbstractMethodFlow>> processXMLFile(File fileName) throws XMLStreamException, FileNotFoundException{

		Map<String, Set<AbstractMethodFlow>> summary = new HashMap<String, Set<AbstractMethodFlow>>();
		InputStream in = new FileInputStream(fileName);
		XMLInputFactory factory = XMLInputFactory.newInstance();
		XMLStreamReader reader = factory.createXMLStreamReader(in);
		State state = State.init;
		String currentMethod = "";
		Map<String, String> fromAttributes = new HashMap<String,String>();
		Map<String, String> toAttributes = new HashMap<String,String>();
		//List<String> path = null;
		
		while(reader.hasNext()){
			reader.next();
			if(reader.hasName()){
				if(reader.getLocalName() == "methods"){
					state = State.methods;
				}else if(reader.getLocalName() == "method" && reader.isStartElement() ){
					if(reader.getAttributeCount() == 1){
						currentMethod = reader.getAttributeValue(0);
						state = State.method;
					}else{
						throw new XMLStreamException("parser error: couldn't read method signature");
					}
				}else if(reader.getLocalName() == "flows"){
					state =  State.flows;
					continue;
				}else if(reader.getLocalName() == "flow" && reader.isStartElement()){
					
					state =  State.flow;
				}else if(reader.getLocalName() == "from" && reader.isStartElement()){
					for( int i = 0; i < reader.getAttributeCount(); i++){
						fromAttributes.put(reader.getAttributeLocalName(i), reader.getAttributeValue(i));
					}
					state = State.from;
				}else if(reader.getLocalName() == "from" && reader.isEndElement()){
					state= State.flows;
				}else if(reader.getLocalName() == "to" && reader.isStartElement()){
					for( int i = 0; i < reader.getAttributeCount(); i++){
						toAttributes.put(reader.getAttributeLocalName(i), reader.getAttributeValue(i));
					}
					state =  State.to;
				}else if(reader.getLocalName() == "to" && reader.isEndElement()){
					state =  State.flow;
				}else if(reader.getLocalName() == "path" && reader.isStartElement()){
					state =  State.path;
				}else if(reader.getLocalName() == "path" && reader.isEndElement()){
					state = State.flow;
				}else if(reader.getLocalName() == "flow" && reader.isEndElement()){
					if(summary.containsKey(currentMethod)){
						summary.get(currentMethod).add(new DefaultMethodFlow(currentMethod, new FlowSourceFromXML(fromAttributes), new FlowSinkFromXML(toAttributes)));
					}else{
						Set<AbstractMethodFlow> data = new HashSet<AbstractMethodFlow>();
						data.add(new DefaultMethodFlow(currentMethod, new FlowSourceFromXML(fromAttributes), new FlowSinkFromXML(toAttributes)));
						summary.put(currentMethod, data);
						state = State.flows;
						
					}
					fromAttributes = new HashMap<String,String>();
					toAttributes = new HashMap<String,String>();
					//path = null;
				}
			}else{
				if(state == State.from && reader.isCharacters()){

				}else if(state == State.to && reader.isCharacters()){

				}else if(state == State.path){
					//TODO add path
				}
				
			}
		}
		return summary;
	
	}
	
	private enum State {
		init, methods, method, flows, flow,to,from,path;
	}
	
}


