package soot.jimple.infoflow.methodSummary.source;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import soot.Local;
import soot.SootField;
import soot.jimple.infoflow.methodSummary.data.FlowSource;

class SourceModel {
	
	//found sources, list position = apl
	private Set<SourceDataInternal> sources;

	public SourceModel(int apLength) {
		sources = new HashSet<SourceDataInternal>(apLength+1);
	}

	boolean addSource(SourceDataInternal data) {
		return sources.add(data);
	}

	/**
	 * Checks for a local with an optional field if it is a source
	 * Since source identification works with points to it can happen that we identify multiple sources with x.f
	 * @param local
	 * @param field 
	 * @return
	 */
	public SourceData isSource(Local local, SootField field) {
		boolean matchedLocal = false;
		List<FlowSource> res = new LinkedList<FlowSource>();
		boolean taintSubFields = false;
		for (SourceDataInternal s : sources) {
			if (local.equals(s.getFieldBase())) {
				matchedLocal = true;
				if ((field == null && s.getField() == null) || (field != null && field.equals(s.getField()))) {
					res.add(s.getSourceInfo());
					taintSubFields |= s.isTaintSubFields();
					// return s;
				}
			}
		}
		if (matchedLocal && res.size() == 0) {
			throw new RuntimeException("the local: " + local + " is a source but we dont have it in our source model");
		}
		if (res.size() == 0)
			return null;

		return new SourceData(res, taintSubFields);
	}

	Set<SourceDataInternal> getSources(int apl) {
		return sources;
	}

	@Override
	public String toString() {
		StringBuffer buf = new StringBuffer();
		for (SourceDataInternal s : sources) {
			buf.append(s.getSourceInfo().toString() + "\n");
		}
		return buf.toString();
	}
}
