package soot.jimple.infoflow.methodSummary.data;

import java.util.Arrays;
import java.util.Map;

/**
 * Data class which stores the data associated to a Sink or a Source of a method
 * flow.
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

	public SourceSinkType type() {
		return type;
	}

	public boolean isParameter() {
		return type().equals(SourceSinkType.Parameter);
	}

	public int getParameterIndex() {
		return parameterIdx;
	}

	public boolean isField() {
		return type().equals(SourceSinkType.Field);
	}

	public boolean isMethodCall() {
		return type().equals(SourceSinkType.MethodCall);
	}

	public String[] getAccessPath() {
		return accessPath;
	}

	public boolean isReturn() {
		return type().equals(SourceSinkType.Return);
	}

	public boolean hasAccessPath() {
		return accessPath != null && accessPath.length > 0;
	}

	public int getAccessPathLenght() {
		if (hasAccessPath())
			return accessPath.length;
		return 0;
	}

	public SourceSinkType getType() {
		return this.type;
	}

	// public abstract Map<String, String> xmlAttributes();
	//
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((accessPath == null) ? 0 : Arrays.hashCode(accessPath));
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
