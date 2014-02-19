package soot.jimple.infoflow.methodSummary.taintWrappers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import soot.ArrayType;
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
	private boolean enableKillTaints = false; //if enabled, the analysis isn't sound.
	private LazySummary flows;

	SummaryTaintWrapper(LazySummary flows) {
		this.flows = flows;
	}

	@Override
	public Set<AccessPath> getTaintsForMethod(Stmt stmt, AccessPath taintedPath, IInfoflowCFG icfg) {
		boolean isExcluseive = isExclusiveInternal(stmt, taintedPath, icfg);
		Set<AccessPath> res = new HashSet<AccessPath>();

		res.add(taintedPath);
		System.out.println();
		Collection<AbstractMethodFlow> methodFlows = getAllFlows(icfg.getCalleesOfCallAt(stmt));
		SootMethod calledMethod = stmt.getInvokeExpr().getMethod();
		// kill some flows
		//TODO think about. i think we only can remove flow if summary flows == real flows. 
		//if we have summary flows > real flow (summaries are an over approximation of the real flows) it isn't working.
		if (enableKillTaints) {
			for (AbstractMethodFlow mFlow : methodFlows) {
				final AbstractFlowSource flowSource = mFlow.source();
				final AbstractFlowSink flowSink = mFlow.sink();
				if (!flowSink.taintSubFields()) {
					if (flowSink.isParamter()) {
						int paraIdx = flowSource.getParamterIndex();
						if (stmt.getInvokeExpr().getArg(paraIdx).equals(taintedPath.getPlainLocal())
								&& !(taintedPath.isInstanceFieldRef() && taintedPath.getFirstField().getType() instanceof ArrayType)) {
							if (flowSink.hasAccessPath()) {
								if (taintedPath.getFieldCount() >= 1) {
									if (taintedPath.getFields()[0].equals(Scene.v().getField(flowSink.getAccessPath()))) {
										res.remove(taintedPath);
									}
								}
							} else {
								res.remove(taintedPath);
							}
						}
					} else if (flowSink.isField()) {
						if (taintedPath.getFieldCount() > 0 && taintedPath.getFirstField().equals(Scene.v().getField(flowSink.getField()))
								&& !(taintedPath.isInstanceFieldRef() && taintedPath.getFirstField().getType() instanceof ArrayType)) {
							if (flowSink.hasAccessPath()) {
								if (taintedPath.getFieldCount() > 1
										&& taintedPath.getFields()[1].equals(Scene.v().getField(flowSink.getAccessPath()))) {
									res.remove(taintedPath);
								}
							} else {
								res.remove(taintedPath);
							}
						}
					}
				}
			}
		}

		// calc taints
		for (AbstractMethodFlow mFlow : methodFlows) {
			final AbstractFlowSource flowSource = mFlow.source();
			final AbstractFlowSink flowSink = mFlow.sink();

			if (flowSource.isParamter()) {
				int paraIdx = flowSource.getParamterIndex();
				if (stmt.getInvokeExpr().getArg(paraIdx).equals(taintedPath.getPlainLocal())) {
					// We have a flow from the parameter a and a.? is tainted
					if (flowSource.hasAccessPath()) {
						if (taintedPath.getFieldCount() == 0 && taintedPath.getTaintSubFields()) {
							addSinkTaint(res, flowSource, flowSink, calledMethod, stmt, taintedPath, null);
						} else if (taintedPath.isInstanceFieldRef() && flowSource.getAccessPath().equals(taintedPath.getFirstField().toString())) {
							addSinkTaint(res, flowSource, flowSink, calledMethod, stmt, taintedPath, null);
						}
					} else {
						addSinkTaint(res, flowSource, flowSink, calledMethod, stmt, taintedPath, taintedPath.getFirstField());
					}
				}
			}

			// There may be a flow from a field to e.g. a return value
			if (flowSource.isField() && taintedPath.getPlainLocal().equals(getMethodBase(stmt))) {
				//
				if (flowSource.hasAccessPath()) {
					if (taintedPath.getFieldCount() == 0 && taintedPath.getTaintSubFields()) {
						addSinkTaint(res, flowSource, flowSink, calledMethod, stmt, taintedPath, null);
					} else if (taintedPath.getFieldCount() >= 1 && taintedPath.getFields()[0].equals(Scene.v().getField(flowSource.getField()))) {
						if (taintedPath.getTaintSubFields() && taintedPath.getFieldCount() == 1) {
							addSinkTaint(res, flowSource, flowSink, calledMethod, stmt, taintedPath, null);
						} else {
							if (taintedPath.getFieldCount() > 1 && taintedPath.getFields()[1].equals(Scene.v().getField(flowSource.getAccessPath()))) {
								addSinkTaint(res, flowSource, flowSink, calledMethod, stmt, taintedPath, null);
							}
						}
					}
				} else {
					addSinkTaint(res, flowSource, flowSink, calledMethod, stmt, taintedPath, taintedPath.getFirstField());
				}
			}
		}
		return res;
	}

	private void addSinkTaint(Set<AccessPath> res, AbstractFlowSource flowSource, AbstractFlowSink flowSink, SootMethod calledMethod, Stmt stmt,
			AccessPath taintedPath, SootField additionalField) {
		if (flowSink.isReturn()) {
			if (stmt instanceof JAssignStmt) {
				if (flowSource.isThis()) {
					res.add(new AccessPath(((JAssignStmt) stmt).getLeftOp(), true));

				} else if (flowSink.hasAccessPath()) {
					ArrayList<SootField> f = new ArrayList<SootField>();
					f.add(Scene.v().getField(flowSink.getAccessPath()));
					if (additionalField != null)
						f.add(additionalField);
					res.add(new AccessPath(((JAssignStmt) stmt).getLeftOp(), f.toArray(new SootField[f.size()]), flowSink.taintSubFields()
							|| taintedPath.getTaintSubFields()));
				} else {

					res.add(new AccessPath(((JAssignStmt) stmt).getLeftOp(), flowSink.taintSubFields() || taintedPath.getTaintSubFields()));
				}
			}

		} else if (flowSink.isField()) {
			if (flowSink.hasAccessPath()) {
				ArrayList<SootField> f = new ArrayList<SootField>();
				f.add(Scene.v().getField(flowSink.getField()));
				f.add(Scene.v().getField(flowSink.getAccessPath()));
				if (additionalField != null)
					f.add(additionalField);
				res.add(new AccessPath(getMethodBase(stmt), f.toArray(new SootField[f.size()]), flowSink.taintSubFields()
						|| taintedPath.getTaintSubFields()));
			} else if (taintedPath.getFieldCount() > 1 && !flowSink.taintSubFields()) {
				ArrayList<SootField> f = new ArrayList<SootField>();
				f.add(Scene.v().getField(flowSink.getField()));
				f.add(taintedPath.getFields()[1]);
				if (additionalField != null)
					f.add(additionalField);
				res.add(new AccessPath(getMethodBase(stmt), f.toArray(new SootField[f.size()]), flowSink.taintSubFields()
						|| taintedPath.getTaintSubFields()));
			} else {
				ArrayList<SootField> f = new ArrayList<SootField>();
				f.add(Scene.v().getField(flowSink.getField()));
				if (additionalField != null)
					f.add(additionalField);
				res.add(new AccessPath(getMethodBase(stmt), f.toArray(new SootField[f.size()]), flowSink.taintSubFields()
						|| taintedPath.getTaintSubFields()));
			}
		} else if (flowSink.isParamter()) {
			Value arg = stmt.getInvokeExpr().getArg(flowSink.getParamterIndex());
			if (flowSink.hasAccessPath()) {
				ArrayList<SootField> f = new ArrayList<SootField>();
				f.add(Scene.v().getField(flowSink.getAccessPath()));
				if (additionalField != null)
					f.add(additionalField);
				res.add(new AccessPath(arg, f.toArray(new SootField[f.size()]), flowSink.taintSubFields() || taintedPath.getTaintSubFields()));
			} else {
				res.add(new AccessPath(taintedPath.getPlainValue(), flowSink.taintSubFields() || taintedPath.getTaintSubFields()));
			}
		}

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
			if (!m2.isStatic() && flows.supportsClass(m2.getDeclaringClass().getName())) {
				logger.debug("exclusive for: " + stmt);
				return true;
			}
		return false;
	}

	public boolean isEnableKillTaints() {
		return enableKillTaints;
	}

	public void setEnableKillTaints(boolean enableKillTaints) {
		this.enableKillTaints = enableKillTaints;
	}
	

}
