package soot.jimple.infoflow.methodSummary.source;

import soot.Local;
import soot.PointsToSet;
import soot.SootField;
import soot.jimple.infoflow.methodSummary.data.impl.DefaultFlowSource;

/**
 * Source data which is internally used to create the source model
 */
class SourceDataInternal {
	
	private DefaultFlowSource sourceInfo;
	//points to set of x where x.field
	private final PointsToSet pts;
	private final Local leftOp;
	private final Local fieldBase;
	private final SootField field;
	private final boolean taintSubFields;
	
	public SourceDataInternal(DefaultFlowSource sourceInfo,Local base, Local leftOp, SootField f,PointsToSet pts, boolean s) {
		super();
		this.sourceInfo = sourceInfo;
		this.pts = pts;
		this.leftOp = leftOp;
		this.fieldBase = base;
		this.taintSubFields = s;
		this.field = f;
	}
	
	public boolean pointsTo(PointsToSet pts2,Local l2){
		return leftOp.equals(l2) || pts.hasNonEmptyIntersection(pts2);
	} 
	
	public DefaultFlowSource getSourceInfo(){
		return sourceInfo;
	}
	
	public Local getLeftOp(){
		return leftOp;
	}
	public boolean getStar(){
		return this.taintSubFields;
	}
	public Local getFieldBase(){
		return fieldBase;
	}
	
	@Override
	public String toString(){
		return leftOp.toString() + " = " + fieldBase.toString() +"." + field.toString() + "  ::  " + sourceInfo.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((fieldBase == null) ? 0 : fieldBase.hashCode());
		result = prime * result + ((leftOp == null) ? 0 : leftOp.hashCode());
		result = prime * result + ((pts == null) ? 0 : pts.hashCode());
		result = prime * result + ((sourceInfo == null) ? 0 : sourceInfo.hashCode());
		result = prime * result + (taintSubFields ? 1231 : 1237);
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
		SourceDataInternal other = (SourceDataInternal) obj;
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
		if (taintSubFields != other.taintSubFields)
			return false;
		return true;
	}
	public SootField getField(){
		return field;
	}

	public boolean isTaintSubFields() {
		return taintSubFields;
	}
	
}
