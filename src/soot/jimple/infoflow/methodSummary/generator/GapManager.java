package soot.jimple.infoflow.methodSummary.generator;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import soot.Local;
import soot.ValueBox;
import soot.jimple.DefinitionStmt;
import soot.jimple.Stmt;
import soot.jimple.infoflow.methodSummary.data.GapDefinition;
import soot.jimple.infoflow.methodSummary.data.summary.MethodSummaries;

/**
 * Class that manages the creation of gaps during the taint propagation and the
 * summary generation
 * 
 * @author Steven Arzt
 *
 */
public class GapManager {

	private final Map<Stmt, GapDefinition> gaps = new HashMap<Stmt, GapDefinition>();
	private int lastGapID = 0;
	
	/**
	 * Gets the data object of the given call into a gap method. If no gap
	 * definition exists, a new one is created.
	 * @param flows The flow set in which to register the gap
	 * @param gapCall The gap to be called
	 * @return The data object of the given gap call. If this call site has
	 * already been processed, the old object is returned. Otherwise, a new
	 * object is generated.
	 */
	public synchronized GapDefinition getOrCreateGapForCall(
			MethodSummaries flows, Stmt gapCall) {
		GapDefinition gd = this.gaps.get(gapCall);
		if (gd == null) {
			// Generate a new gap ID
			// Register it in the summary object
			gd = flows.getOrCreateGap(lastGapID++,
					gapCall.getInvokeExpr().getMethod().getSignature());
			this.gaps.put(gapCall, gd);
		}
		return gd;
	}

	/**
	 * Gets the data object of the given call into a gap method
	 * @param gapCall The gap to be called
	 * @return The data object of the given gap call if it exists, otherwise
	 * null
	 */
	public GapDefinition getGapForCall(Stmt gapCall) {
		return this.gaps.get(gapCall);
	}
	
	/**
	 * Gets whether the given local is referenced in any gap. This can either be
	 * as a parameter, a base object, or a return value
	 * @param local The local to check
	 * @return True if the given local is referenced in at least one gap,
	 * otherwise false
	 */
	public synchronized boolean isLocalReferencedInGap(Local local) {
		for (Stmt stmt : gaps.keySet()) {
			for (ValueBox vb : stmt.getUseBoxes())
				if (vb.getValue() == local)
					return true;
			if (stmt instanceof DefinitionStmt)
				if (((DefinitionStmt) stmt).getLeftOp() == local)
					return true;
		}
		return false;
	}
	
	/**
	 * Gets the gap definitions that references the given local. References can 
	 * either be as parameters, as base objects, or as return values. Note that 
	 * @param local The local for which to find the gap references
	 * @return The gaps that reference the given local
	 */
	public Set<GapDefinition> getGapDefinitionsForLocal(Local local) {
		Set<GapDefinition> res = null;
		stmt : for (Stmt stmt : gaps.keySet()) {
			for (ValueBox vb : stmt.getUseBoxes())
				if (vb.getValue() == local) {
					if (res == null)
						res = new HashSet<>();
					res.add(gaps.get(stmt));
					continue stmt;
				}
			if (stmt instanceof DefinitionStmt)
				if (((DefinitionStmt) stmt).getLeftOp() == local) {
					if (res == null)
						res = new HashSet<>();
					res.add(gaps.get(stmt));
					continue stmt;
				}
		}
		return res;
 	}
	
}
