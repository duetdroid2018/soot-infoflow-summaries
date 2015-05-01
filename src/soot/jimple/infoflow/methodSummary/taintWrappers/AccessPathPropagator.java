package soot.jimple.infoflow.methodSummary.taintWrappers;

import soot.jimple.Stmt;
import soot.jimple.infoflow.data.Abstraction;
import soot.jimple.infoflow.methodSummary.data.GapDefinition;

/**
 * Class for describing an element at the frontier of the access path
 * propagation tree
 * 
 * @author Steven Arzt
 *
 */
class AccessPathPropagator {
	
	private final Taint taint;
	private final GapDefinition gap;
	private final AccessPathPropagator parent;
	
	private final Stmt stmt;
	private final Abstraction d1;
	private final Abstraction d2;
	
	public AccessPathPropagator(Taint taint) {
		this(taint, null, null);
	}
	
	public AccessPathPropagator(Taint taint,
			GapDefinition gap,
			AccessPathPropagator parent) {
		this(taint, gap, parent, null, null, null);
	}		
	
	public AccessPathPropagator(Taint taint,
			GapDefinition gap,
			AccessPathPropagator parent,
			Stmt stmt,
			Abstraction d1,
			Abstraction d2) {
		this.taint = taint;
		this.gap = gap;
		this.parent = parent;
		this.stmt = stmt;
		this.d1 = d1;
		this.d2 = d2;
	}
	
	public Taint getTaint() {
		return this.taint;
	}
	
	/**
	 * Gets the gap in which the taint is being propagated. This gap
	 * refers to the call stack / current method
	 * @return The gap in which this taint is  being propagated
	 */
	public GapDefinition getGap() {
		return this.gap;
	}
	
	/**
	 * Gets the parent of this AccessPathPropagator. This is the link to the
	 * previous method in the call stack before we descended into the current
	 * gap. If the gap is null, the parent must be null as well.
	 * @return The parent of this AccessPathPropagator
	 */
	public AccessPathPropagator getParent() {
		return this.parent;
	}
	
	/**
	 * Gets the statement with which this propagator is associated. A statement
	 * only exists for root-level propagators and indicates the original call
	 * for which the summary application was started.
	 * @return The statement associated with this propagator
	 */
	public Stmt getStmt() {
		return this.stmt;
	}
	
	/**
	 * Gets the context abstraction with which this propagator is associated.
	 * This value only exists for root-level propagators.
	 * @return The context abstraction associated with this propagator
	 */
	public Abstraction getD1() {
		return this.d1;
	}
	
	/**
	 * Gets the abstraction for which the taint wrapper was originally called.
	 * This value only exists for root-level propagators.
	 * @return The abstraction for which the taint wrapper was originally called
	 */
	public Abstraction getD2() {
		return this.d2;
	}
	
	/**
	 * Creates a copy of this AccessPathPropagator with a new taint
	 * @param newTaint The taint that the copied AccessPathPropagator shall
	 * have
	 * @return An AccessPathPropagator that is identical to the current one,
	 * except for the taint which is replaced by the given value
	 */
	public AccessPathPropagator copyWithNewTaint(Taint newTaint) {
		return new AccessPathPropagator(newTaint, gap, parent, stmt, d1, d2);
	}
	
	@Override
	public String toString() {
		return this.taint.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((taint == null) ? 0 : taint.hashCode());
		result = prime * result
				+ ((gap == null) ? 0 : gap.hashCode());
		result = prime * result
				+ ((parent == null) ? 0 : parent.hashCode());
		result = prime * result
				+ ((stmt == null) ? 0 : stmt.hashCode());
		result = prime * result
				+ ((d1 == null) ? 0 : d1.hashCode());
		result = prime * result
				+ ((d2 == null) ? 0 : d2.hashCode());
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
		AccessPathPropagator other = (AccessPathPropagator) obj;
		if (taint == null) {
			if (other.taint != null)
				return false;
		} else if (!taint.equals(other.taint))
			return false;
		if (gap == null) {
			if (other.gap != null)
				return false;
		} else if (!gap.equals(other.gap))
			return false;
		if (parent == null) {
			if (other.parent != null)
				return false;
		} else if (!parent.equals(other.parent))
			return false;
		if (stmt == null) {
			if (other.stmt != null)
				return false;
		} else if (!stmt.equals(other.stmt))
			return false;
		if (d1 == null) {
			if (other.d1 != null)
				return false;
		} else if (!d1.equals(other.d1))
			return false;
		if (d2 == null) {
			if (other.d2 != null)
				return false;
		} else if (!d2.equals(other.d2))
			return false;
		return true;
	}
	
}
