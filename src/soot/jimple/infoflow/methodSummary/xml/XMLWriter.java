package soot.jimple.infoflow.methodSummary.xml;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Map.Entry;
import java.util.Set;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import soot.jimple.infoflow.methodSummary.data.MethodFlow;
import soot.jimple.infoflow.methodSummary.data.summary.MethodSummaries;

public class XMLWriter implements ISummaryWriter {
	private XMLStreamWriter writer;
	private OutputStream out;

	public XMLWriter(File file) throws FileNotFoundException, XMLStreamException {
		out = new FileOutputStream(file);
		XMLOutputFactory factory = XMLOutputFactory.newInstance();
		writer = factory.createXMLStreamWriter(out);
	}

	boolean wroteMethodsTag = false;

	@Override
	public void write(MethodSummaries flow) throws XMLStreamException {
		writer.writeStartDocument();
		writer.writeStartElement("methods");

		for (Entry<String, Set<MethodFlow>> m : flow.getFlows().entrySet()) {
			writer.writeStartElement("method");
			writer.writeAttribute("id", m.getKey());
			writer.writeStartElement("flows");
			for (MethodFlow data : m.getValue()) {
				writer.writeStartElement("flow");
				writer.writeStartElement("from");
				writer.writeAttribute(XMLConstants.ATTRIBUTE_FLOWTYPE, data.source().xmlAttributes().get(XMLConstants.ATTRIBUTE_FLOWTYPE));
				for (Entry<String, String> t : data.source().xmlAttributes().entrySet()) {
					if (t.getKey().contains(XMLConstants.ATTRIBUTE_FLOWTYPE))
						continue;
					writer.writeAttribute(t.getKey(), t.getValue());
				}
				writer.writeEndElement(); // end from
				writer.writeStartElement("to");
				writer.writeAttribute(XMLConstants.ATTRIBUTE_FLOWTYPE, data.sink().xmlAttributes().get(XMLConstants.ATTRIBUTE_FLOWTYPE));
				for (Entry<String, String> t : data.sink().xmlAttributes().entrySet()) {
					if (t.getKey().contains(XMLConstants.ATTRIBUTE_FLOWTYPE))
						continue;
					writer.writeAttribute(t.getKey(), t.getValue());
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
