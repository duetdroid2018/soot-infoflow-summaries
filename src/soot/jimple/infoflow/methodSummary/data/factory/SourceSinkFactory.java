package soot.jimple.infoflow.methodSummary.data.factory;

import soot.ArrayType;
import soot.SootField;
import soot.jimple.infoflow.data.AccessPath;
import soot.jimple.infoflow.methodSummary.data.FlowSink;
import soot.jimple.infoflow.methodSummary.data.FlowSource;
import soot.jimple.infoflow.methodSummary.data.SourceSinkType;

public class SourceSinkFactory {

	private final int summaryAPLength;
	
	public SourceSinkFactory(int summaryAPLength) {
		this.summaryAPLength = summaryAPLength;
	}
	
	/**
	 * Cuts the given array of fields to the maximum access path length
	 * @param fields The array of fields to cut
	 * @return
	 */
	private SootField[] cutAPLength(SootField[] fields) {
		if (fields == null || fields.length == 0)
			return null;
		if (fields.length <= summaryAPLength)
			return fields;
		
		SootField f[] = new SootField[summaryAPLength];
		System.arraycopy(fields, 0, f, 0, summaryAPLength);
		return f;
	}
	
	public FlowSource createParameterSource(int parameterIdx) {
		return new FlowSource(SourceSinkType.Parameter, parameterIdx);
	}
	
	public FlowSource createThisSource() {
		return new FlowSource(SourceSinkType.Field);
	}
	
	/**
	 * Creates a new source model based on the given information
	 * @param type The type of data flow source
	 * @param parameterIdx The index of the method parameter through which the
	 * source value was obtained
	 * @param accessPath The access path describing the exact source object
	 * @return The newly created source object
	 */
	public FlowSource createSource(SourceSinkType type, int parameterIdx,
			AccessPath accessPath) {
		return new FlowSource(type, parameterIdx,
				sootFieldsToString(cutAPLength(accessPath.getFields())));
	}
	
	/**
	 * Creates a sink that models a value assigned to a field reachable through
	 * a parameter value
	 * @param paraIdx The index of the parameter
	 * @param accessPath The access path modeling the field inside the parameter
	 * value
	 * @return The sink object
	 */
	public FlowSink createParameterSink(int paraIdx, AccessPath accessPath) {
		if (accessPath.isLocal()) {
			if (!(accessPath.getBaseType() instanceof ArrayType))
				throw new RuntimeException("Parameter locals cannot directly be sinks");
			else
				return new FlowSink(SourceSinkType.Parameter, paraIdx,
						accessPath.getTaintSubFields());
		}
		else if (accessPath.getFieldCount() < summaryAPLength)
			return new FlowSink(SourceSinkType.Parameter, paraIdx,
					sootFieldsToString(accessPath.getFields()),
					accessPath.getTaintSubFields());
		else
			return new FlowSink(SourceSinkType.Parameter, paraIdx,
					sootFieldsToString(cutAPLength(accessPath.getFields())),
					true);
	}
		
	/**
	 * Creates a sink that models the value returned by the method or a field
	 * reachable through the return value.
	 * @param accessPath The access path modeling the returned value
	 * @return The sink object
	 */
	public FlowSink createReturnSink(AccessPath accessPath) {
		if (accessPath.isLocal())
			return new FlowSink(SourceSinkType.Return, -1,
					accessPath.getTaintSubFields());
		else if (accessPath.getFieldCount() < summaryAPLength)
			return new FlowSink(SourceSinkType.Return, -1,
					sootFieldsToString(accessPath.getFields()),
					accessPath.getTaintSubFields());
		else
			return new FlowSink(SourceSinkType.Return, -1,
					sootFieldsToString(cutAPLength(accessPath.getFields())),
					true);
	}
	
	/**
	 * Creates a sink that models a value assigned to a field
	 * @param accessPath The access path modeling the field
	 * @return The sink object
	 */
	public FlowSink createFieldSink(AccessPath accessPath) {
		if (accessPath.isLocal())
			return new FlowSink(SourceSinkType.Field, -1,
					accessPath.getTaintSubFields());
		else if (accessPath.getFieldCount() < summaryAPLength)
			return new FlowSink(SourceSinkType.Field, -1,
					sootFieldsToString(accessPath.getFields()),
					accessPath.getTaintSubFields());
		else
			return new FlowSink(SourceSinkType.Field, -1,
					sootFieldsToString(cutAPLength(accessPath.getFields())),
					true);
	}
	
	private String[] sootFieldsToString(SootField[] fields){
		if (fields == null || fields.length == 0)
			return null;
		
		String[] res = new String[fields.length];
		for (int i = 0; i < fields.length; i++)
			res[i] = fields[i].toString();
		return res;
	}
	
}
