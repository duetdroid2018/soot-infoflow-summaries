package soot.jimple.infoflow.methodSummary.data;

import soot.jimple.infoflow.methodSummary.data.impl.SummaryAccessPath;



public abstract class FlowSink extends AbstractFlowSinkSource {
	private final boolean taintSubFields;
	protected FlowSink(SourceSinkType type, int paramterIdx, SummaryAccessPath accessPath, boolean taintSubFields) {
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
