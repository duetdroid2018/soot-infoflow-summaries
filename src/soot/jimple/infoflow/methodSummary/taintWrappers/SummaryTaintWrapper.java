package soot.jimple.infoflow.methodSummary.taintWrappers;

import java.io.File;
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
import soot.Unit;
import soot.Value;
import soot.jimple.DefinitionStmt;
import soot.jimple.InstanceInvokeExpr;
import soot.jimple.InvokeExpr;
import soot.jimple.Stmt;
import soot.jimple.infoflow.data.AccessPath;
import soot.jimple.infoflow.methodSummary.data.MethodFlow;
import soot.jimple.infoflow.methodSummary.data.FlowSink;
import soot.jimple.infoflow.methodSummary.data.FlowSource;
import soot.jimple.infoflow.methodSummary.data.summary.LazySummary;
import soot.jimple.infoflow.solver.IInfoflowCFG;
import soot.jimple.infoflow.taintWrappers.AbstractTaintWrapper;
import soot.jimple.infoflow.taintWrappers.EasyTaintWrapper;

public class SummaryTaintWrapper extends AbstractTaintWrapper {
	private LazySummary flows;
	private EasyTaintWrapper w;
	private boolean simpleInterfaceHandlying = true;
	private boolean taintHashAEqualsCode = true;

	public SummaryTaintWrapper(LazySummary flows) {
		this.flows = flows;
		try {
			w = new EasyTaintWrapper(new File("EasyTaintWrapperSourceComplet.txt"));
		} catch (Exception e) {

		}
	}

	@Override
	public Set<AccessPath> getTaintsForMethod(Stmt stmt, AccessPath taintedPath, IInfoflowCFG icfg) {
		// We always retain the incoming taint

		Set<AccessPath> res = new HashSet<AccessPath>();
		
		
		if(stmt.toString().contains("hash")){
			System.out.print("");
		}
		
		res.add(taintedPath);
		if(isExclusiveInternal(stmt, taintedPath, icfg)){
			res.add(taintedPath);
			System.out.print("");
		}
		
		Set<SootMethod> callees = new HashSet<SootMethod>();
		callees.addAll(icfg.getCalleesOfCallAt(stmt));
		callees.add(stmt.getInvokeExpr().getMethod());
		if(simpleInterfaceHandlying){
			SootMethod m = stmt.getInvokeExpr().getMethod();
			callees.addAll(getAllPossibleMethods(m));
		}
		
		Collection<MethodFlow> methodFlows = getAllFlows(callees);

		// calc taints
		for (MethodFlow mFlow : methodFlows) {
			final FlowSource flowSource = mFlow.source();
			final FlowSink flowSink = mFlow.sink();

			if (flowSource.isParameter()) {
				int paraIdx = flowSource.getParameterIndex();
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
			if (flowSource.isThis() && (taintedPath.isLocal() || taintedPath.isInstanceFieldRef())
					&& taintedPath.getPlainValue().equals(getMethodBase(stmt))) {
				addSinkTaint(res, flowSource, flowSink, stmt, taintedPath);
			}
		}
		if(taintHashAEqualsCode && isExclusive(stmt, taintedPath, icfg) && (stmt.getInvokeExpr().getMethod().getName().equals("hashCode")
				|| stmt.getInvokeExpr().getMethod().getName().startsWith("equals"))){
			if (stmt instanceof DefinitionStmt) {
				DefinitionStmt defStmt = (DefinitionStmt) stmt;
				res.add(new AccessPath(defStmt.getLeftOp(), true));
			}
		}
		//if(w.isExclusive(stmt, taintedPath, icfg) || isExclusive(stmt, taintedPath, icfg)){
			Set<AccessPath> tmpRes = w.getTaintsForMethod(stmt, taintedPath, icfg);
			if(! (tmpRes.containsAll(res) && res.containsAll(tmpRes))){
			//	if(stmt.toString().contains("<java.lang"))
					System.out.println(stmt.toString());
				w.getTaintsForMethod(stmt, taintedPath, icfg);
			//	return tmpRes;
			}
			
			System.out.print("");
//		}
		

		return res;
	}

	private boolean compareFields(AccessPath taintedPath, FlowSource flowSource) {
		// If a is tainted, the summary must match a. If a.* is tainted, the
		// summary can also be a.b.
		if (taintedPath.getFieldCount() == 0)
			return !flowSource.isField() || taintedPath.getTaintSubFields();

		// if we have x.f....fn and the source is x.f'.f1'...f'n+1 and we dont taint sub, we cant have a match
		if (taintedPath.getFieldCount() < flowSource.getFieldCount() && !taintedPath.getTaintSubFields())
			return false;

		for (int i = 0; i < taintedPath.getFieldCount() && i < flowSource.getFieldCount(); i++) {
			SootField taintField = taintedPath.getFields()[i];
			String sourceField = flowSource.getFields().get(i);
			if (!sourceField.equals(taintField.toString()))
				return false;
		}

		return true;
	}

	/**
	 * Gets the field with the specified signature if it exists, otherwise returns null
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
	 * @return The Array of fields with the given signature if all exists, otherwise null
	 */
	private SootField[] safeGetFields(List<String> fieldSigs) {
		SootField[] fields = new SootField[fieldSigs.size()];
		for (int i = 0; i < fieldSigs.size(); i++) {
			fields[i] = safeGetField(fieldSigs.get(i));
			if (fields[i] == null)
				return null;
		}
		return fields;

	}

	private void addSinkTaint(Set<AccessPath> res, FlowSource flowSource, FlowSink flowSink, Stmt stmt,
			AccessPath taintedPath) {
		boolean taintSubFields = flowSink.taintSubFields() || taintedPath.getTaintSubFields();

		// Do we need to taint the return value?
		if (flowSink.isReturn()) {
			if (stmt instanceof DefinitionStmt) {
				DefinitionStmt defStmt = (DefinitionStmt) stmt;
				if (flowSource.isThis()) {
					res.add(new AccessPath(defStmt.getLeftOp(), true));
				}
				if (flowSink.hasAccessPath()) {
					SootField[] fields = safeGetFields(flowSink.getFields());
					if (fields == null)
						taintSubFields = true;
					res.add(new AccessPath(defStmt.getLeftOp(), fields, taintSubFields));
				} else {
					res.add(new AccessPath(defStmt.getLeftOp(), taintSubFields));
				}
			}
		}
		// Do we need to taint a field of the base object?
		else if (flowSink.isField() && stmt.containsInvokeExpr() && stmt.getInvokeExpr() instanceof InstanceInvokeExpr) {
			// TODO check
			InstanceInvokeExpr iinv = (InstanceInvokeExpr) stmt.getInvokeExpr();
			SootField[] sinkFields = safeGetFields(flowSink.getFields());
			if (sinkFields == null) {
				taintSubFields = true;
			}
			res.add(new AccessPath(iinv.getBase(), sinkFields, taintSubFields));
		}
		// Do we need to taint a field of the parameter?
		else if (flowSink.isParameter()) {
			Value arg = stmt.getInvokeExpr().getArg(flowSink.getParameterIndex());
			if (arg instanceof Local) {
				res.add(new AccessPath(arg, safeGetFields(flowSink.getFields()), taintSubFields));
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
			throw new RuntimeException("Statement is not a method call: " + stmt);
		InvokeExpr invExpr = stmt.getInvokeExpr();
		if (invExpr instanceof InstanceInvokeExpr)
			return ((InstanceInvokeExpr) invExpr).getBase();
		return null;
	}

	@Override
	protected boolean isExclusiveInternal(Stmt stmt, AccessPath taintedPath, IInfoflowCFG icfg) {
		boolean returnVal = false;
		
		SootMethod method = stmt.getInvokeExpr().getMethod();
		if (checkIsExclusiveForAMethod(method)) {
			//System.out.println("wrapper is exclusive for " + method);
			returnVal = true;
		}
		for (SootMethod m2 : icfg.getCalleesOfCallAt(stmt)) {
			if (checkIsExclusiveForAMethod(m2)) {
				//System.out.println("wrapper is exclusive for " + m2);
				returnVal = true;

			} else {
				if(w.isExclusive(stmt, taintedPath, icfg)){
					//System.out.println("missed " + m2);
				}
			}
		}
		return returnVal;
	}
	
	private boolean checkIsExclusiveForAMethod(SootMethod m ){
		if(flows.supportsClass(m.getDeclaringClass().getName()))
			return true;	
		if(simpleInterfaceHandlying){
		if(m.getDeclaringClass().isInterface()){
			for(SootClass c : Scene.v().getActiveHierarchy().getImplementersOf(m.getDeclaringClass())){
				if(flows.supportsClass(c.getName()))
					return true;
			}
		}
		}
		return false;
	}
	
	private Set<SootMethod> getAllPossibleMethods(SootMethod m){
		Set<SootMethod> res = new HashSet<>();
		if(m.getDeclaringClass().isInterface()){
			for(SootClass c : Scene.v().getActiveHierarchy().getImplementersOf(m.getDeclaringClass())){
				try{
					SootMethod tmp = c.getMethod(m.getName(), m.getParameterTypes(), m.getReturnType());
					if( tmp != null)
						res.add(tmp);
				}catch(Exception e){
					
				}
			}
		}
		return res;
	}

}
