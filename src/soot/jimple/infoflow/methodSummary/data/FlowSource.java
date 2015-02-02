package soot.jimple.infoflow.methodSummary.data;

import java.util.Arrays;

/**
 * Representation of a flow source
 * 
 * @author Steven Arzt
 */
public class FlowSource extends AbstractFlowSinkSource {

	public FlowSource(SourceSinkType type) {
		super(type, -1, null);
	}
	public FlowSource(SourceSinkType type, String[] fields) {
		super(type, -1, fields);
	}
	
	public FlowSource(SourceSinkType type, int parameterIdx) {
		super(type, parameterIdx, null);
	}
	
	public FlowSource(SourceSinkType type, int parameterIdx, String[] fields) {
		super(type, parameterIdx, fields);
	}
		
	@Override
	public String toString(){
		if(isParameter())
			return "Parameter " + getParameterIndex() + (accessPath == null ? "" : " "
					+  Arrays.toString(accessPath));
		
		if(isField())
			return "Field" + (accessPath == null ? "" : " " + Arrays.toString(accessPath));
		
		if(isThis())
			return "THIS";
		
		return "<unknown>";
	}

	//a this source is a field source with apl = 0
	public boolean isThis()
	{
		return type().equals(SourceSinkType.Field) && !hasAccessPath();
	}
}
