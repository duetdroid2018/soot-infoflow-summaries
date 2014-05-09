package soot.jimple.infoflow.methodSummary.data;

import soot.jimple.infoflow.methodSummary.sourceSink.IFlowSource;


public class DefaultMethodFlow extends AbstractMethodFlow {
	public String methodSig() {
		return methodSig;
	}

	private final String methodSig;
	private final IFlowSource from;
	private final IFlowSink to;

	public DefaultMethodFlow(String methodSig, IFlowSource from, IFlowSink to) {
		this.methodSig = methodSig;
		this.from = from;
		this.to = to;
	}
	
	@Override
	public IFlowSource source() {
		return from;
	}

	@Override
	public IFlowSink sink() {
		return to;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || !(obj instanceof DefaultMethodFlow))
			return false;
		
		DefaultMethodFlow other = (DefaultMethodFlow) obj;
		if (!other.methodSig.equals(this.methodSig))
			return false;
		if (!other.from.equals(this.from))
			return false;
		if (!other.to.equals(this.to))
			return false;
		return true;
	}
	
	@Override
	public int hashCode() {
		int prime = 31;
		return prime * methodSig.hashCode()
				+ prime * from.hashCode()
				+ prime * to.hashCode();
	}
	
	@Override
	public String toString(){
		return "{" + methodSig +" Source: [" + from.toString() + "] Sink: [" + to.toString() + "]" + "}";		
	}

	@Override
	public String classSig() {
		return methodSig.substring(1, methodSig.indexOf(":"));
	}

}
