package soot.jimple.infoflow.methodSummary.taintWrappers;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import soot.Scene;
import soot.SootField;
import soot.SootMethod;
import soot.Value;
import soot.jimple.DefinitionStmt;
import soot.jimple.InstanceInvokeExpr;
import soot.jimple.InvokeExpr;
import soot.jimple.Stmt;
import soot.jimple.infoflow.data.AccessPath;
import soot.jimple.infoflow.methodSummary.data.AbstractMethodFlow;
import soot.jimple.infoflow.methodSummary.data.IFlowSink;
import soot.jimple.infoflow.methodSummary.data.IFlowSource;
import soot.jimple.infoflow.methodSummary.data.impl.LazySummary;
import soot.jimple.infoflow.solver.IInfoflowCFG;
import soot.jimple.infoflow.taintWrappers.AbstractTaintWrapper;

public class SummaryTaintWrapper extends AbstractTaintWrapper {
	private LazySummary flows;

	public SummaryTaintWrapper(LazySummary flows) {
		this.flows = flows;
	}

	@Override
	public Set<AccessPath> getTaintsForMethod(Stmt stmt, AccessPath taintedPath, IInfoflowCFG icfg) {
		// We always retain the incoming taint
		Set<AccessPath> res = new HashSet<AccessPath>();
		res.add(taintedPath);

		Set<SootMethod> callees = new HashSet<SootMethod>();
		callees.addAll(icfg.getCalleesOfCallAt(stmt));
		callees.add(stmt.getInvokeExpr().getMethod());

		Collection<AbstractMethodFlow> methodFlows = getAllFlows(callees);

		// calc taints
		for (AbstractMethodFlow mFlow : methodFlows) {
			final IFlowSource flowSource = mFlow.source();
			final IFlowSink flowSink = mFlow.sink();

			if (flowSource.isParamter()) {
				int paraIdx = flowSource.getParamterIndex();
				if (stmt.getInvokeExpr().getArg(paraIdx).equals(taintedPath.getPlainValue())) {
					if (compareFields(taintedPath, flowSource))
						addSinkTaint(res, flowSource, flowSink, stmt, taintedPath);
				}
			}

			// There may be a flow from a field to e.g. a return value
			if (flowSource.isField() && (taintedPath.isLocal() || taintedPath.isInstanceFieldRef())
					&& taintedPath.getPlainValue().equals(getMethodBase(stmt))) {
				if (compareFields(taintedPath, flowSource))
					addSinkTaint(res, flowSource, flowSink, stmt, taintedPath);
			}
			if(flowSource.isThis() && (taintedPath.isLocal() || taintedPath.isInstanceFieldRef())
					&& taintedPath.getPlainValue().equals(getMethodBase(stmt))){
				addSinkTaint(res, flowSource, flowSink, stmt, taintedPath);
			}
		}
		return res; 
	}

	private boolean compareFields(AccessPath taintedPath, IFlowSource flowSource) {
		// If a is tainted, the summary must match a. If a.* is tainted, the
		// summary can also be a.b.
		if (taintedPath.getFieldCount() == 0)
			return !flowSource.isField() || taintedPath.getTaintSubFields();

		// If we have at least one field, the first field must always match
		if (!flowSource.isField() || !flowSource.getField().equals(taintedPath.getFirstField().toString()))
			return false;
		// If we have only one field, that's it
		if (taintedPath.getFieldCount() == 1)
			return true;

		// If we have more fields, they must match as well
		if (taintedPath.getTaintSubFields())
			return true;
		// If we have source f and taint f.f2
		if(!flowSource.hasAccessPath())
			return true;
		return taintedPath.getFieldCount() > 1 && flowSource.hasAccessPath() && flowSource.getAccessPath().equals(taintedPath.getFields()[1].toString());
	}

	/**
	 * Gets the field with the specified signature if it exists, otherwise
	 * returns null
	 * 
	 * @param fieldSig
	 *            The signature of the field to retrieve
	 * @return The field with the given signature if it exists, otherwise false
	 */
	private SootField safeGetField(String fieldSig) {
		if (fieldSig == null || fieldSig.equals(""))
			return null;
		if (Scene.v().containsField(fieldSig))
			return Scene.v().getField(fieldSig);
		return null;
	}

	private void addSinkTaint(Set<AccessPath> res, IFlowSource flowSource, IFlowSink flowSink, Stmt stmt, AccessPath taintedPath) {
		boolean taintSubFields = flowSink.taintSubFields() || taintedPath.getTaintSubFields();

		// Do we need to taint the return value?
		if (flowSink.isReturn()) {
			if (stmt instanceof DefinitionStmt) {
				DefinitionStmt defStmt = (DefinitionStmt) stmt;
				if(flowSource.isThis()){
					res.add(new AccessPath(defStmt.getLeftOp(), true));
				}
				if (flowSink.hasAccessPath()) {
					SootField field = safeGetField(flowSink.getAccessPath());
					if (field == null)
						taintSubFields = true;
					res.add(new AccessPath(defStmt.getLeftOp(), field, taintSubFields));
				} else{
					if (taintedPath.getFieldCount() > 1){
						res.add(new AccessPath(defStmt.getLeftOp(), taintedPath.getFields()[1], taintSubFields));
					}else{
						res.add(new AccessPath(defStmt.getLeftOp(), taintSubFields));
					}
					
				}
			}
		}
		// Do we need to taint a field of the base object?
		else if (flowSink.isField() && stmt.containsInvokeExpr() && stmt.getInvokeExpr() instanceof InstanceInvokeExpr) {
			InstanceInvokeExpr iinv = (InstanceInvokeExpr) stmt.getInvokeExpr();
			List<SootField> fields = new LinkedList<SootField>();
			SootField field0 = safeGetField(flowSink.getField());
			if (field0 != null) {
				fields.add(field0);
				if (flowSink.hasAccessPath()) {
					SootField field1 = safeGetField(flowSink.getAccessPath());
					if (field1 != null)
						fields.add(field1);
					else
						taintSubFields = true;
				}
				else if (taintedPath.getFieldCount() > 1){
					fields.add(taintedPath.getFields()[1]);
				}
			} else
				taintSubFields = true;
			res.add(new AccessPath(iinv.getBase(), fields.toArray(new SootField[fields.size()]), taintSubFields));
		}
		// Do we need to taint a field of the parameter?
		else if (flowSink.isParamter() && flowSink.hasAccessPath()) {
			Value arg = stmt.getInvokeExpr().getArg(flowSink.getParamterIndex());
			res.add(new AccessPath(arg, safeGetField(flowSink.getAccessPath()), taintSubFields));
		}
		// We dont't know what this is
		else
			throw new RuntimeException("Unknown summary sink type: " + flowSink);
	}

	/**
	 * Gets all flow summaries for the given set of methods
	 * 
	 * @param methods
	 *            The set of methods for which to get flow summaries
	 * @return The set of flow summaries for the given methods
	 */
	private Collection<AbstractMethodFlow> getAllFlows(Collection<SootMethod> methods) {
		List<AbstractMethodFlow> methodFlows = new LinkedList<AbstractMethodFlow>();
		for (SootMethod m : methods)
			methodFlows.addAll(flows.getMethodFlows(m));
		return methodFlows;
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
			throw new RuntimeException("Statement is not a method call: " + stmt);
		InvokeExpr invExpr = stmt.getInvokeExpr();
		if (invExpr instanceof InstanceInvokeExpr)
			return ((InstanceInvokeExpr) invExpr).getBase();
		return null;
	}

	@Override
	protected boolean isExclusiveInternal(Stmt stmt, AccessPath taintedPath, IInfoflowCFG icfg) {
		for (SootMethod m2 : icfg.getCalleesOfCallAt(stmt))
			if (!m2.isStatic() && flows.supportsClass(m2.getDeclaringClass().getName()))
				return true;
		return false;
	}

}
