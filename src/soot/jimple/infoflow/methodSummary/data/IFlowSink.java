package soot.jimple.infoflow.methodSummary.data;

public interface IFlowSink extends IFlowSinkSource {

	public boolean isReturn();
	
	public boolean taintSubFields();

}
