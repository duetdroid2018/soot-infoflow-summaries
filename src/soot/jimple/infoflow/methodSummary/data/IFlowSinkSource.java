package soot.jimple.infoflow.methodSummary.data;

import java.util.Map;

public interface IFlowSinkSource {
	
	public boolean isParamter();

	public int getParamterIndex();

	public boolean isField();

	public boolean isThis();

	public String getField();

	public String getParaType();

	public boolean hasAccessPath();
	
	public String getAccessPath();
	
	public Map<String, String> xmlAttributes();
	
}
