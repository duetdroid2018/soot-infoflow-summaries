package soot.jimple.infoflow.methodSummary.data.impl;

import soot.Local;
import soot.PointsToSet;
import soot.SootField;
import soot.jimple.infoflow.methodSummary.data.IFlowSource;

public class Source {
	private FlowSourceForSummary sourceInfo;
	private PointsToSet pts;
	private Local leftOp;
	private Local fieldBase;
	private SootField f;
	private boolean star;
	
	public Source(FlowSourceForSummary sourceInfo,Local base, Local leftOp, SootField f,PointsToSet pts, boolean s) {
		super();
		this.sourceInfo = sourceInfo;
		this.pts = pts;
		this.leftOp = leftOp;
		this.fieldBase = base;
		this.star = s;
		this.f = f;
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((fieldBase == null) ? 0 : fieldBase.hashCode());
		result = prime * result + ((leftOp == null) ? 0 : leftOp.hashCode());
		result = prime * result + ((pts == null) ? 0 : pts.hashCode());
		result = prime * result + ((sourceInfo == null) ? 0 : sourceInfo.hashCode());
		result = prime * result + (star ? 1231 : 1237);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Source other = (Source) obj;
		if (fieldBase == null) {
			if (other.fieldBase != null)
				return false;
		} else if (!fieldBase.equals(other.fieldBase))
			return false;
		if (leftOp == null) {
			if (other.leftOp != null)
				return false;
		} else if (!leftOp.equals(other.leftOp))
			return false;
		if (pts == null) {
			if (other.pts != null)
				return false;
		} else if (!pts.equals(other.pts))
			return false;
		if (sourceInfo == null) {
			if (other.sourceInfo != null)
				return false;
		} else if (!sourceInfo.equals(other.sourceInfo))
			return false;
		if (star != other.star)
			return false;
		return true;
	}
	public SootField getField(){
		return f;
	}
	
}
