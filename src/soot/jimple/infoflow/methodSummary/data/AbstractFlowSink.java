package soot.jimple.infoflow.methodSummary.data;


public abstract class AbstractFlowSink implements IFlowSinkSource{
	abstract public boolean isReturn();
	abstract public boolean taintSubFields();
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof AbstractFlowSink) {
			AbstractFlowSink sink = (AbstractFlowSink) o;
			if(isReturn()){
				return sink.isReturn();
			}else if(isParamter()){
				return sink.isParamter() && sink.getParamterIndex() == getParamterIndex() && sink.getParaType().equals(getParaType());
			}else if(isField()){
				return sink.isField() && sink.getField().equals(getField()) && (isThis() == sink.isThis());
			}
		} 
		return false;
	}
}
