package soot.jimple.infoflow.methodSummary.data;

import soot.jimple.infoflow.methodSummary.data.impl.SummaryAccessPath;



public abstract class FlowSource extends AbstractFlowSinkSource {

	protected FlowSource(SourceSinkType type, int paramterIdx, SummaryAccessPath accessPath) {
		super(type, paramterIdx, accessPath);
	}


	//TODO could be removed. A This sources is just a field source with
	public boolean isThis()
	{
		return type().equals(SourceSinkType.Field) && !hasAccessPath();
	}
}
