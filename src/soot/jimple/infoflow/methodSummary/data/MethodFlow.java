package soot.jimple.infoflow.methodSummary.data;


public class MethodFlow {

	public String methodSig() {
		return methodSig;
	}

	private final String methodSig;
	private final FlowSource from;
	private final FlowSink to;

	public MethodFlow(String methodSig, FlowSource from, FlowSink to) {
		this.methodSig = methodSig;
		this.from = from;
		this.to = to;
	}
	
	public FlowSource source() {
		return from;
	}
	
	public FlowSink sink() {
		return to;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || !(obj instanceof MethodFlow))
			return false;
		
		MethodFlow other = (MethodFlow) obj;
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
	
}
