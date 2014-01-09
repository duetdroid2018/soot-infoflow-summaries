package soot.jimple.infoflow.methodSummary.xml;

import java.io.File;
import java.io.FileNotFoundException;

import javax.xml.stream.XMLStreamException;

import soot.jimple.infoflow.methodSummary.data.MethodSummaries;

public interface ISummaryReader {
	public MethodSummaries processXMLFile(File source) throws XMLStreamException, FileNotFoundException;
	public MethodSummaries processXMLFile(String source) throws XMLStreamException, FileNotFoundException;
}
