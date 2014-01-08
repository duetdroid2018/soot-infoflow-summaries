package soot.jimple.infoflow.methodSummary.xml;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.Set;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import soot.jimple.infoflow.methodSummary.data.AbstractMethodFlow;
import soot.jimple.infoflow.methodSummary.data.Tuple;

public class XMLWriter implements ISummaryWriter {
	private File fileName;
	private XMLStreamWriter writer;
	private OutputStream out;

	public XMLWriter(File file) throws FileNotFoundException, XMLStreamException {
		fileName = file;
		out = new FileOutputStream(fileName);
		XMLOutputFactory factory = XMLOutputFactory.newInstance();
		writer = factory.createXMLStreamWriter(out);
		//factory.setProperty(XMLOutputFactory.INDENTATION, "/t");
		//writer = factory.createXMLStreamWriter(out);
		
	}

	boolean wroteMethodsTag = false;

	@Override
	public void write(Map<String,Set<AbstractMethodFlow>> flow) throws XMLStreamException {
		writer.writeStartDocument();
		writer.writeStartElement("methods");

		for (String m : flow.keySet()) {
			writer.writeStartElement("method");
			writer.writeAttribute("id", flow.get(m).iterator().next().methodSig());
			writer.writeStartElement("flows");
			for (AbstractMethodFlow data : flow.get(m)) {
				writer.writeStartElement("flow");
				writer.writeStartElement("from");
				for (Tuple<String, String> t : data.source().xmlAttributes()) {
					writer.writeAttribute(t._1, t._2);
				}
				writer.writeEndElement(); // end from
//				if (printPath && data.flowPath() != null) {
//					writer.writeStartElement("path");
//					for (String p : data.flowPath()) {
//						writer.writeStartElement("pathStep");
//						writer.writeCharacters(p);
//						writer.writeEndElement();
//					}
//					writer.writeEndElement();
//				}
				writer.writeStartElement("to");
				for (Tuple<String, String> t : data.sink().xmlAttributes()) {
					writer.writeAttribute(t._1, t._2);
				}
				// writer.writeCharacters(data.flowSink().xmlString());
				writer.writeEndElement();
				writer.writeEndElement(); // end flow
			}
			writer.writeEndElement(); // close flows
			writer.writeEndElement(); // close method
		}
		writer.writeEndElement();
		writer.writeEndDocument();
		writer.close();

	}
}
