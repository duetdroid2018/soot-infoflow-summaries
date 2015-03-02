package soot.jimple.infoflow.methodSummary.taintWrappers;

import heros.solver.IDESolver;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import soot.BooleanType;
import soot.DoubleType;
import soot.FastHierarchy;
import soot.FloatType;
import soot.Hierarchy;
import soot.IntType;
import soot.Local;
import soot.LongType;
import soot.RefType;
import soot.Scene;
import soot.SootClass;
import soot.SootField;
import soot.SootMethod;
import soot.Type;
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

import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

public class SummaryTaintWrapper extends AbstractTaintWrapper {
	private LazySummary flows;
	
	private Hierarchy hierarchy;
	private FastHierarchy fastHierarchy;
	
	protected final LoadingCache<SootMethod, Set<MethodFlow>> methodToFlows =
			IDESolver.DEFAULT_CACHE_BUILDER.build(new CacheLoader<SootMethod, Set<MethodFlow>>() {
				@Override
				public Set<MethodFlow> load(SootMethod method) throws Exception {
					// Get the flows in the target method
					Set<MethodFlow> flowsInCallee = new HashSet<MethodFlow>(flows.getMethodFlows(method));

					// We look into parent classes and interfaces if we did not find
					// anything for the class itself
					if (flowsInCallee.isEmpty())
						for (SootMethod callee : getAllCallees(method))
							flowsInCallee.addAll(flows.getMethodFlows(callee));
					
					return flowsInCallee;
				}
			});
	
	/**
	 * Creates a new instance of the {@link SummaryTaintWrapper} class
	 * @param flows The flows loaded from disk
	 */
	public SummaryTaintWrapper(LazySummary flows) {
		this.flows = flows;
	}
	
	@Override
	public void initialize() {
		// Load all classes for which we have summaries to signatures
		for (String className : flows.getLoadableClasses())
			Scene.v().forceResolve(className, SootClass.SIGNATURES);
		for (String className : flows.getSupportedClasses())
			Scene.v().forceResolve(className, SootClass.SIGNATURES);
		
		// Get the hierarchy
		this.hierarchy = Scene.v().getActiveHierarchy();
		this.fastHierarchy = Scene.v().getOrMakeFastHierarchy();
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
		
		// Get the cached data flows
		final SootMethod method = stmt.getInvokeExpr().getMethod();
		Set<MethodFlow> flowsInCallee = methodToFlows.getUnchecked(method);
		
		// If we have no direct entry, check the CG
		if (flowsInCallee.isEmpty()) {
			flowsInCallee = new HashSet<MethodFlow>();
			for (SootMethod callee : icfg.getCalleesOfCallAt(stmt))
				flowsInCallee.addAll(methodToFlows.getUnchecked(callee));
		}
		
		// If we did not find any flows for an interface, we take all flows from
		// all implementors
		if (flowsInCallee.isEmpty()) {
			for (SootMethod implementor : getAllImplementors(method))
				flowsInCallee.addAll(flows.getMethodFlows(implementor));
		}
		
		// If we have no data flows, we can abort early
		if (flowsInCallee.isEmpty())
			return Collections.emptySet();
				
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
	
	private Collection<SootMethod> getAllImplementors(SootMethod method) {
		final String subSig = method.getSubSignature();
		Set<SootMethod> implementors = new HashSet<SootMethod>();
		
		List<SootClass> workList = new ArrayList<SootClass>();
		workList.add(method.getDeclaringClass());
		Set<SootClass> doneSet = new HashSet<SootClass>();
		
		while (!workList.isEmpty()) {
			SootClass curClass = workList.remove(0);
			if (!doneSet.add(curClass))
				continue;
			
			if (curClass.isInterface())
				workList.addAll(hierarchy.getImplementersOfUnsafe(curClass));
			else
				workList.addAll(hierarchy.getSubclassesOfUnsafe(curClass));
			
			SootMethod ifm = curClass.getMethodUnsafe(subSig);
			if (ifm != null)
				implementors.add(ifm);
		}
		
		return implementors;
	}
	
	/**
	 * Gets an over-approximation of all methods that can be called when the
	 * given method is called. This includes all methods with matching signatures
	 * in all parent classes as well as those in the implemented interfaces.
	 * @param method The method for which to get the targets
	 * @return The targets that could be called for the given method
	 */
	private Set<SootMethod> getAllCallees(SootMethod method) {
		Set<SootMethod> callees = new HashSet<SootMethod>();
		final String subSig = method.getSubSignature();
		
		List<SootClass> workList = new ArrayList<SootClass>();
		workList.add(method.getDeclaringClass());
		Set<SootClass> doneSet = new HashSet<SootClass>();
		
		while (!workList.isEmpty()) {
			SootClass ifc = workList.remove(0);
			if (!doneSet.add(ifc))
				continue;
			
			SootMethod ifm = ifc.getMethodUnsafe(subSig);
			if (ifm != null)
				callees.add(ifm);
			
			if (ifc.hasSuperclass())
				workList.add(ifc.getSuperclass());
			workList.addAll(ifc.getInterfaces());
		}
		
		return callees;
	}
	
	/**
	 * Applies a data flow summary to a given tainted access path
	 * @param flow The data flow summary to apply
	 * @param stmt The call site that calls the summarized library method
	 * @param curAP The currently tainted access path
	 * @return The access path obtained by applying the given data flow summary
	 * to the given access path. if the summary is not applicable, null is
	 * returned.
	 */
	private AccessPath applyFlow(MethodFlow flow, Stmt stmt, AccessPath curAP) {		
		final FlowSource flowSource = flow.source();
		final FlowSink flowSink = flow.sink();
		
		// Make sure that the base type of the incoming taint and the one of
		// the summary are compatible
		boolean typesCompatible = isCastCompatible(curAP.getBaseType(),
				getTypeFromString(flowSource.getBaseType()));
		if (!typesCompatible)
			return null;

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
			
			Type callType = stmt.getInvokeExpr().getMethod().getDeclaringClass().getType();
			if (taint && compareFields(curAP, flowSource))
				if (isCastCompatible(curAP.getBaseType(), callType))
					return addSinkTaint(flowSource, flowSink, stmt, curAP);
		}
		else if (flowSource.isThis()) {
			Type callType = stmt.getInvokeExpr().getMethod().getDeclaringClass().getType();
			if (curAP.isLocal() || curAP.isInstanceFieldRef())
				if (curAP.getPlainValue().equals(getMethodBase(stmt)))
					if (isCastCompatible(curAP.getBaseType(), callType))
						return addSinkTaint(flowSource, flowSink, stmt, curAP);
		}
		
		// Nothing matched
		return null;
	}
	
	/**
	 * Checks whether the type tracked in the access path is compatible with the
	 * type of the base object expected by the flow summary
	 * @param baseType The base type tracked in the access path
	 * @param checkType The type in the summary
	 * @return True if the tracked base type is compatible with the type expected
	 * by the flow summary, otherwise false
	 */
	private boolean isCastCompatible(Type baseType, Type checkType) {
		return baseType == checkType
				|| fastHierarchy.canStoreType(baseType, checkType)
				|| fastHierarchy.canStoreType(checkType, baseType);
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
		
		SootField sf = Scene.v().grabField(fieldSig);
		if (sf != null)
			return sf;
		
		// This field does not exist, so we need to create it
		String className = fieldSig.substring(1);
		className = className.substring(0, className.indexOf(":"));
		SootClass sc = Scene.v().getSootClassUnsafe(className);
		if (sc.resolvingLevel() < SootClass.SIGNATURES)
			Scene.v().forceResolve(className, SootClass.SIGNATURES);
		
		String type = fieldSig.substring(fieldSig.indexOf(": ") + 2);
		type = type.substring(0, type.indexOf(" "));
		
		String fieldName = fieldSig.substring(fieldSig.lastIndexOf(" ") + 1);
		fieldName = fieldName.substring(0, fieldName.length() - 1);
		
		return Scene.v().makeFieldRef(sc, fieldName, getTypeFromString(type), false).resolve();
	}

	private Type getTypeFromString(String type) {
		if (type.equals("int"))
			return IntType.v();
		else if (type.equals("long"))
			return LongType.v();
		else if (type.equals("float"))
			return FloatType.v();
		else if (type.equals("double"))
			return DoubleType.v();
		else if (type.equals("boolean"))
			return BooleanType.v();
		return RefType.v(type);
	}

	/**
	 * Gets an array of fields with the specified signatures
	 * 
	 * @param fieldSigs
	 *            , list of the field signatures to retrieve
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

	/**
	 * Gets an array of types with the specified class names
	 * 
	 * @param fieldTypes
	 *            , list of the type names to retrieve
	 * @return The Array of fields with the given signature if all exists,
	 *         otherwise null
	 */
	private Type[] safeGetTypes(String[] fieldTypes) {
		if (fieldTypes == null || fieldTypes.length == 0)
			return null;
		Type[] types = new Type[fieldTypes.length];
		for (int i = 0; i < fieldTypes.length; i++)
			types[i] = getTypeFromString(fieldTypes[i]);
		return types;
	}
	
	private AccessPath addSinkTaint(FlowSource flowSource,
			FlowSink flowSink, Stmt stmt, AccessPath taintedPath) {
		boolean taintSubFields = flowSink.taintSubFields()
				|| taintedPath.getTaintSubFields();

		final SootField[] fields = safeGetFields(flowSink.getAccessPath());
		final Type[] fieldTypes = safeGetTypes(flowSink.getAccessPathTypes());
		
		final SootField[] remainingFields = getRemainingFields(flowSource, taintedPath);
		final Type[] remainingFieldTypes = getRemainingFieldTypes(flowSource, taintedPath);

		final SootField[] appendedFields = append(fields, remainingFields);
		final Type[] appendedFieldTypes = append(fieldTypes, remainingFieldTypes);

		// Do we need to taint the return value?
		if (flowSink.isReturn()) {
			// If the return value is never used, we can abort
			if (!(stmt instanceof DefinitionStmt))
				return null;
			
			DefinitionStmt defStmt = (DefinitionStmt) stmt;
			if (flowSink.hasAccessPath()) {								
				return new AccessPath(defStmt.getLeftOp(),
						appendedFields,
						getTypeFromString(flowSink.getBaseType()),
						appendedFieldTypes,
						taintSubFields || fields == null);
			} else
				return new AccessPath(defStmt.getLeftOp(), null,
						getTypeFromString(flowSink.getBaseType()), null,
						taintSubFields);
		}
		// Do we need to taint a field of the base object?
		else if (flowSink.isField()
				&& stmt.containsInvokeExpr()
				&& stmt.getInvokeExpr() instanceof InstanceInvokeExpr) {
			// If we taint something in the base object, its type must match. We
			// might have a taint for "a" in o.add(a) and need to check whether
			// "o" matches the expected type in our summary.
			InstanceInvokeExpr iinv = (InstanceInvokeExpr) stmt.getInvokeExpr();
			if (!fastHierarchy.canStoreType(iinv.getBase().getType(),
					getTypeFromString(flowSink.getBaseType())))
				return null;
			
			return new AccessPath(iinv.getBase(),
					appendedFields,
					getTypeFromString(flowSink.getBaseType()),
					appendedFieldTypes,
					taintSubFields || fields == null);
		}
		// Do we need to taint a field of the parameter?
		else if (flowSink.isParameter()
				&& stmt.containsInvokeExpr()) {
			Value arg = stmt.getInvokeExpr().getArg(flowSink.getParameterIndex());
			if (arg instanceof Local) {
				return new AccessPath(arg,
						appendedFields,
						getTypeFromString(flowSink.getBaseType()),
						appendedFieldTypes,
						taintSubFields);
			}
			else
				throw new RuntimeException("Cannot taint non-local parameter");
		}
		// We dont't know what this is
		else
			throw new RuntimeException("Unknown summary sink type: " + flowSink);
	}
	
	/**
	 * Concatenates the two given arrays to one bigger array
	 * @param fields The first array
	 * @param remainingFields The second array
	 * @return The concatendated array containing all elements from both given
	 * arrays
	 */
	private SootField[] append(SootField[] fields, SootField[] remainingFields) {
		if (fields == null)
			return remainingFields;
		if (remainingFields == null)
			return fields;
		
		int cnt = fields.length + remainingFields.length;
		SootField[] appended = new SootField[cnt];
		System.arraycopy(fields, 0, appended, 0, fields.length);
		System.arraycopy(remainingFields, 0, appended, fields.length, remainingFields.length);
		return appended;
	}

	/**
	 * Concatenates the two given arrays to one bigger array
	 * @param fields The first array
	 * @param remainingFields The second array
	 * @return The concatendated array containing all elements from both given
	 * arrays
	 */
	private Type[] append(Type[] fields, Type[] remainingFields) {
		if (fields == null)
			return remainingFields;
		if (remainingFields == null)
			return fields;
		
		int cnt = fields.length + remainingFields.length;
		Type[] appended = new Type[cnt];
		System.arraycopy(fields, 0, appended, 0, fields.length);
		System.arraycopy(remainingFields, 0, appended, fields.length, remainingFields.length);
		return appended;
	}

	/**
	 * Gets the remaining fields which are tainted, but not covered by the given
	 * flow summary source
	 * @param flowSource The flow summary source
	 * @param taintedPath The tainted access path
	 * @return The remaining fields which are tainted in the given access path,
	 * but which are not covered by the given flow summary source
	 */
	private SootField[] getRemainingFields(FlowSource flowSource, AccessPath taintedPath) {
		if (!flowSource.hasAccessPath())
			return taintedPath.getFields();
		
		int fieldCnt = taintedPath.getFieldCount() - flowSource.getAccessPathLength();
		if (fieldCnt <= 0)
			return null;
		
		SootField[] fields = new SootField[fieldCnt];
		System.arraycopy(taintedPath.getFields(), flowSource.getAccessPathLength(),
				fields, 0, fieldCnt);
		return fields;
	}
	
	/**
	 * Gets the types of the remaining fields which are tainted, but not covered
	 * by the given flow summary source
	 * @param flowSource The flow summary source
	 * @param taintedPath The tainted access path
	 * @return The types of the remaining fields which are tainted in the given
	 * access path, but which are not covered by the given flow summary source
	 */
	private Type[] getRemainingFieldTypes(FlowSource flowSource, AccessPath taintedPath) {
		if (!flowSource.hasAccessPath())
			return taintedPath.getFieldTypes();
		
		int fieldCnt = taintedPath.getFieldCount() - flowSource.getAccessPathLength();
		if (fieldCnt <= 0)
			return null;
		
		Type[] fields = new Type[fieldCnt];
		System.arraycopy(taintedPath.getFieldTypes(), flowSource.getAccessPathLength(),
				fields, 0, fieldCnt);
		return fields;
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
