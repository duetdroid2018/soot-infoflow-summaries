package soot.jimple.infoflow.methodSummary.sourceSink;

import soot.jimple.infoflow.methodSummary.data.AbstractFlowSinkSource;
import soot.jimple.infoflow.methodSummary.data.SourceSinkType;
import soot.jimple.infoflow.methodSummary.data.SummaryAccessPath;


public abstract class IFlowSource extends AbstractFlowSinkSource {

	protected IFlowSource(SourceSinkType type, int paramterIdx, SummaryAccessPath accessPath) {
		super(type, paramterIdx, accessPath);
	}


}
