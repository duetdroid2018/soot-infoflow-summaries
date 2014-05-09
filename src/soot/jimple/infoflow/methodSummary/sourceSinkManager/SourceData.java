package soot.jimple.infoflow.methodSummary.sourceSinkManager;

import java.util.List;

import soot.jimple.infoflow.methodSummary.data.DefaultFlowSource;

public class SourceData {
	List<DefaultFlowSource> sourceInfo;
	boolean taintSubFields;
	
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
