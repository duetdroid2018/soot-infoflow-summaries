package soot.jimple.infoflow.methodSummary.xml;

import java.util.Map;
import java.util.Set;

import javax.xml.stream.XMLStreamException;

import soot.jimple.infoflow.methodSummary.data.AbstractMethodFlow;


public interface ISummaryWriter {
	public void write(Map<String,Set<AbstractMethodFlow>> flow) throws XMLStreamException;
}
