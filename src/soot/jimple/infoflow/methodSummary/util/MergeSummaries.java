package soot.jimple.infoflow.methodSummary.util;

import java.util.Map;
import java.util.Set;

import soot.jimple.infoflow.methodSummary.data.AbstractMethodFlow;

public class MergeSummaries {
	public static Map<String, Set<AbstractMethodFlow>> putAll(Map<String, Set<AbstractMethodFlow>> flows,
			Map<String, Set<AbstractMethodFlow>> newFlows) {
		for (String key : newFlows.keySet()) {
			if (newFlows.get(key) != null && newFlows.get(key).size() > 0) {
				if (flows.containsKey(key)) {
					if (flows.get(key) != null)
						flows.get(key).addAll(newFlows.get(key));
					else
						flows.put(key, newFlows.get(key));
				} else {
					flows.put(key, newFlows.get(key));
				}
			}
		}
		return flows;
	}
}
