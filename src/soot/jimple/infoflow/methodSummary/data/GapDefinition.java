package soot.jimple.infoflow.methodSummary.data;


/**
 * Definition of a gap in a method summary. A gap occurs if a data flow reaches
 * a callback method which is not known to the library generation code, but must
 * be analyzed when the summary is later used.
 * 
 * @author Steven Arzt
 *
 */
public class GapDefinition {
	
	private final int id;
	private final String signature;
	
	/**
	 * Creates a new instance of the {@link GapDefinition} class
	 * @param id The unique ID of this gap definition
	 * @param signature The signature of the callee
	 */
	public GapDefinition(int id, String signature) {
		this.id = id;
		this.signature = signature;
	}
	
	/**
	 * Gets the unique ID of this gap definition
	 * @return The unique ID of this gap definition
	 */
	public int getID() {
		return this.id;
	}
	
	/**
	 * Gets the signature of the callee of this gap
	 * @return The signature of the callee of this gap
	 */
	public String getSignature() {
		return this.signature;
	}
	
}
