package soot.jimple.infoflow.methodSummary.source;

import java.util.List;

import soot.jimple.infoflow.methodSummary.data.FlowSource;

public class SourceData {
	private final List<FlowSource> sourceInfo;
	private final boolean taintSubFields;
	
	public SourceData(List<FlowSource> sourceInfo, boolean taintSubFields){
		this.sourceInfo = sourceInfo;
		this.taintSubFields = taintSubFields;
	}
	
	public List<FlowSource> getSourceInfo() {
		return sourceInfo;
	}
	public boolean isTaintSubFields() {
		return taintSubFields;
	}
	
}