package soot.jimple.infoflow.methodSummary.data;



public class MethodFlow {

	public String methodSig() {
		return methodSig;
	}

	private final String methodSig;
	private final FlowSource from;
	private final FlowSink to;
	private final boolean isAlias;
	
	/**
	 * Creates a new instance of the MethodFlow class
	 * @param methodSig The signature of the method containing the flow
	 * @param from The start of the data flow (source)
	 * @param to The end of the data flow (sink)
	 * @param isAlias True if the source and the sink alias, false if this is
	 * not the case.
	 */
	public MethodFlow(String methodSig, FlowSource from, FlowSink to,
			boolean isAlias) {
		this.methodSig = methodSig;
		this.from = from;
		this.to = to;
		this.isAlias = isAlias;
	}
	
	/**
	 * Gets the source, i.e., the incoming flow
	 * @return The incoming flow
	 */
	public FlowSource source() {
		return from;
	}
	
	/**
	 * Gets the sink, i.e., the outgoing flow
	 * @return The outgoing flow
	 */
	public FlowSink sink() {
		return to;
	}
	
	/**
	 * Checks whether the current flow is coarser than the given flow, i.e., if
	 * all elements referenced by the given flow are also referenced by this flow
	 * @param flow The flow with which to compare the current flow
	 * @return True if the current flow is coarser than the given flow, otherwise
	 * false
	 */
	public boolean isCoarserThan(MethodFlow flow) {
		if (flow.equals(this))
			return true;
				
		return this.from.isCoarserThan(flow.source())
				&& this.to.isCoarserThan(flow.sink());
	}
	
	/**
	 * Reverses the current flow
	 * @return The reverse of the current flow
	 */
	public MethodFlow reverse() {
		FlowSource reverseSource = new FlowSource(to.getType(),
				to.getParameterIndex(), to.getBaseType(), to.getAccessPath(),
				to.getAccessPathTypes(), to.getGap());
		FlowSink reverseSink = new FlowSink(from.getType(),
				from.getParameterIndex(), from.getBaseType(),
				from.getAccessPath(), from.getAccessPathTypes(),
				to.taintSubFields(), from.getGap());
		return new MethodFlow(methodSig, reverseSource, reverseSink, isAlias);
	}
	
	/**
	 * Gets whether the source and the sink of this data flow alias
	 * @return True the source and the sink of this data flow alias, otherwise
	 * false
	 */
	public boolean isAlias() {
		return this.isAlias;
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
