package soot.jimple.infoflow.methodSummary.postProcessor;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import soot.jimple.infoflow.methodSummary.data.summary.GapDefinition;
import soot.jimple.infoflow.methodSummary.data.summary.MethodFlow;
import soot.jimple.infoflow.methodSummary.data.summary.MethodSummaries;

/**
 * Class for compacting a set of method flow summaries
 * 
 * @author Steven Arzt
 *
 */
public class SummaryFlowCompactor {
	
	private static final Logger logger = LoggerFactory.getLogger(InfoflowResultPostProcessor.class);
	
	private final MethodSummaries summaries;
	
	/**
	 * Creates a new instance of the SummaryFlowCompactor class
	 * @param summaries The set of flow summaries to compact
	 */
	public SummaryFlowCompactor(MethodSummaries summaries) {
		this.summaries = summaries;
	}
	
	/**
	 * Compacts the flow set
	 */
	public void compact() {
		compactFlowSet();
		removeDuplicateFlows();
		compactGaps();
	}
	
	/**
	 * Compacts the flow set by removing flows that are over-approximations of
	 * others
	 * @param flows The flow set to compact
	 */
	private void compactFlowSet() {
		int flowsRemoved = 0;
		boolean hasChanged = false;
		do {
			hasChanged = false;
			for (Iterator<MethodFlow> flowIt = summaries.iterator(); flowIt.hasNext(); ) {
				MethodFlow flow = flowIt.next();
				
				// Check if there is a more precise flow
				for (MethodFlow flow2 : summaries)
					if (flow != flow2 && flow.isCoarserThan(flow2)) {
						flowIt.remove();
						flowsRemoved++;
						hasChanged = true;
						break;
					}
				
				if (hasChanged)
					break;
			}
		} while (hasChanged);
		
		logger.info("Removed {} flows in favour of more precise ones", flowsRemoved);
	}
	
	/**
	 * Compacts the set of gaps to remove unnecessary ones
	 */
	public void compactGaps() {
		// If we only have incoming flows into a gap, but no outgoing ones, we
		// can remove the gap and all its flows altogether
		for (GapDefinition gd : summaries.getAllGaps()) {
			if (summaries.getOutFlowsForGap(gd).isEmpty()) {
				summaries.removeAll(summaries.getInFlowsForGap(gd));
				summaries.removeGap(gd);
			}
		}
		
		// Remove all unused gaps that are never referenced
		Set<GapDefinition> gaps = new HashSet<GapDefinition>(summaries.getAllGaps());
		for (GapDefinition gd : gaps) {
			boolean gapIsUsed = false;
			for (MethodFlow flow : summaries.getAllFlows())
				if (flow.source().getGap() == gd || flow.sink().getGap() == gd) {
					gapIsUsed = true;
					break;
				}
			if (!gapIsUsed)
				summaries.removeGap(gd);
		}
	}
	
	/**
	 * Removes duplicate flows from the given set of method summaries. A flow is
	 * considered  duplicate if the same flow already exists in reverse and is
	 * an alias relationship, i.e., a flow that is valid in both directions.
	 * @param summaries The set of summaries to clean up
	 */
	private void removeDuplicateFlows() {
		outer : for (Iterator<MethodFlow> flowIt = summaries.iterator(); flowIt.hasNext(); ) {
			MethodFlow curFlow = flowIt.next();
			
			// Check for the same flow in reverse
			for (MethodFlow compFlow : summaries) {
				if (curFlow != compFlow
						&& compFlow.isAlias()
						&& curFlow.isAlias()) {
					if (curFlow.reverse().equals(compFlow)) {
						// To make the results is reproducible, we introduce some
						// rules on which flows we keep and which ones we delete
						if (curFlow.source().getGap() == null
								&& curFlow.sink().getGap() != null)
							continue;
						
						flowIt.remove();
						continue outer;
					}
				}
			}
		}
	}

}
