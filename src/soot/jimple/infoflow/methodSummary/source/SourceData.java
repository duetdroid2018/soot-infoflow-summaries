package soot.jimple.infoflow.methodSummary.source;

import java.util.List;

import soot.jimple.infoflow.methodSummary.data.impl.DefaultFlowSource;

public class SourceData {
	private final List<DefaultFlowSource> sourceInfo;
	private final boolean taintSubFields;
	
	public SourceData(List<DefaultFlowSource> sourceInfo, boolean taintSubFields){
		this.sourceInfo = sourceInfo;
		this.taintSubFields = taintSubFields;
	}
	
	public List<DefaultFlowSource> getSourceInfo() {
		return sourceInfo;
	}
	public boolean isTaintSubFields() {
		return taintSubFields;
	}
	
}
