package soot.jimple.infoflow.methodSummary.data.impl;

import soot.PointsToSet;
import soot.jimple.infoflow.methodSummary.data.IFlowSource;

public class Source {
	IFlowSource sourceInfo;
	PointsToSet pts;
	public Source(IFlowSource sourceInfo,PointsToSet pts) {
		super();
		this.sourceInfo = sourceInfo;
		this.pts = pts;
	}
	
	public boolean pointsTo(PointsToSet pts2){
		return pts.hasNonEmptyIntersection(pts2);
	} 
	
}
