package soot.jimple.infoflow.methodSummary.taintWrappers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import soot.Local;
import soot.Scene;
import soot.SootField;
import soot.SootMethod;
import soot.Value;
import soot.jimple.DefinitionStmt;
import soot.jimple.InstanceInvokeExpr;
import soot.jimple.InvokeExpr;
import soot.jimple.Stmt;
import soot.jimple.infoflow.data.AccessPath;
import soot.jimple.infoflow.methodSummary.data.FlowSink;
import soot.jimple.infoflow.methodSummary.data.FlowSource;
import soot.jimple.infoflow.methodSummary.data.MethodFlow;
import soot.jimple.infoflow.methodSummary.data.summary.LazySummary;
import soot.jimple.infoflow.solver.IInfoflowCFG;
import soot.jimple.infoflow.taintWrappers.AbstractTaintWrapper;

public class SummaryTaintWrapper extends AbstractTaintWrapper {
	private LazySummary flows;
	
	/**
	 * Creates a new instance of the {@link SummaryTaintWrapper} class
	 * @param flows The flows loaded from disk
	 */
	public SummaryTaintWrapper(LazySummary flows) {
		this.flows = flows;
	}

	@Override
	public Set<AccessPath> getTaintsForMethod(Stmt stmt,
			AccessPath taintedPath, IInfoflowCFG icfg) {
		// We only care about method invocations
		if (!stmt.containsInvokeExpr())
			return Collections.singleton(taintedPath);
		
		// We always retain the incoming taint
		Set<AccessPath> res = new HashSet<AccessPath>();
		res.add(taintedPath);
		
		// Get the flows in the target method
		SootMethod callee = stmt.getInvokeExpr().getMethod();
		Set<MethodFlow> flowsInCallee = new HashSet<MethodFlow>(flows.getMethodFlows(callee));
		for (SootMethod sm : icfg.getCalleesOfCallAt(stmt))
			flowsInCallee.addAll(flows.getMethodFlows(sm));
		
		// Apply the data flows until we reach a fixed point
		List<AccessPath> workList = new ArrayList<AccessPath>();
		workList.add(taintedPath);
		while (!workList.isEmpty()) {
			AccessPath curAP = workList.remove(0);
			for (MethodFlow flow : flowsInCallee) {
				AccessPath newAP = applyFlow(flow, stmt, curAP);
				if (newAP != null && res.add(newAP))
					workList.add(newAP);
			}
		}
		
		return res;
	}
	
	private AccessPath applyFlow(MethodFlow flow, Stmt stmt, AccessPath curAP) {		
		final FlowSource flowSource = flow.source();
		final FlowSink flowSink = flow.sink();
		
		if (flowSource.isParameter()) {
			// Get the parameter index from the call and compare it to the
			// parameter index in the flow summary
			final int paramIdx = getParameterIndex(stmt, curAP);
			if (paramIdx == flowSource.getParameterIndex()) {
				if (compareFields(curAP, flowSource))
					return addSinkTaint(flowSource, flowSink, stmt, curAP);
			}
		}
		else if (flowSource.isField()) {
			// Flows from a field can either be applied to the same field or
			// the base object in total
			boolean taint = (curAP.isLocal() || curAP.isInstanceFieldRef())
					&& curAP.getPlainValue().equals(getMethodBase(stmt));
			
			if (taint && compareFields(curAP, flowSource))
				return addSinkTaint(flowSource, flowSink, stmt, curAP);
		}
		else if (flowSource.isThis()) {
			if (curAP.isLocal() || curAP.isInstanceFieldRef())
				if (curAP.getPlainValue().equals(getMethodBase(stmt)))
					return addSinkTaint(flowSource, flowSink, stmt, curAP);
		}
		
		// Nothing matched
		return null;
	}

	/**
	 * Gets the parameter index to which the given access path refers
	 * @param stmt The invocation statement
	 * @param curAP The access path
	 * @return The parameter index to which the given access path refers if it
	 * exists. Otherwise, if the given access path does not refer to a parameter,
	 * -1 is returned.
	 */
	private int getParameterIndex(Stmt stmt, AccessPath curAP) {
		if (!stmt.containsInvokeExpr())
			return -1;
		if (curAP.isStaticFieldRef())
			return -1;
		
		final InvokeExpr iexpr = stmt.getInvokeExpr();
		for (int i = 0; i < iexpr.getArgCount(); i++)
			if (iexpr.getArg(i) == curAP.getPlainValue())
				return i;
		return -1;
	}

	private boolean compareFields(AccessPath taintedPath, FlowSource flowSource) {
		// If a is tainted, the summary must match a. If a.* is tainted, the
		// summary can also be a.b.
		if (taintedPath.getFieldCount() == 0)
			return !flowSource.isField() || taintedPath.getTaintSubFields();

		// if we have x.f....fn and the source is x.f'.f1'...f'n+1 and we don't
		// taint sub, we can't have a match
		if (taintedPath.getFieldCount() < flowSource.getAccessPathLength()
				&& !taintedPath.getTaintSubFields())
			return false;
		
		// Compare the shared sub-path
		for (int i = 0; i < taintedPath.getFieldCount()
				&& i < flowSource.getAccessPathLength(); i++) {
			SootField taintField = taintedPath.getFields()[i];
			String sourceField = flowSource.getAccessPath()[i];
			if (!sourceField.equals(taintField.toString()))
				return false;
		}

		return true;
	}

	/**
	 * Gets the field with the specified signature if it exists, otherwise
	 * returns null
	 * 
	 * @param fieldSig
	 *            The signature of the field to retrieve
	 * @return The field with the given signature if it exists, otherwise null
	 */
	private SootField safeGetField(String fieldSig) {
		if (fieldSig == null || fieldSig.equals(""))
			return null;
		if (Scene.v().containsField(fieldSig))
			return Scene.v().getField(fieldSig);
		return null;
	}

	/**
	 * Gets an array of fields with the specified signatures
	 * 
	 * @param fieldSigs
	 *            , list of the field signatures to retriev
	 * @return The Array of fields with the given signature if all exists,
	 *         otherwise null
	 */
	private SootField[] safeGetFields(String[] fieldSigs) {
		if (fieldSigs == null || fieldSigs.length == 0)
			return null;
		SootField[] fields = new SootField[fieldSigs.length];
		for (int i = 0; i < fieldSigs.length; i++) {
			fields[i] = safeGetField(fieldSigs[i]);
			if (fields[i] == null)
				return null;
		}
		return fields;

	}

	private AccessPath addSinkTaint(FlowSource flowSource,
			FlowSink flowSink, Stmt stmt, AccessPath taintedPath) {
		boolean taintSubFields = flowSink.taintSubFields()
				|| taintedPath.getTaintSubFields();

		// Do we need to taint the return value?
		if (flowSink.isReturn()) {
			// If the return value is never used, we can abort
			if (!(stmt instanceof DefinitionStmt))
				return null;
			
			DefinitionStmt defStmt = (DefinitionStmt) stmt;
			if (flowSink.hasAccessPath()) {
				SootField[] fields = safeGetFields(flowSink.getAccessPath());
				return new AccessPath(defStmt.getLeftOp(), fields,
							taintSubFields || fields == null);
			} else
				return new AccessPath(defStmt.getLeftOp(), taintSubFields);
		}
		// Do we need to taint a field of the base object?
		else if (flowSink.isField()
				&& stmt.containsInvokeExpr()
				&& stmt.getInvokeExpr() instanceof InstanceInvokeExpr) {
			InstanceInvokeExpr iinv = (InstanceInvokeExpr) stmt.getInvokeExpr();
			SootField[] fields = safeGetFields(flowSink.getAccessPath());
			return new AccessPath(iinv.getBase(), fields, taintSubFields || fields == null);
		}
		// Do we need to taint a field of the parameter?
		else if (flowSink.isParameter()
				&& stmt.containsInvokeExpr()) {
			Value arg = stmt.getInvokeExpr().getArg(flowSink.getParameterIndex());
			if (arg instanceof Local)
				return new AccessPath(arg, safeGetFields(flowSink
						.getAccessPath()), taintSubFields);
			else
				throw new RuntimeException("Cannot taint non-local parameter");
		}
		// We dont't know what this is
		else
			throw new RuntimeException("Unknown summary sink type: " + flowSink);
	}
	
	/**
	 * Gets the base object on which the method is invoked
	 * 
	 * @param stmt
	 *            The statement for which to get the base of the method call
	 * @return The base object of the method call if it exists, otherwise null
	 */
	private Value getMethodBase(Stmt stmt) {
		if (!stmt.containsInvokeExpr())
			throw new RuntimeException("Statement is not a method call: "
					+ stmt);
		InvokeExpr invExpr = stmt.getInvokeExpr();
		if (invExpr instanceof InstanceInvokeExpr)
			return ((InstanceInvokeExpr) invExpr).getBase();
		return null;
	}

	@Override
	protected boolean isExclusiveInternal(Stmt stmt, AccessPath taintedPath,
			IInfoflowCFG icfg) {
		// If we support the method, we are exclusive for it
		return supportsCallee(stmt, icfg);
	}
	
	@Override
	public boolean supportsCallee(SootMethod method) {
		// Check whether we directly support that class
		if (flows.supportsClass(method.getDeclaringClass().getName()))
			return true;
		
		return false;
	}
	
	@Override
	public boolean supportsCallee(Stmt callSite, IInfoflowCFG icfg) {
		if (!callSite.containsInvokeExpr())
			return false;

		SootMethod method = callSite.getInvokeExpr().getMethod();
		if (supportsCallee(method))
			return true;
		
		// Check all callees
		for (SootMethod sm : icfg.getCalleesOfCallAt(callSite))
			if (supportsCallee(sm))
				return true;
		
		return false;
	}

}
