package soot.jimple.infoflow.methodSummary.data;

import soot.jimple.infoflow.methodSummary.data.impl.SummaryAccessPath;



public abstract class FlowSource extends AbstractFlowSinkSource {

	protected FlowSource(SourceSinkType type, int paramterIdx, SummaryAccessPath accessPath) {
		super(type, paramterIdx, accessPath);
	}


}
