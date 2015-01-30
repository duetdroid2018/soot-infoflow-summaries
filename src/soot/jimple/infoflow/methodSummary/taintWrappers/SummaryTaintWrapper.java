package soot.jimple.infoflow.methodSummary.taintWrappers;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import soot.Local;
import soot.Scene;
import soot.SootClass;
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
	private boolean interfaceHandling = true;

	public SummaryTaintWrapper(LazySummary flows) {
		this.flows = flows;
	}

	@Override
	public Set<AccessPath> getTaintsForMethod(Stmt stmt,
			AccessPath taintedPath, IInfoflowCFG icfg) {
		// We always retain the incoming taint
		Set<AccessPath> res = new HashSet<AccessPath>();

		if (isExclusiveInternal(stmt, taintedPath, icfg)) {
			res.add(taintedPath);
		} else {
			return res;
		}
		SootMethod m = stmt.getInvokeExpr().getMethod();
		
		Set<SootMethod> callees = calcAllRelevantMethods(m,stmt,icfg);
		Collection<MethodFlow> methodFlows = getAllFlows(callees);

		// calc taints
		for (MethodFlow mFlow : methodFlows) {
			final FlowSource flowSource = mFlow.source();
			final FlowSink flowSink = mFlow.sink();

			if (flowSource.isParameter()) {
				int paraIdx = flowSource.getParameterIndex();
				if (stmt.getInvokeExpr().getArg(paraIdx)
						.equals(taintedPath.getPlainValue())) {
					if (compareFields(taintedPath, flowSource))
						addSinkTaint(res, flowSource, flowSink, stmt,
								taintedPath);
				}
			}

			// There may be a flow from a field to e.g. a return value
			if (flowSource.isField()
					&& (taintedPath.isLocal() || taintedPath
							.isInstanceFieldRef())
					&& taintedPath.getPlainValue().equals(getMethodBase(stmt))) {
				if (compareFields(taintedPath, flowSource))
					addSinkTaint(res, flowSource, flowSink, stmt, taintedPath);
			}
			if (flowSource.isThis()
					&& (taintedPath.isLocal() || taintedPath
							.isInstanceFieldRef())
					&& taintedPath.getPlainValue().equals(getMethodBase(stmt))) {
				addSinkTaint(res, flowSource, flowSink, stmt, taintedPath);
			}
		}
		return res;
	}

	private Set<SootMethod> calcAllRelevantMethods(SootMethod m, Stmt stmt,
			IInfoflowCFG icfg) {
		Set<SootMethod> callees = new HashSet<SootMethod>();
		callees.addAll(icfg.getCalleesOfCallAt(stmt));
		callees.add(m);
		

		if (interfaceHandling) {
			callees.addAll(getMethodsInterfaceCall(m));
		} 

		// simple inherited method handling
		// if we call a method of class c which is not implemented in c but is
		// inherited we need to check the summaries for the parent classes
		// we only need to do that if c doesnt override the method
		// TODO at the moment we do that even if c override the method => add a
		// check if c override the method
		callees.addAll(handleInheritedMethods(m));
		return callees;
	}

	private Set<SootMethod> handleInheritedMethods(SootMethod m) {
		// build class chain
		List<SootClass> classes = new LinkedList<>();
		SootClass c = m.getDeclaringClass();
		// classes.add(c);
		do {
			c = c.getSuperclass();
			classes.add(c);
		} while (c.hasSuperclass());

		// look for methods
		Set<SootMethod> methods = new HashSet<>();
		for (SootClass clz : classes) {
			try {
				SootMethod method = clz.getMethod(m.getName(),
						m.getParameterTypes(), m.getReturnType());
				methods.add(method);
			} catch (Exception e) {

			}
		}

		return methods;
	}

	private boolean compareFields(AccessPath taintedPath, FlowSource flowSource) {
		// If a is tainted, the summary must match a. If a.* is tainted, the
		// summary can also be a.b.
		if (taintedPath.getFieldCount() == 0)
			return !flowSource.isField() || taintedPath.getTaintSubFields();

		// if we have x.f....fn and the source is x.f'.f1'...f'n+1 and we dont
		// taint sub, we cant have a match
		if (taintedPath.getFieldCount() < flowSource.getAccessPathLenght()
				&& !taintedPath.getTaintSubFields())
			return false;

		for (int i = 0; i < taintedPath.getFieldCount()
				&& i < flowSource.getAccessPathLenght(); i++) {
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

	private void addSinkTaint(Set<AccessPath> res, FlowSource flowSource,
			FlowSink flowSink, Stmt stmt, AccessPath taintedPath) {
		boolean taintSubFields = flowSink.taintSubFields()
				|| taintedPath.getTaintSubFields();

		// Do we need to taint the return value?
		if (flowSink.isReturn()) {
			if (stmt instanceof DefinitionStmt) {
				DefinitionStmt defStmt = (DefinitionStmt) stmt;
				if (flowSource.isThis()) {
					res.add(new AccessPath(defStmt.getLeftOp(), true));
				}
				if (flowSink.hasAccessPath()) {
					SootField[] fields = safeGetFields(flowSink.getAccessPath());
					if (fields == null)
						taintSubFields = true;
					res.add(new AccessPath(defStmt.getLeftOp(), fields,
							taintSubFields));
				} else {
					res.add(new AccessPath(defStmt.getLeftOp(), taintSubFields));
				}
			}
		}
		// Do we need to taint a field of the base object?
		else if (flowSink.isField() && stmt.containsInvokeExpr()
				&& stmt.getInvokeExpr() instanceof InstanceInvokeExpr) {
			// TODO check
			InstanceInvokeExpr iinv = (InstanceInvokeExpr) stmt.getInvokeExpr();
			SootField[] sinkFields = safeGetFields(flowSink.getAccessPath());
			if (sinkFields == null) {
				taintSubFields = true;
			}
			res.add(new AccessPath(iinv.getBase(), sinkFields, taintSubFields));
		}
		// Do we need to taint a field of the parameter?
		else if (flowSink.isParameter()) {
			Value arg = stmt.getInvokeExpr().getArg(
					flowSink.getParameterIndex());
			if (arg instanceof Local) {
				res.add(new AccessPath(arg, safeGetFields(flowSink
						.getAccessPath()), taintSubFields));
			} else {
				System.err.println("paramter is not a local " + arg);
			}

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
	private Collection<MethodFlow> getAllFlows(Collection<SootMethod> methods) {
		List<MethodFlow> methodFlows = new LinkedList<MethodFlow>();
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
		boolean returnVal = false;

		SootMethod method = stmt.getInvokeExpr().getMethod();
		if (method.isConstructor() && method.getParameterCount() == 0)
			return false;
		if (checkIsExclusive(method)) {
			returnVal = true;
		}
		for (SootMethod m2 : icfg.getCalleesOfCallAt(stmt)) {
			if (checkIsExclusive(m2)) {
				returnVal = true;
			}
		}
		return returnVal;
	}

	private boolean checkIsExclusive(SootMethod m) {
		if (flows.supportsClass(m.getDeclaringClass().getName()))
			return true;
		if (interfaceHandling) {
			if (m.getDeclaringClass().isInterface()) {
				for (SootClass c : Scene.v().getActiveHierarchy()
						.getImplementersOf(m.getDeclaringClass())) {
					if (flows.supportsClass(c.getName()))
						return true;
				}
			}
		}
		return false;
	}

	private Set<SootMethod> getMethodsInterfaceCall(SootMethod m) {
		Set<SootMethod> res = new HashSet<>();
		if (m.getDeclaringClass().isInterface()) {
			for (SootClass c : Scene.v().getActiveHierarchy()
					.getImplementersOf(m.getDeclaringClass())) {
				try {
					SootMethod tmp = c.getMethod(m.getName(),
							m.getParameterTypes(), m.getReturnType());
					if (tmp != null)
						res.add(tmp);
				} catch (Exception e) {

				}
			}
		}
		return res;
	}
	
	
	@Override
	public boolean supportsCallee(SootMethod method) {
		return checkIsExclusive(method);
	}
	
	@Override
	public boolean supportsCallee(Stmt callSite, IInfoflowCFG icfg) {
		if (!callSite.containsInvokeExpr())
			return false;

		SootMethod method = callSite.getInvokeExpr().getMethod();
		if (!supportsCallee(method))
			return false;
				
		//check if we can create an taint for that method
		Set<SootMethod> callees = calcAllRelevantMethods(method,callSite,icfg);
		if(getAllFlows(callees).size() > 0)
			return true;

		return false;
	}

}
