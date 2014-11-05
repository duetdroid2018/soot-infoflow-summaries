package soot.jimple.infoflow.methodSummary.data;

/**
 * Enumeration containing the types of sources and sinks for which summaries can
 * be generated.
 * 
 * @author Steven Arzt
 */
public enum SourceSinkType {
	/**
	 * The flow starts or ends at a field of the current base object
	 */
	Field,
	
	/**
	 * The flow starts or ends at a field of a parameter of the current method
	 */
	Parameter,
	
	/**
	 * The flow starts or ends at the return value of the current method
	 */
	Return,
	
	/**
	 * The flow starts or ends at a method call. This is used if there are
	 * "gaps" in the flow that replace callbacks in unknown code.
	 */
	MethodCall
}
