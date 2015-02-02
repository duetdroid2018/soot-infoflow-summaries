package soot.jimple.infoflow.methodSummary.data;

import java.util.Arrays;
import java.util.Map;

import soot.jimple.infoflow.methodSummary.xml.XMLConstants;

/**
 * Representation of a flow sink.
 * 
 * @author Steven Arzt
 */
public class FlowSink extends AbstractFlowSinkSource {
	private final boolean taintSubFields;
	
	public FlowSink(SourceSinkType type, int paramterIdx,
			String[] fields, boolean taintSubFields) {
		super(type, paramterIdx, fields);
		this.taintSubFields = taintSubFields;
	}
	
	public FlowSink(SourceSinkType type, int paramterIdx,
			boolean taintSubFields) {
		super(type, paramterIdx, null);
		this.taintSubFields = taintSubFields;
	}
	
	public FlowSink(SourceSinkType type, String[] accessPath,
			boolean taintSubFields2) {
		super(type,-1,accessPath);
		this.taintSubFields = taintSubFields2;
	}

	public boolean taintSubFields(){
		return taintSubFields;
	}

	/**
	 * Checks whether the current source or sink is coarser than the given one,
	 * i.e., if all elements referenced by the given source or sink are also
	 * referenced by this one
	 * @param src The source or sink with which to compare the current one
	 * @return True if the current source or sink is coarser than the given one,
	 * otherwise false
	 */
	@Override
	public boolean isCoarserThan(AbstractFlowSinkSource other) {
		return super.isCoarserThan(other)
				&& other instanceof FlowSink
				&& this.taintSubFields;
	}
	
	@Override
	public Map<String, String> xmlAttributes() {
		Map<String, String> res = super.xmlAttributes();
		res.put(XMLConstants.ATTRIBUTE_TAINT_SUB_FIELDS, taintSubFields() + "");
		return res;
	}
	
	@Override
	public String toString(){
		if (isParameter())
			return "Parameter " + getParameterIndex() + (accessPath == null ? "" : " "
					+ Arrays.toString(accessPath)) + " " + taintSubFields();
		
		if (isField())
			return "Field" + (accessPath == null ? "" : " "
					+ Arrays.toString(accessPath)) + " " + taintSubFields();
		
		if(isReturn())
			return "Return" + (accessPath == null ? "" : " "
					+ Arrays.toString(accessPath)) + " " + taintSubFields();
		
		return "invalid sink";
	}	
	
	@Override
	public int hashCode() {
		return super.hashCode() + (31 * (taintSubFields ? 1 : 0));
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!super.equals(obj))
			return false;
		
		return this.taintSubFields == ((FlowSink) obj).taintSubFields;
	}
	
}
