package soot.jimple.infoflow.methodSummary.data;



public abstract class IFlowSink extends AbstractFlowSinkSource {
	private final boolean taintSubFields;
	protected IFlowSink(SourceSinkType type, int paramterIdx, SummaryAccessPath accessPath, boolean taintSubFields) {
		super(type, paramterIdx, accessPath);
		this.taintSubFields = taintSubFields;
	}
	public boolean isReturn(){
		return type().equals(SourceSinkType.Return);
	}
	public boolean taintSubFields(){
		return taintSubFields;
	}

}
