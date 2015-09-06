package soot.jimple.infoflow.methodSummary.data.summary;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import soot.jimple.infoflow.collect.ConcurrentHashSet;

/**
 * Data class encapsulating a set of method summaries
 * 
 * @author Steven Arzt
 */
public class MethodSummaries implements Iterable<MethodFlow> {
	
	private final Map<String, Set<MethodFlow>> flows;
	private final Map<Integer, GapDefinition> gaps;
	
	public MethodSummaries() {
		this(new ConcurrentHashMap<String, Set<MethodFlow>>());
	}
	
	MethodSummaries(Set<MethodFlow> flows) {
		this(flowSetToFlowMap(flows),
				new ConcurrentHashMap<Integer, GapDefinition>());
	}
	
	MethodSummaries(Map<String, Set<MethodFlow>> flows) {
		this(flows, new ConcurrentHashMap<Integer, GapDefinition>());
	}
	
	MethodSummaries(Map<String, Set<MethodFlow>> flows,
			Map<Integer, GapDefinition> gaps) {
		this.flows = flows;
		this.gaps = gaps;
	}
	
	/**
	 * Converts a flat set of method flows into a map from method signature to
	 * set of flows inside the respective method
	 * @param flows The flat set of method flows
	 * @return The signature-to-flow set map
	 */
	private static Map<String, Set<MethodFlow>> flowSetToFlowMap(Set<MethodFlow> flows) {
		Map<String, Set<MethodFlow>> flowSet = new HashMap<>();
		for (MethodFlow flow : flows) {
			Set<MethodFlow> flowsForSignature = flowSet.get(flow.methodSig());
			if (flowsForSignature == null) {
				flowsForSignature = new HashSet<>();
				flowSet.put(flow.methodSig(), flowsForSignature);
			}
			flowsForSignature.add(flow);
		}
		return flowSet;
	}
	
	/**
	 * Merges the given flows into the this method summary object
	 * @param newFlows The new flows to be merged
	 */
	public void merge(Set<MethodFlow> newFlows) {
		for (MethodFlow flow : newFlows) {
			Set<MethodFlow> existingFlows = flows.get(flow.methodSig());
			if (existingFlows == null) {
				existingFlows = new HashSet<>();
				flows.put(flow.methodSig(), existingFlows);
			}
			existingFlows.add(flow);
		}
	}
	
	/**
	 * Merges the given flows into the this method summary object
	 * @param newFlows The new flows to be merged
	 */
	public void merge(Map<String, Set<MethodFlow>> newFlows) {
		for (String key : newFlows.keySet()) {
			Set<MethodFlow> existingFlows = flows.get(key);
			if (existingFlows != null)
				existingFlows.addAll(newFlows.get(key));
			else
				flows.put(key, newFlows.get(key));
		}
	}

	/**
	 * Merges the given flows into the this method summary object
	 * @param newFlows The new flows to be merged
	 */
	public void merge(MethodSummaries newFlows) {
		if (newFlows == null)
			return;
		merge(newFlows.flows);
		if (newFlows.gaps != null)
			this.gaps.putAll(newFlows.gaps);
	}
	
	/**
	 * Gets all flows for the method with the given signature
	 * @param methodSig The signature of the method for which to retrieve the
	 * data flows
	 * @return The set of data flows for the method with the given signature
	 */
	public Set<MethodFlow> getFlowsForMethod(String methodSig) {
		Set<MethodFlow> methFlows = flows.get(methodSig);
		if (methFlows == null)
			return Collections.emptySet();
		return methFlows;
	}
	
	/**
	 * Adds a new flow for a method to this summary object
	 * @param flow The flow to add
	 */
	public boolean addFlow(MethodFlow flow) {
		Set<MethodFlow> methodFlows = flows.get(flow.methodSig());
		if (methodFlows == null) {
			methodFlows = new ConcurrentHashSet<MethodFlow>();
			flows.put(flow.methodSig(), methodFlows);
		}
		return methodFlows.add(flow);
	}
	
	/**
	 * Gets the gaps in this method summary. Gap definitions are mappings
	 * between unique IDs and definition objects
	 * @return The gap mapping for this method summary
	 */
	public Map<Integer, GapDefinition> getGaps() {
		return this.gaps;
	}
	
	/**
	 * Gets the gap definition with the given id. If no such gap definition
	 * exists, null is returned
	 * @param id The id for which to retrieve the gap definition
	 * @return The gap with the given id if it exists, otherwise null
	 */
	public GapDefinition getGap(int id) {
		if (this.gaps == null)
			return null;
		return this.gaps.get(id);
	}
	
	/**
	 * Gets all gaps defined in this method summary
	 * @return All gaps defined in this method summary
	 */
	public Collection<GapDefinition> getAllGaps() {
		return this.gaps.values();
	}
	
	/**
	 * Gets all flows registered in this method summary as a mapping from method
	 * signature to flow set
	 * @return The individual flows in this method summary
	 */
	public Map<String, Set<MethodFlow>> getFlows() {
		return this.flows;
	}
	
	/**
	 * Gets a set containing all flows in this summary object regardless of the
	 * method they are in
	 * @return A flat set of all flows contained in this summary object
	 */
	public Set<MethodFlow> getAllFlows() {
		Set<MethodFlow> flows = new HashSet<MethodFlow>();
		for (Set<MethodFlow> methodFlows : this.flows.values())
			flows.addAll(methodFlows);
		return flows;
	}

	@Override
	public Iterator<MethodFlow> iterator() {
		return new Iterator<MethodFlow>() {
			
			private String curMethod = null;
			private Iterator<Entry<String, Set<MethodFlow>>> flowIt = flows.entrySet().iterator();
			private Iterator<MethodFlow> curMethodIt = null;

			@Override
			public boolean hasNext() {
				return flowIt.hasNext()
						|| (curMethodIt != null && curMethodIt.hasNext());
			}

			@Override
			public MethodFlow next() {
				if (curMethodIt != null && !curMethodIt.hasNext())
					curMethodIt = null;
				if (curMethodIt == null) {
					Entry<String, Set<MethodFlow>> entry = flowIt.next();
					curMethodIt = entry.getValue().iterator();
					curMethod = entry.getKey();
				}
				return curMethodIt.next();
			}

			@Override
			public void remove() {
				curMethodIt.remove();
				if (flows.get(curMethod).isEmpty()) {
					flowIt.remove();
					curMethodIt = null;
				}
			}
		};
	}
	
	/**
	 * Retrieves the gap definition with the given ID if it exists, otherwise
	 * creates a new gap definition with this ID
	 * @param gapID The unique ID of the gap
	 * @param signature The signature of the callee
	 * @return The gap definition with the given ID
	 */
	public GapDefinition getOrCreateGap(int gapID, String signature) {
		GapDefinition gd = this.gaps.get(gapID);
		if (gd == null) {
			gd = new GapDefinition(gapID, signature);
			this.gaps.put(gapID, gd);
		}
		
		// If the existing gap did not have a method signature so far, we
		// silently add it to make the definition complete
		if (gd.getSignature() == null || gd.getSignature().isEmpty())
			gd.setSignature(signature);
		else if (!gd.getSignature().equals(signature))
			throw new RuntimeException("Gap signature mismatch detected");
		
		return gd;
	}
	
	/**
	 * Creates a temporary, underspecified gap with the given ID. This method is
	 * intended for incrementally loading elements from XML.
	 * @param gapID The unique ID of the gap
	 * @return The gap definition with the given ID
	 */
	public GapDefinition createTemporaryGap(int gapID) {
		if (this.gaps.containsKey(gapID))
			throw new RuntimeException("A gap with the ID " + gapID
					+ " already exists");
		
		GapDefinition gd = new GapDefinition(gapID);
		this.gaps.put(gapID, gd);
		return gd;
	}
	/**
	 * Removes the given gap definition from this method summary object
	 * @param gap The gap definition to remove
	 * @return True if the gap was contained in this method summary object
	 * before, otherwise false
	 */
	public boolean removeGap(GapDefinition gap) {
		for (Entry<Integer, GapDefinition> entry : this.gaps.entrySet())
			if (entry.getValue() == gap) {
				boolean ok = this.gaps.remove(entry.getKey()) == gap;
				return ok;
			}
		return false;
	}
	
	/**
	 * Clears all flows from this method summary
	 */
	public void clear() {
		if (this.flows != null)
			this.flows.clear();
		if (this.gaps != null)
			this.gaps.clear();
	}
	
	/**
	 * Gets the total number of flows in this summary object
	 * @return The total number of flows in this summary object
	 */
	public int getFlowCount() {
		int cnt = 0;
		for (Set<MethodFlow> methodFlows : this.flows.values())
			cnt += methodFlows.size();
		return cnt;
	}
	
	/**
	 * Validates this method summary object
	 */
	public void validate() {
		validateGaps();
		validateFlows();
	}
	
	/**
	 * Checks whether the gaps in this method summary are valid
	 */
	private void validateGaps() {
		// For method that has a flow into a gap, we must also have one flow to
		// the base object of that gap
		for (String methodName : getFlows().keySet()) {
			Set<GapDefinition> gapsWithFlows = new HashSet<GapDefinition>();
			Set<GapDefinition> gapsWithBases = new HashSet<GapDefinition>();
			
			for (MethodFlow flow : getFlows().get(methodName))
				if (!flow.isCustom()) {
					// For the source, record all flows to gaps and all flows to bases
					if (flow.source().getGap() != null) {
						if (flow.source().getType() == SourceSinkType.GapBaseObject)
							gapsWithBases.add(flow.source().getGap());
						else
							gapsWithFlows.add(flow.source().getGap());
					}
	
					// For the sink, record all flows to gaps and all flows to bases
					if (flow.sink().getGap() != null) {
						if (flow.sink().getType() == SourceSinkType.GapBaseObject)
							gapsWithBases.add(flow.sink().getGap());
						else
							gapsWithFlows.add(flow.sink().getGap());
					}
				}
			
			// Check whether we have some flow for which we don't have a base
			for (GapDefinition gd : gapsWithFlows)
				if (!gapsWithBases.contains(gd))
					throw new RuntimeException("Flow to/from a gap without a base detected "
							+ " for method " + methodName + ". Gap target is "
							+ gd.getSignature());
		}
		
		// No gap without a method signature may exist
		for (GapDefinition gap : this.getAllGaps())
			if (gap.getSignature() == null || gap.getSignature().isEmpty())
				throw new RuntimeException("Gap without signature detected");
	}
	
	/**
	 * Validates all flows inside this method summary object
	 */
	private void validateFlows() {
		for (String methodName : getFlows().keySet())
			for (MethodFlow flow : getFlows().get(methodName)) {
				flow.source().validate(methodName);
				flow.sink().validate(methodName);
			}
	}
	
	/**
	 * Gets all flows into the given gap
	 * @param gd The gap for which to get the incoming flows
	 * @return The set of flows into the given gap
	 */
	public Set<MethodFlow> getInFlowsForGap(GapDefinition gd) {
		Set<MethodFlow> res = new HashSet<>();
		for (String methodName : getFlows().keySet())
			for (MethodFlow flow : getFlows().get(methodName)) {
				if (flow.sink().getGap() == gd)
					res.add(flow);
			}
		return res;
	}
	
	/**
	 * Gets all flows out of the given gap
	 * @param gd The gap for which to get the outgoing flows
	 * @return The set of flows out of the given gap
	 */
	public Set<MethodFlow> getOutFlowsForGap(GapDefinition gd) {
		Set<MethodFlow> res = new HashSet<>();
		for (String methodName : getFlows().keySet())
			for (MethodFlow flow : getFlows().get(methodName)) {
				if (flow.source().getGap() == gd)
					res.add(flow);
			}
		return res;
	}
	
	/**
	 * Removes all of the given flows from this flow set
	 * @param toRemove The collection of flows to remove
	 */
	public void removeAll(Collection<MethodFlow> toRemove) {
		for (Iterator<MethodFlow> flowIt = this.iterator(); flowIt.hasNext(); ) {
			MethodFlow flow = flowIt.next();
			if (toRemove.contains(flow))
				flowIt.remove();
		}
	}
	
	/**
	 * Gets whether this method summary object is empty, i.e., does not contain
	 * any flows
	 * @return True if this method summary object is empty, otherwise false
	 */
	public boolean isEmpty() {
		return this.flows == null || this.flows.isEmpty();
	}
	
}
