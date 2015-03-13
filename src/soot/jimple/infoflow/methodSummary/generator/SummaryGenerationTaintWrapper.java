package soot.jimple.infoflow.methodSummary.generator;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import soot.SootMethod;
import soot.jimple.Stmt;
import soot.jimple.infoflow.data.AccessPath;
import soot.jimple.infoflow.solver.IInfoflowCFG;
import soot.jimple.infoflow.taintWrappers.AbstractTaintWrapper;
import soot.util.ConcurrentHashMultiMap;
import soot.util.MultiMap;

/**
 * Taint wrapper to be used during summary construction. If we find a call for
 * which we have no callee, we create a gap in our summary. This means that this
 * taint wrapper needs to produce fake sources for the possible outcomes of the
 * code inside the gap.
 * 
 * @author Steven Arzt
 */
public class SummaryGenerationTaintWrapper extends AbstractTaintWrapper {
	
	private MultiMap<Stmt, AccessPath> gapAccessPaths =
			new ConcurrentHashMultiMap<Stmt, AccessPath>();
	
	@Override
	public void initialize() {
		
	}
	
	@Override
	public Set<AccessPath> getTaintsForMethod(Stmt stmt, AccessPath taintedPath, IInfoflowCFG icfg) {
		// This must be a method invocation
		if (!stmt.containsInvokeExpr())
			return null;
		
		// If we have callees, we analyze them as usual
		Collection<SootMethod> callees = icfg.getCalleesOfCallAt(stmt);
		if (callees != null && !callees.isEmpty())
			return null;
		
		return Collections.emptySet();
		/*
		
		// Produce a continuation
		Set<AccessPath> res = new HashSet<AccessPath>();
		if (stmt.getInvokeExpr() instanceof InstanceInvokeExpr) {
			AccessPath ap = new AccessPath(((InstanceInvokeExpr) stmt.getInvokeExpr()).getBase(), true);
			res.add(ap);
			gapAccessPaths.put(stmt, ap);
		}
		for (Value paramVal : stmt.getInvokeExpr().getArgs()) {
			AccessPath ap = new AccessPath(paramVal, true);
			res.add(ap);
			gapAccessPaths.put(stmt, ap);
		}
		if (stmt instanceof DefinitionStmt) {
			AccessPath ap = new AccessPath(((DefinitionStmt) stmt).getLeftOp(), true);
			res.add(ap);
			gapAccessPaths.put(stmt, ap);
		}
		
		return res;
		*/
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
	
	public MultiMap<Stmt, AccessPath> getGapAccessPaths() {
		return this.gapAccessPaths;
	}

}
