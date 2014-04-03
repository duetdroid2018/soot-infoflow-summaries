package soot.jimple.infoflow.methodSummary.data.impl;

import soot.Local;
import soot.PointsToSet;
import soot.jimple.infoflow.methodSummary.data.IFlowSource;

public class Source {
	private FlowSourceForSummary sourceInfo;
	private PointsToSet pts;
	private Local leftOp;
	private Local fieldBase;
	private boolean star;
	
	public Source(FlowSourceForSummary sourceInfo,Local base, Local leftOp, PointsToSet pts, boolean s) {
		super();
		this.sourceInfo = sourceInfo;
		this.pts = pts;
		this.leftOp = leftOp;
		this.fieldBase = base;
		this.star = s;
	}
	
	public boolean pointsTo(PointsToSet pts2,Local l2){
		return leftOp.equals(l2) || pts.hasNonEmptyIntersection(pts2);
	} 
	
	public FlowSourceForSummary getSourceInfo(){
		return sourceInfo;
	}
	
	public Local getLeftOp(){
		return leftOp;
	}
	public boolean getStar(){
		return this.star;
	}
	public Local getFieldBase(){
		return fieldBase;
	}
	
	@Override
	public String toString(){
		return leftOp.toString();
	}
	
}
