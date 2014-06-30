package soot.jimple.infoflow.methodSummary.data;

public interface MethodFlow {

	 public FlowSource source();

	 public FlowSink sink();

	 public String methodSig();

	// public String[] flowPath();
	 public String classSig();

	
}
