package soot.jimple.infoflow.methodSummary.xml;

import javax.xml.stream.XMLStreamException;

import soot.jimple.infoflow.methodSummary.data.summary.MethodSummaries;

/**
 * Interface for all classes capable of writing method summary information into
 * files
 * 
 * @author Steven Arzt
 */
public interface ISummaryWriter {
	
	/**
	 * Writes the given flows into a file
	 * @param flow The flows to write
	 * @throws XMLStreamException
	 */
	public void write(MethodSummaries flow) throws XMLStreamException;
	
}
