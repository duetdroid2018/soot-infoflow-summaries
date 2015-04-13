package soot.jimple.infoflow.methodSummary.taintWrappers;

import heros.solver.IDESolver;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import soot.ArrayType;
import soot.BooleanType;
import soot.DoubleType;
import soot.FastHierarchy;
import soot.FloatType;
import soot.Hierarchy;
import soot.IntType;
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
import soot.jimple.infoflow.methodSummary.data.AbstractFlowSinkSource;
import soot.jimple.infoflow.methodSummary.data.FlowSink;
import soot.jimple.infoflow.methodSummary.data.FlowSource;
import soot.jimple.infoflow.methodSummary.data.GapDefinition;
import soot.jimple.infoflow.methodSummary.data.MethodFlow;
import soot.jimple.infoflow.methodSummary.data.SourceSinkType;
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
			loadClass(className);
		for (String className : flows.getSupportedClasses())
			loadClass(className);
		
		// Get the hierarchy
		this.hierarchy = Scene.v().getActiveHierarchy();
		this.fastHierarchy = Scene.v().getOrMakeFastHierarchy();
	}
	
	/**
	 * Loads the class with the given name into the scene. This makes sure that
	 * there is at least a phantom class with the given name
	 * @param className The name of the class to load
	 */
	private void loadClass(String className) {
		SootClass sc = Scene.v().getSootClassUnsafe(className);
		if (sc == null) {
			sc = new SootClass(className);
			sc.setPhantom(true);
			Scene.v().addClass(sc);
		}
		else if (sc.resolvingLevel() < SootClass.HIERARCHY)
			Scene.v().forceResolve(className, SootClass.HIERARCHY);
	}
	
	/**
	 * Class representing a tainted item during propagation
	 * 
	 * @author Steven Arzt
	 *
	 */
	private class Taint extends FlowSink implements Cloneable {

		public Taint(SourceSinkType type, int paramterIdx, String baseType,
				boolean taintSubFields) {
			super(type, paramterIdx, baseType, taintSubFields);
		}
		
		public Taint(SourceSinkType type, int paramterIdx,
				String baseType, String[] fields, String[] fieldTypes,
				boolean taintSubFields) {
			super(type, paramterIdx, baseType, fields, fieldTypes, taintSubFields);
		}
		
		public Taint(SourceSinkType type, int paramterIdx,
				String baseType, String[] fields, String[] fieldTypes,
				boolean taintSubFields, GapDefinition gap) {
			super(type, paramterIdx, baseType, fields, fieldTypes, taintSubFields, gap);
		}
		
		@Override
		public Taint clone() {
			return new Taint(getType(), getParameterIndex(), getBaseType(),
					getAccessPath(), getAccessPathTypes(), taintSubFields());
		}
		
	}
	
	/**
	 * Class for describing an element at the frontier of the access path
	 * propagation tree
	 * 
	 * @author Steven Arzt
	 *
	 */
	private class AccessPathPropagator {
		
		private final Taint taint;
		private final GapDefinition gap;
		private final AccessPathPropagator parent;
		
		public AccessPathPropagator(Taint taint) {
			this(taint, null, null);
		}
		
		public AccessPathPropagator(Taint taint,
				GapDefinition gap,
				AccessPathPropagator parent) {
			this.taint = taint;
			this.gap = gap;
			this.parent = parent;
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
			return true;
		}
		
	}
	
	/**
	 * Creates a taint that can be propagated through method summaries based on
	 * the given access path
	 * @param ap The access path from which to create a taint
	 * @param stmt The statement at which the access path came in
	 * @param icfg The interprocedural control flow graph
	 * @return The taint derived from the given access path
	 */
	private Taint createTaintFromAccessPath(AccessPath ap, Stmt stmt, IInfoflowCFG icfg) {
		SootMethod sm = icfg.getMethodOf(stmt);
		Value base = getMethodBase(stmt);
		
		// Check whether the base object or some field in it is tainted
		if (!sm.isStatic()
				&& (ap.isLocal() || ap.isInstanceFieldRef())
				&& base != null
				&& base == ap.getPlainValue()) {
			return new Taint(SourceSinkType.Field,
					-1,
					ap.getBaseType().toString(),
					fieldArrayToStringArray(ap.getFields()),
					typeArrayToStringArray(ap.getFieldTypes()),
					ap.getTaintSubFields());
		}
		
		// Check whether a parameter is tainted
		int paramIdx = getParameterIndex(stmt, ap);
		if (paramIdx >= 0)
			return new Taint(SourceSinkType.Parameter,
					paramIdx,
					ap.getBaseType().toString(),
					fieldArrayToStringArray(ap.getFields()),
					typeArrayToStringArray(ap.getFieldTypes()),
					ap.getTaintSubFields());
		
		// We haven't found any corresponding taint
		throw new RuntimeException("Could not create taint for access path "
				+ ap + " at " + stmt);
	}
	
	/**
	 * Converts a taint back into an access path that is valid at the given
	 * statement
	 * @param t The taint to convert into an access path
	 * @param stmt The statement at which the access path shall be valid
	 * @param icfg The interprocedural control flow graph
	 * @return The access path derived from the given taint
	 */
	private AccessPath createAccessPathFromTaint(Taint t, Stmt stmt, IInfoflowCFG icfg) {
		// Convert the taints to Soot objects
		SootField[] fields = safeGetFields(t.getAccessPath());
		Type[] types = safeGetTypes(t.getAccessPathTypes());
		Type baseType = getTypeFromString(t.getBaseType());
		
		// If the taint is a return value, we taint the left side of the
		// assignment
		if (t.isReturn()) {
			// If the return value is not used, we can abort
			if (!(stmt instanceof DefinitionStmt))
				return null;
			
			DefinitionStmt defStmt = (DefinitionStmt) stmt;
			return new AccessPath(defStmt.getLeftOp(),
					fields, baseType, types, t.taintSubFields());
		}
		
		// If the taint is a parameter value, we need to identify the
		// corresponding local
		if (t.isParameter() && stmt.containsInvokeExpr()) {
			InvokeExpr iexpr = stmt.getInvokeExpr();
			return new AccessPath(iexpr.getArg(t.getParameterIndex()),
					fields, baseType, types, t.taintSubFields());
		}
		
		// If the taint is on the base value, we need to taint the base local
		if (t.isField()
				&& stmt.containsInvokeExpr()
				&& stmt.getInvokeExpr() instanceof InstanceInvokeExpr) {
			InstanceInvokeExpr iiexpr = (InstanceInvokeExpr) stmt.getInvokeExpr();
			return new AccessPath(iiexpr.getBase(),
					fields, baseType, types, t.taintSubFields());
		}
		
		throw new RuntimeException("Could not convert taint to access path: "
				+ t + " at " + stmt);
	}
	
	/**
	 * Converts an array of SootFields to an array of strings
	 * @param fields The array of SootFields to convert
	 * @return The array of strings corresponding to the given array of
	 * SootFields
	 */
	private String[] fieldArrayToStringArray(SootField[] fields) {
		if (fields == null)
			return null;
		String[] stringFields = new String[fields.length];
		for (int i = 0; i < fields.length; i++)
			stringFields[i] = fields[i].toString();
		return stringFields;
	}
	
	/**
	 * Converts an array of Soot Types to an array of strings
	 * @param fields The array of Soot Types to convert
	 * @return The array of strings corresponding to the given array of
	 * Soot Types
	 */
	private String[] typeArrayToStringArray(Type[] types) {
		if (types == null)
			return null;
		String[] stringTypes = new String[types.length];
		for (int i = 0; i < types.length; i++)
			stringTypes[i] = types[i].toString();
		return stringTypes;
	}
	
	@Override
	public Set<AccessPath> getTaintsForMethodInternal(Stmt stmt,
			AccessPath taintedPath, IInfoflowCFG icfg) {		
		// We only care about method invocations
		if (!stmt.containsInvokeExpr())
			return Collections.singleton(taintedPath);
		
		// We always retain the incoming taint
		Set<AccessPath> res = new HashSet<AccessPath>();
		res.add(taintedPath);
		
		// Get the cached data flows
		final SootMethod method = stmt.getInvokeExpr().getMethod();
		Set<MethodFlow> flowsInCallee = getFlowSummariesForMethod(stmt, icfg,
				method);
		
		// If we have no data flows, we can abort early
		if (flowsInCallee.isEmpty())
			return Collections.emptySet();
		
		// Create a level-0 propagator for the initially tainted access path
		List<AccessPathPropagator> workList = new ArrayList<AccessPathPropagator>();
		workList.add(new AccessPathPropagator(createTaintFromAccessPath(
				taintedPath, stmt, icfg)));
		
		// Apply the data flows until we reach a fixed point
		Set<AccessPathPropagator> doneSet = new HashSet<AccessPathPropagator>();
		while (!workList.isEmpty()) {
			final AccessPathPropagator curPropagator = workList.remove(0);
			final GapDefinition curGap = curPropagator.getGap();
			
			// Make sure we don't have invalid data
			if (curGap != null && curPropagator.getParent() == null)
				throw new RuntimeException("Gap flow without parent detected");
			
			// Get the correct set of flows to apply
			Set<MethodFlow> flowsInTarget = curGap == null ? flowsInCallee
					: getFlowSummariesForGap(icfg, curGap);
			
			for (MethodFlow flow : flowsInTarget) {
				AccessPathPropagator newPropagator = applyFlow(flow, curPropagator);
				if (newPropagator == null)
					continue;
				
				// Propagate it
				if (newPropagator.getParent() == null
						&& newPropagator.getTaint().getGap() == null) {
					AccessPath ap = createAccessPathFromTaint(newPropagator.getTaint(),
							stmt, icfg);
					if (ap == null)
						continue;
					else
						res.add(ap);
				}
				if (doneSet.add(newPropagator))
					workList.add(newPropagator);
			}
		}
		
		return res;
	}
	
	private AccessPathPropagator safePopParent(AccessPathPropagator curPropagator) {
		if (curPropagator.parent == null)
			return null;
		return curPropagator.parent.parent;
	}

	private Set<MethodFlow> getFlowSummariesForGap(IInfoflowCFG icfg,
			GapDefinition gap) {
		// If we have the method in Soot, we can be more clever
		if (Scene.v().containsMethod(gap.getSignature())) {
			SootMethod gapMethod = Scene.v().getMethod(gap.getSignature());
			return getFlowSummariesForMethod(null, icfg, gapMethod);
		}
		
		// If we don't have the method, we can only directly look for the
		// signature
		return flows.getMethodFlows(gap.getSignature());
	}
	
	private Set<MethodFlow> getFlowSummariesForMethod(Stmt stmt,
			IInfoflowCFG icfg, final SootMethod method) {
		Set<MethodFlow> flowsInCallee = methodToFlows.getUnchecked(method);
		
		// If we have no direct entry, check the CG
		if (flowsInCallee.isEmpty() && stmt != null) {
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
		return flowsInCallee;
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
				workList.addAll(hierarchy.getImplementersOf(curClass));
			else
				workList.addAll(hierarchy.getSubclassesOf(curClass));
			
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
	 * @param propagator The access path propagator on which to apply the given
	 * flow
	 * @return The access path propagator obtained by applying the given data
	 * flow summary to the given access path propagator. if the summary is not
	 * applicable, null is returned.
	 */
	private AccessPathPropagator applyFlow(MethodFlow flow, AccessPathPropagator propagator) {		
		final FlowSource flowSource = flow.source();
		final FlowSink flowSink = flow.sink();
		final Taint taint = propagator.getTaint();
		
		// Make sure that the base type of the incoming taint and the one of
		// the summary are compatible
		boolean typesCompatible = isCastCompatible(
				getTypeFromString(taint.getBaseType()),
				getTypeFromString(flowSource.getBaseType()));
		if (!typesCompatible)
			return null;
		
		// If this flow starts at a gap, our current taint must be at that gap
		if (taint.getGap() != flow.source().getGap())
			return null;
		
		// Maintain the stack of access path propagations
		final AccessPathPropagator parent;
		final GapDefinition gap;
		if (flow.sink().getGap() != null) {	// ends in gap, push on stack
			parent = propagator;
			gap = flow.sink().getGap();
		}
		else if (flow.source().getGap() != null) { // starts in gap, propagates inside method
			parent = propagator.parent;
			gap = propagator.gap;
		}
		else {
			parent = safePopParent(propagator);
			gap = propagator.parent == null ? null : propagator.parent.gap;
		}
		
		boolean addTaint = false;
		if (flowSource.isParameter() && taint.isParameter()) {
			// Get the parameter index from the call and compare it to the
			// parameter index in the flow summary
			if (taint.getParameterIndex() == flowSource.getParameterIndex()) {
				if (compareFields(taint, flowSource))
					addTaint = true;
			}
		}
		else if (flowSource.isField()) {
			// Flows from a field can either be applied to the same field or
			// the base object in total
			boolean doTaint = (taint.isGapBaseObject() || taint.isField());
			if (doTaint && compareFields(taint, flowSource))
				addTaint = true;
		}
		// We can have a flow from a local or a field
		else if (flowSource.isThis() && taint.isField())
			addTaint = true;
		// A value can also flow from the return value of a gap to somewhere
		else if (flowSource.isReturn()
				&& flowSource.getGap() != null
				&& taint.getGap() != null)
			addTaint = true;			
		
		// If we didn't find a match, there's little we can do
		if (!addTaint)
			return null;
		
		// Construct a new propagator
		Taint newTaint = addSinkTaint(flowSource, flowSink, taint,
				propagator.getGap());
		if (newTaint == null)
			return null;
		
		AccessPathPropagator newPropagator = new AccessPathPropagator(newTaint,
				gap, parent);
		return newPropagator;
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
	
	/**
	 * Checks whether the fields mentioned in the given taint correspond to
	 * those of the given flow source
	 * @param taintedPath The tainted access path
	 * @param flowSource The flow source with which to compare the taint
	 * @return True if the given taint references the same fields as the
	 * given flow source, otherwise false
	 */
	private boolean compareFields(Taint taintedPath, FlowSource flowSource) {
		// If a is tainted, the summary must match a. If a.* is tainted, the
		// summary can also be a.b.
		if (taintedPath.getFieldCount() == 0)
			return !flowSource.isField() || taintedPath.taintSubFields();
		
		// if we have x.f....fn and the source is x.f'.f1'...f'n+1 and we don't
		// taint sub, we can't have a match
		if (taintedPath.getFieldCount() < flowSource.getAccessPathLength()
				&& !taintedPath.taintSubFields())
			return false;
		
		// Compare the shared sub-path
		for (int i = 0; i < taintedPath.getFieldCount()
				&& i < flowSource.getAccessPathLength(); i++) {
			String taintField = taintedPath.getAccessPath()[i];
			String sourceField = flowSource.getAccessPath()[i];
			if (!sourceField.equals(taintField))
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
		if (sc.resolvingLevel() < SootClass.SIGNATURES
				&& !sc.isPhantom()) {
			System.err.println("WARNING: Class not loaded: " + sc);
			return null;
		}
		
		String type = fieldSig.substring(fieldSig.indexOf(": ") + 2);
		type = type.substring(0, type.indexOf(" "));
		
		String fieldName = fieldSig.substring(fieldSig.lastIndexOf(" ") + 1);
		fieldName = fieldName.substring(0, fieldName.length() - 1);
		
		return Scene.v().makeFieldRef(sc, fieldName, getTypeFromString(type), false).resolve();
	}
	
	/**
	 * Creates a Soot Type from the given string
	 * @param type A string representing a Soot type
	 * @return The Soot Type corresponding to the given string
	 */
	private Type getTypeFromString(String type) {		
		// Reduce arrays
		int numDimensions = 0;
		while (type.endsWith("[]")) {
			numDimensions++;
			type = type.substring(0, type.length() - 2);
		}
		
		// Generate the target type
		final Type t;
		if (type.equals("int"))
			t = IntType.v();
		else if (type.equals("long"))
			t = LongType.v();
		else if (type.equals("float"))
			t = FloatType.v();
		else if (type.equals("double"))
			t = DoubleType.v();
		else if (type.equals("boolean"))
			t = BooleanType.v();
		else
			t = RefType.v(type);
		
		if (numDimensions == 0)
			return t;
		return ArrayType.v(t, numDimensions);
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
	
	/**
	 * Given the taint at the source and the flow, computes the taint at the
	 * sink
	 * @param flowSource The source definition of the flow
	 * @param flowSink The sink definition of the flow
	 * @param taint The taint at the source statement
	 * @param gap The gap at which the new flow will hold
	 * @return The taint at the sink that is obtained when applying the given
	 * flow to the given source taint
	 */
	private Taint addSinkTaint(FlowSource flowSource,
			FlowSink flowSink, Taint taint, GapDefinition gap) {
		boolean taintSubFields = flowSink.taintSubFields()
				|| taint.taintSubFields();
		
		final String[] remainingFields = getRemainingFields(flowSource, taint);
		final String[] remainingFieldTypes = getRemainingFieldTypes(flowSource, taint);

		final String[] appendedFields = append(flowSink.getAccessPath(), remainingFields);
		final String[] appendedFieldTypes = append(flowSink.getAccessPathTypes(), remainingFieldTypes);

		// If we taint something in the base object, its type must match. We
		// might have a taint for "a" in o.add(a) and need to check whether
		// "o" matches the expected type in our summary.
		int lastCommonAPIdx = Math.min(flowSource.getAccessPathLength(), taint.getAccessPathLength());
		Type sinkType = getTypeFromString(getAssignmentType(flowSink));
		Type taintType = getTypeFromString(getAssignmentType(taint, lastCommonAPIdx - 1));
		if (!isCastCompatible(taintType, sinkType)) {
			// If the target is an array, the value might also flow into an element
			Type sinkBaseType = sinkType;
			boolean found = false;
			while (sinkBaseType instanceof ArrayType) {
				sinkBaseType = ((ArrayType) sinkBaseType).getElementType(); 
				if (isCastCompatible(taintType, sinkBaseType)) {
					found = true;
					break;
				}
			}
			if (!found)
				return null;
		}

		// Taint the correct fields
		return new Taint(flowSink.getType(),
				flowSink.getParameterIndex(),
				flowSink.getBaseType(),
				appendedFields,
				appendedFieldTypes,
				taintSubFields,
				gap);
	}
	
	/**
	 * Gets the type at the given position from a taint.
	 * @param taint The taint from which to get the propagation type
	 * @param idx The index inside the access path from which to get the type.
	 * -1 refers to the base type
	 * @return The type at the given index inside the access path
	 */
	private String getAssignmentType(Taint taint, int idx) {
		if (idx < 0)
			return taint.getBaseType();
		return taint.getAccessPathTypes()[idx];
	}
	
	/**
	 * Gets the type that is finally assigned when propagating this source or
	 * sink. For an access path a.b.c, this would be the type of "c".
	 * @param srcSink The source or sink from which to get the propagation type
	 * @return The type of the value which the access path of the given source
	 * or sink finally references
	 */
	private String getAssignmentType(AbstractFlowSinkSource srcSink) {
		if (!srcSink.hasAccessPath())
			return srcSink.getBaseType();
		return srcSink.getAccessPathTypes()[srcSink.getAccessPathLength() - 1];
	}
	
	/**
	 * Concatenates the two given arrays to one bigger array
	 * @param fields The first array
	 * @param remainingFields The second array
	 * @return The concatenated array containing all elements from both given
	 * arrays
	 */
	private String[] append(String[] fields, String[] remainingFields) {
		if (fields == null)
			return remainingFields;
		if (remainingFields == null)
			return fields;
		
		int cnt = fields.length + remainingFields.length;
		String[] appended = new String[cnt];
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
	private String[] getRemainingFields(FlowSource flowSource, Taint taintedPath) {
		if (!flowSource.hasAccessPath())
			return taintedPath.getAccessPath();
		
		int fieldCnt = taintedPath.getFieldCount() - flowSource.getAccessPathLength();
		if (fieldCnt <= 0)
			return null;
		
		String[] fields = new String[fieldCnt];
		System.arraycopy(taintedPath.getAccessPath(), flowSource.getAccessPathLength(),
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
	private String[] getRemainingFieldTypes(FlowSource flowSource, Taint taintedPath) {
		if (!flowSource.hasAccessPath())
			return taintedPath.getAccessPathTypes();
		
		int fieldCnt = taintedPath.getFieldCount() - flowSource.getAccessPathLength();
		if (fieldCnt <= 0)
			return null;
		
		String[] fields = new String[fieldCnt];
		System.arraycopy(taintedPath.getAccessPathTypes(), flowSource.getAccessPathLength(),
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


// TODO: Test cases for appending fields
// TODO: Test cases for type checking with lists
// TODO: Test case for return in field / return direct
// TODO: Test accessing a tainted field in summarized method through an alias in user code
// TODO: Gap base object from field + aliasing
