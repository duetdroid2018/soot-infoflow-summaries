package soot.jimple.infoflow.methodSummary.data;

public abstract class AbstractMethodFlow {

	abstract public AbstractFlowSource source();

	abstract public AbstractFlowSink sink();

	abstract public String methodSig();

	// public String[] flowPath();
	abstract public String classSig();

	@Override
	public boolean equals(Object o) {
		if (o instanceof AbstractMethodFlow) {
			AbstractMethodFlow flow = (AbstractMethodFlow) o;
			if (source().equals(flow.source()) && sink().equals(flow.sink()) && methodSig().equals(flow.methodSig())
					&& classSig().equals(flow.classSig())) {
				return true;
			}
		} else {
			return false;
		}
		return false;
	}
}
