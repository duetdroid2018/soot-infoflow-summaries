package soot.jimple.infoflow.methodSummary.data;

import soot.jimple.infoflow.methodSummary.data.impl.SummaryAccessPath;



public abstract class FlowSource extends AbstractFlowSinkSource {

	protected FlowSource(SourceSinkType type, int paramterIdx, SummaryAccessPath accessPath) {
		super(type, paramterIdx, accessPath);
	}

	//a this source is a field source with apl = 0
	public boolean isThis()
	{
		return type().equals(SourceSinkType.Field) && !hasAccessPath();
	}
}
