package soot.jimple.infoflow.methodSummary.taintWrappers;

import static soot.Scene.v;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import soot.Scene;
import soot.SootField;
import soot.SootMethod;
import soot.Value;
import soot.jimple.InstanceInvokeExpr;
import soot.jimple.InvokeExpr;
import soot.jimple.Stmt;
import soot.jimple.infoflow.data.AccessPath;
import soot.jimple.infoflow.methodSummary.data.AbstractFlowSink;
import soot.jimple.infoflow.methodSummary.data.AbstractFlowSource;
import soot.jimple.infoflow.methodSummary.data.AbstractMethodFlow;
import soot.jimple.infoflow.methodSummary.data.impl.LazySummary;
import soot.jimple.infoflow.solver.IInfoflowCFG;
import soot.jimple.infoflow.taintWrappers.AbstractTaintWrapper;
import soot.jimple.internal.JAssignStmt;

public class SummaryTaintWrapper extends AbstractTaintWrapper {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	private LazySummary flows;

	SummaryTaintWrapper(LazySummary flows) {
		this.flows = flows;
	}
	
	@Override
	public Set<AccessPath> getTaintsForMethod(Stmt stmt, AccessPath taintedPath,
			IInfoflowCFG icfg) {
		Set<AccessPath> res = new HashSet<AccessPath>();
		
		// TODO: Can't we kill taints?
		res.add(taintedPath);

		Collection<AbstractMethodFlow> methodFlows = getAllFlows(icfg.getCalleesOfCallAt(stmt));
		SootMethod calledMethod = stmt.getInvokeExpr().getMethod();
		
		// calc taints
		for (AbstractMethodFlow mFlow : methodFlows) {
			final AbstractFlowSource flowSource = mFlow.source();
			final AbstractFlowSink flowSink = mFlow.sink();

			if (flowSource.isParamter()) {
				int paraIdx = flowSource.getParamterIndex();
				if (stmt.getInvokeExpr().getArg(paraIdx).equals(taintedPath.getPlainLocal())) {
					// We have a flow from the parameter a and a.? is tainted 
					
					if (taintedPath.isInstanceFieldRef()) {
						if (flowSource.hasAccessPath()) {
							if (flowSource.getAccessPath().equals(taintedPath.getFirstField().toString())) {
								addSinkTaint(res, flowSource, flowSink, calledMethod, stmt, taintedPath);
							}
						} else {
							// TODO
						}
					} else {
						if (taintedPath.getTaintSubFields()) {
							addSinkTaint(res, flowSource, flowSink, calledMethod, stmt, taintedPath);
						} else {
							if (!flowSource.hasAccessPath()) {
								addSinkTaint(res, flowSource, flowSink, calledMethod, stmt, taintedPath);
							}
						}

					}

				}
			}
			
			// There may be a flow from a field to e.g. a return value
			if (flowSource.isField() && taintedPath.isInstanceFieldRef()) {
				if (taintedPath.getFirstField().equals(Scene.v().getField(flowSource.getField()))) {
					if (taintedPath.getFieldCount() == 1) {
						if (!flowSink.hasAccessPath() || taintedPath.getTaintSubFields()) {
							addSinkTaint(res, flowSource, flowSink, calledMethod, stmt, taintedPath);
						}
					} else {
						if (flowSink.hasAccessPath() && v().getField(flowSink.getAccessPath()).equals(taintedPath.getFirstField().toString())) {
							addSinkTaint(res, flowSource, flowSink, calledMethod, stmt, taintedPath);
						}
					}
				}
			}
		}
		return res;
	}

	private void addSinkTaint(Set<AccessPath> res, AbstractFlowSource flowSource, AbstractFlowSink flowSink, SootMethod calledMethod, Stmt stmt,
			AccessPath taintedPath) {
		if (flowSink.isReturn()) {
			if (stmt instanceof JAssignStmt) {
				if (flowSink.hasAccessPath()) {
					SootField[] f = { Scene.v().getField(flowSink.getAccessPath()) };
					res.add(new AccessPath(((JAssignStmt) stmt).getLeftOp(), f, flowSink.taintSubFields() || taintedPath.getTaintSubFields()));
				} else {
					res.add(new AccessPath(((JAssignStmt) stmt).getLeftOp(), flowSink.taintSubFields() || taintedPath.getTaintSubFields()));
				}
			}

		} else if (flowSink.isField()) {
			if (flowSink.hasAccessPath()) {
				SootField[] f = { Scene.v().getField(flowSink.getField()), Scene.v().getField(flowSink.getAccessPath()) };
				res.add(new AccessPath(getMethodBase(stmt), f, flowSink.taintSubFields() || taintedPath.getTaintSubFields()));
			} else {
				SootField[] f = { Scene.v().getField(flowSink.getField()) };
				res.add(new AccessPath(getMethodBase(stmt), f, flowSink.taintSubFields() || taintedPath.getTaintSubFields()));
			}
		} else if (flowSink.isParamter()) {
			Value arg = stmt.getInvokeExpr().getArg(flowSink.getParamterIndex());
			if (flowSink.hasAccessPath()) {
				SootField[] f = { Scene.v().getField(flowSink.getAccessPath()) };
				res.add(new AccessPath(arg, f, flowSink.taintSubFields() || taintedPath.getTaintSubFields()));
			} else {
				res.add(new AccessPath(taintedPath.getPlainValue(), flowSink.taintSubFields() || taintedPath.getTaintSubFields()));
			}
		}

	}
	
	/**
	 * Gets all flow summaries for the given set of methods
	 * @param methods The set of methods for which to get flow summaries
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
	 * @param stmt The statement for which to get the base of the method call
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
	protected boolean isExclusiveInternal(Stmt stmt, AccessPath taintedPath,
			IInfoflowCFG icfg) {
		for (SootMethod m2 : icfg.getCalleesOfCallAt(stmt))
			if (flows.supportsClass(m2.getDeclaringClass().getName())) {
				logger.debug("exclusive for: " + stmt);
				return true;
			}
		return false;
	}
	
}
