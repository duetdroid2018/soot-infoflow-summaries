package soot.jimple.infoflow.methodSummary.data;

/**
 * This class saves one method flow
 *  that is 
 *  	- the source
 *  	- the sink
 *  	- method sig
 *  	- class sig
 *
 */
public interface MethodFlow {

	 public FlowSource source();

	 public FlowSink sink();

	 public String methodSig();

	// public String[] flowPath();
	 public String classSig();

	
}
