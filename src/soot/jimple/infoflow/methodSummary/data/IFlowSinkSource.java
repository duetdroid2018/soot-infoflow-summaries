package soot.jimple.infoflow.methodSummary.data;

import java.util.List;
import java.util.Map;

import soot.jimple.infoflow.data.Abstraction;
import soot.jimple.infoflow.methodSummary.data.impl.SummaryAccessPath;

public interface IFlowSinkSource {
	
	public boolean isParamter();

	public int getParamterIndex();

	public boolean isField();

	public boolean isThis();

	public List<String> getFields();
	public String getFirstField();
	public int getFieldCount();
	public boolean hasAccessPath();
	//public String getParaType();

	
	
	//public SummaryAccessPath getAccessPath();
	
	public Map<String, String> xmlAttributes();
	
}
