package soot.jimple.infoflow.methodSummary.generator;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import soot.SootMethod;
import soot.Value;
import soot.jimple.DefinitionStmt;
import soot.jimple.InstanceInvokeExpr;
import soot.jimple.Stmt;
import soot.jimple.infoflow.data.AccessPath;
import soot.jimple.infoflow.methodSummary.data.summary.MethodSummaries;
import soot.jimple.infoflow.solver.IInfoflowCFG;
import soot.jimple.infoflow.taintWrappers.AbstractTaintWrapper;

/**
 * Taint wrapper to be used during summary construction. If we find a call for
 * which we have no callee, we create a gap in our summary. This means that this
 * taint wrapper needs to produce fake sources for the possible outcomes of the
 * code inside the gap.
 * 
 * @author Steven Arzt
 */
public class SummaryGenerationTaintWrapper extends AbstractTaintWrapper {
	
	private final MethodSummaries summaries;
	private final GapManager gapManager;
	
	public SummaryGenerationTaintWrapper(MethodSummaries summaries,
			GapManager gapManager) {
		this.summaries = summaries;
		this.gapManager = gapManager;
	}
	
	@Override
	public void initialize() {
		
	}
	
	@Override
	public Set<AccessPath> getTaintsForMethod(Stmt stmt, AccessPath taintedPath, IInfoflowCFG icfg) {
		// This must be a method invocation
		if (!stmt.containsInvokeExpr())
			return null;
		
		// Check whether we need to create a gap
		if (!needsGapConstruction(stmt, icfg))
			return null;
		
		// Do create the gap
		gapManager.getOrCreateGapForCall(summaries, stmt);
		
		// Produce a continuation
		Set<AccessPath> res = new HashSet<AccessPath>();
		if (stmt.getInvokeExpr() instanceof InstanceInvokeExpr)
			res.add(new AccessPath(((InstanceInvokeExpr) stmt.getInvokeExpr()).getBase(), true));
		for (Value paramVal : stmt.getInvokeExpr().getArgs())
			if (AccessPath.canContainValue(paramVal))
				res.add(new AccessPath(paramVal, true));
		if (stmt instanceof DefinitionStmt)
			res.add(new AccessPath(((DefinitionStmt) stmt).getLeftOp(), true));
		
		return res;
	}
	
	/**
	 * Checks whether we need to produce a gap for the given method call
	 * @param stmt The call statement
	 * @param icfg The interprocedural control flow graph
	 * @return True if we need to create a gap, otherwise false
	 */
	private boolean needsGapConstruction(Stmt stmt, IInfoflowCFG icfg) {
		// If the callee is native, there is no need for a gap
		if (stmt.getInvokeExpr().getMethod().isNative())
			return false;
		
		// We always need to construct a gap to toString(), equals(), and
		// hashCode()
		/*
		final String subSig = stmt.getInvokeExpr().getMethod().getSubSignature();
		if (subSig.equals("java.lang.String toString()")
				|| subSig.equals("int hashCode()")
				|| subSig.equals("boolean equals(java.lang.Object)"))
			return true;
		*/
		
		// We always construct a gap if we have no callees
		Collection<SootMethod> callees = icfg.getCalleesOfCallAt(stmt);
		if (callees == null || callees.isEmpty())
			System.out.println("");
		
		return callees == null || callees.isEmpty();
	}

	@Override
	protected boolean isExclusiveInternal(Stmt stmt, AccessPath taintedPath, IInfoflowCFG icfg) {
		return false;
	}

	@Override
	public boolean supportsCallee(SootMethod method) {
		// Callees are always theoretically supported
		return true;
	}

	@Override
	public boolean supportsCallee(Stmt callSite, IInfoflowCFG icfg) {
		// We only wrap calls that have no callees
		Collection<SootMethod> callees = icfg.getCalleesOfCallAt(callSite);
		return callees == null || callees.isEmpty();
	}
	
}
