package soot.jimple.infoflow.methodSummary.data;

public abstract class AbstractMethodFlow {

	abstract public IFlowSource source();

	abstract public IFlowSink sink();

	abstract public String methodSig();

	// public String[] flowPath();
	abstract public String classSig();

	@Override
	public boolean equals(Object o) {
		if (o == null || !(o instanceof AbstractMethodFlow))
			return false;
		if (this == o)
			return true;
		
		AbstractMethodFlow flow = (AbstractMethodFlow) o;
		if (!source().equals(flow.source()))
			return false;
		if (!sink().equals(flow.sink()))
			return false;
		if (!methodSig().equals(flow.methodSig()))
			return false;
		if (!classSig().equals(flow.classSig()))
			return false;
		return true;
	}
	
	@Override
	public int hashCode() {
		int prime = 31;
		return prime * source().hashCode()
				+ prime * sink().hashCode()
				+ prime * methodSig().hashCode();
	}
	
}
