package soot.jimple.infoflow.methodSummary.data;

import java.util.Arrays;
import java.util.Map;

/**
 * Data class which stores the data associated to a Sink or a Source of a
 * method flow.
 */
public abstract class AbstractFlowSinkSource {
	protected final SourceSinkType type;
	protected final int parameterIdx;
	protected final String[] accessPath;
		
	public AbstractFlowSinkSource(SourceSinkType type, int parameterIdx,
			String[] accessPath) {
		this.type = type;
		this.parameterIdx = parameterIdx;
		this.accessPath = accessPath;
	}
		
	/**
	 * Checks whether the current source or sink is coarser than the given one,
	 * i.e., if all elements referenced by the given source or sink are also
	 * referenced by this one
	 * @param src The source or sink with which to compare the current one
	 * @return True if the current source or sink is coarser than the given one,
	 * otherwise false
	 */
	public boolean isCoarserThan(AbstractFlowSinkSource other) {
		if (this.equals(other))
			return true;
		
		if (this.type != other.type)
			return false;
		if (this.parameterIdx != other.parameterIdx)
			return false;
		if (this.accessPath != null && other.accessPath != null) {
			if (this.accessPath.length > other.accessPath.length)
				return false;
			for (int i = 0; i < this.accessPath.length; i++)
				if (!this.accessPath[i].equals(other.accessPath[i]))
					return false;
		}
		return true;
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
	
	public boolean isField() {
		return type().equals(SourceSinkType.Field);
	}

	public String[] getAccessPath() {
		return accessPath;
	}
	
	public boolean hasAccessPath(){
		return accessPath != null && accessPath.length > 0;
	}
		
	public SourceSinkType getType() {
		return this.type;
	}
	
	public abstract Map<String, String> xmlAttributes();
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((accessPath == null) ? 0 : Arrays.hashCode(accessPath));
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
		} else if (!Arrays.equals(accessPath, other.accessPath))
			return false;
		if (parameterIdx != other.parameterIdx)
			return false;
		if (type != other.type)
			return false;
		return true;
	}
	
}
