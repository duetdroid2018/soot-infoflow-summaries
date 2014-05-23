package soot.jimple.infoflow.methodSummary.data;

import java.util.List;
import java.util.Map;

import soot.jimple.infoflow.methodSummary.data.impl.SummaryAccessPath;

/**
 * Data class which stores the data associated to a Sink or a Source of a method flow.
 * 
 */
public abstract class AbstractFlowSinkSource {
	protected final SourceSinkType type;
	protected final int parameterIdx;
	protected final SummaryAccessPath accessPath;
	
	protected AbstractFlowSinkSource(SourceSinkType type, int paramterIdx,SummaryAccessPath accessPath){
		this.type = type;
		this.parameterIdx = paramterIdx;
		this.accessPath = accessPath;
	}
	
	public SourceSinkType type(){
		return type;
	}
	public boolean isParameter(){
		return type().equals(SourceSinkType.Parameter);
	}
	
	public int getParameterIndex(){
		return parameterIdx;
	}
	public boolean isField()
	{
		return type().equals(SourceSinkType.Field);
	};
	

	public List<String> getFields(){
		return accessPath.getFields();
	}
	public String getField(int idx){
		return accessPath.getFields().get(idx);
	}
	public int getFieldCount(){
		return accessPath.getAPLength();
	}

	public boolean hasAccessPath(){
		return accessPath != null && accessPath.notEmpty();
	}
	public SummaryAccessPath getAccessPath(){
		return accessPath;
	}
	public abstract Map<String, String> xmlAttributes();

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((accessPath == null) ? 0 : accessPath.hashCode());
		result = prime * result + parameterIdx;
		result = prime * result + ((type == null) ? 0 : type.hashCode());
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
		AbstractFlowSinkSource other = (AbstractFlowSinkSource) obj;
		if (accessPath == null) {
			if (other.accessPath != null)
				return false;
		} else if (!accessPath.equals(other.accessPath))
			return false;
		if (parameterIdx != other.parameterIdx)
			return false;
		if (type != other.type)
			return false;
		return true;
	}
	
}
