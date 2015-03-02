package soot.jimple.infoflow.methodSummary.data;

import java.util.Arrays;

/**
 * Representation of a flow source
 * 
 * @author Steven Arzt
 */
public class FlowSource extends AbstractFlowSinkSource {

	public FlowSource(SourceSinkType type, String baseType) {
		super(type, -1, baseType, null, null);
	}
	
	public FlowSource(SourceSinkType type, String baseType, String[] fields,
			String[] fieldTypes) {
		super(type, -1, baseType, fields, fieldTypes);
	}
	
	public FlowSource(SourceSinkType type, int parameterIdx, String baseType) {
		super(type, parameterIdx, baseType, null, null);
	}
	
	public FlowSource(SourceSinkType type, int parameterIdx, String baseType,
			String[] fields, String[] fieldTypes) {
		super(type, parameterIdx, baseType, fields, fieldTypes);
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
