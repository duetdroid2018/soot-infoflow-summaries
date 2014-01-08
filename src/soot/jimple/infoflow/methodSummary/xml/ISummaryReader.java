package soot.jimple.infoflow.methodSummary.xml;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Map;
import java.util.Set;

import javax.xml.stream.XMLStreamException;

import soot.jimple.infoflow.methodSummary.data.AbstractMethodFlow;

public interface ISummaryReader {
	public Map<String, Set<AbstractMethodFlow>> processXMLFile(File source) throws XMLStreamException, FileNotFoundException;
	public Map<String, Set<AbstractMethodFlow>> processXMLFile(String source) throws XMLStreamException, FileNotFoundException;
}
