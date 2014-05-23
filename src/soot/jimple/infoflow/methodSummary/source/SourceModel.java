package soot.jimple.infoflow.methodSummary.source;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import soot.Local;
import soot.SootField;
import soot.jimple.infoflow.methodSummary.data.impl.DefaultFlowSource;

public class SourceModel {
	
	//found sources, list position = apl
	private List<Set<SourceDataInternal>> sources;

	public SourceModel(int apLength) {
		sources = new ArrayList<Set<SourceDataInternal>>(apLength +1);
		for (int i = 0; i < apLength +1; i++) {
			sources.add(i, new HashSet<SourceDataInternal>());
		}
	}

	boolean addSource(int apl, SourceDataInternal data) {
		return sources.get(apl).add(data);
	}

	/**
	 * checks for a x.f if it is a source
	 * 
	 * Since source identification works with points to it can happen that we identify multiple sources with x.f
	 */
	public SourceData isSource(Local x, SootField f) {
		boolean matchedLocal = false;
		List<DefaultFlowSource> res = new LinkedList<DefaultFlowSource>();
		boolean taintSubFields = false;
		for (int i = 1; i < sources.size(); i++) {
			for (SourceDataInternal s : sources.get(i)) {
				if (x.equals(s.getFieldBase())) {
					matchedLocal = true;
					if ((f == null && s.getField() == null) || (f != null && f.equals(s.getField()))) {
						res.add(s.getSourceInfo());
						taintSubFields |= s.isTaintSubFields();
						// return s;
					}

				}

			}
		}
		if (matchedLocal && res.size() == 0) {
			throw new RuntimeException("the local: " + x + " is a source but we dont have it in our source model");
		}
		if (res.size() == 0)
			return null;

		return new SourceData(res, taintSubFields);
	}

	Set<SourceDataInternal> getSources(int apl) {
		return sources.get(apl);
	}

	@Override
	public String toString() {
		StringBuffer buf = new StringBuffer();
		for (int i = 1; i < sources.size(); i++) {
			buf.append("APL: " + i + "\n");
			if (sources.size() >= i && sources.get(i) != null) {
				for (SourceDataInternal s : sources.get(i)) {
					buf.append(s.getSourceInfo().toString() + "\n");
				}
			}
		}
		return buf.toString();
	}
}
