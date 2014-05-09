package soot.jimple.infoflow.methodSummary.data;



public abstract class IFlowSource extends AbstractFlowSinkSource {

	protected IFlowSource(SourceSinkType type, int paramterIdx, SummaryAccessPath accessPath) {
		super(type, paramterIdx, accessPath);
	}


}
