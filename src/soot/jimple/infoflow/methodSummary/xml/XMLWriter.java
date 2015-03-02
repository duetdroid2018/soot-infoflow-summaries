package soot.jimple.infoflow.methodSummary.xml;

import static soot.jimple.infoflow.methodSummary.xml.XMLConstants.ATTRIBUTE_ACCESSPATH;
import static soot.jimple.infoflow.methodSummary.xml.XMLConstants.ATTRIBUTE_ACCESSPATHTYPES;
import static soot.jimple.infoflow.methodSummary.xml.XMLConstants.ATTRIBUTE_BASETYPE;
import static soot.jimple.infoflow.methodSummary.xml.XMLConstants.ATTRIBUTE_ERROR;
import static soot.jimple.infoflow.methodSummary.xml.XMLConstants.ATTRIBUTE_FLOWTYPE;
import static soot.jimple.infoflow.methodSummary.xml.XMLConstants.ATTRIBUTE_PARAMTER_INDEX;
import static soot.jimple.infoflow.methodSummary.xml.XMLConstants.ATTRIBUTE_RETURN;
import static soot.jimple.infoflow.methodSummary.xml.XMLConstants.ATTRIBUTE_TAINT_SUB_FIELDS;
import static soot.jimple.infoflow.methodSummary.xml.XMLConstants.ATTRIBUT_METHOD_SIG;
import static soot.jimple.infoflow.methodSummary.xml.XMLConstants.TREE_FLOW;
import static soot.jimple.infoflow.methodSummary.xml.XMLConstants.TREE_FLOWS;
import static soot.jimple.infoflow.methodSummary.xml.XMLConstants.TREE_METHOD;
import static soot.jimple.infoflow.methodSummary.xml.XMLConstants.TREE_METHODS;
import static soot.jimple.infoflow.methodSummary.xml.XMLConstants.TREE_SINK;
import static soot.jimple.infoflow.methodSummary.xml.XMLConstants.TREE_SOURCE;
import static soot.jimple.infoflow.methodSummary.xml.XMLConstants.VALUE_TRUE;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Map.Entry;
import java.util.Set;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import soot.jimple.infoflow.methodSummary.data.AbstractFlowSinkSource;
import soot.jimple.infoflow.methodSummary.data.MethodFlow;
import soot.jimple.infoflow.methodSummary.data.summary.MethodSummaries;

public class XMLWriter  {
	public XMLWriter(){
		
	}
	public void write(File file, MethodSummaries summary)  throws FileNotFoundException, XMLStreamException  {
		
		
		OutputStream out = new FileOutputStream(file);
		XMLOutputFactory factory = XMLOutputFactory.newInstance();
		XMLStreamWriter writer = factory.createXMLStreamWriter(out);
			
		writer.writeStartDocument();
		writer.writeStartElement(TREE_METHODS);
		
		for (Entry<String, Set<MethodFlow>> m : summary.getFlows().entrySet()) {
			
			//write method sub tree
			writer.writeStartElement(TREE_METHOD);
			writer.writeAttribute(ATTRIBUT_METHOD_SIG, m.getKey());
			
			writer.writeStartElement(TREE_FLOWS);
			for (MethodFlow data : m.getValue()) {
				writer.writeStartElement(TREE_FLOW);				
				writeFlowSource(writer,data);
				writeFlowSink(writer,data);
				writer.writeEndElement(); // end flow 
			}
			writer.writeEndElement(); // close flows
			writer.writeEndElement(); // close method
		}
		writer.writeEndElement(); //end methods tree
		writer.writeEndDocument();
		writer.close();

	}

	private void writeFlowSink(XMLStreamWriter writer, MethodFlow data) throws XMLStreamException {
		writer.writeStartElement(TREE_SINK);
		writerFlowSourceSink(writer,data,false);
		writer.writeEndElement();
	}

	private void writeFlowSource(XMLStreamWriter writer, MethodFlow data) throws XMLStreamException {
		writer.writeStartElement(TREE_SOURCE);
		writerFlowSourceSink(writer,data,true);
		writer.writeEndElement();
		
	}
	private void writerFlowSourceSink(XMLStreamWriter writer, MethodFlow data, boolean source) throws XMLStreamException{
		AbstractFlowSinkSource currentFlow = source ? data.source() : data.sink();
		writer.writeAttribute(ATTRIBUTE_FLOWTYPE, currentFlow.getType().toString());
		
		if(currentFlow.isField()){
			//nothing we need to write in the xml file here (we write the access path later)
		}else if(currentFlow.isMethodCall()){
			//TODO needs to be added later
		}else if(currentFlow.isParameter()){
			writer.writeAttribute(ATTRIBUTE_PARAMTER_INDEX, currentFlow.getParameterIndex() +"");
		}else if(currentFlow.isReturn() && !source){
			writer.writeAttribute(ATTRIBUTE_RETURN, VALUE_TRUE);
		}else{
			writer.writeAttribute(ATTRIBUTE_ERROR, "no valied source");
			System.err.println("ERROR: the summary for " + data.methodSig() + " is corrupted");
		}
		writer.writeAttribute(ATTRIBUTE_BASETYPE, currentFlow.getBaseType());
		if(currentFlow.hasAccessPath() && currentFlow.getAccessPath() != null){
			writer.writeAttribute(ATTRIBUTE_ACCESSPATH, Arrays.toString(currentFlow.getAccessPath()));
			writer.writeAttribute(ATTRIBUTE_ACCESSPATHTYPES, Arrays.toString(currentFlow.getAccessPathTypes()));
		}
		if(!source && data.sink().taintSubFields()){
			writer.writeAttribute(ATTRIBUTE_TAINT_SUB_FIELDS, VALUE_TRUE);
		}
		
	}
	


}
