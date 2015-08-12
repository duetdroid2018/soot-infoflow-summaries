package soot.jimple.infoflow.methodSummary.postProcessor;

import heros.solver.Pair;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import soot.ArrayType;
import soot.Local;
import soot.PrimType;
import soot.RefLikeType;
import soot.RefType;
import soot.Scene;
import soot.SootField;
import soot.SootMethod;
import soot.Type;
import soot.Unit;
import soot.Value;
import soot.jimple.AssignStmt;
import soot.jimple.Constant;
import soot.jimple.DefinitionStmt;
import soot.jimple.InstanceFieldRef;
import soot.jimple.InstanceInvokeExpr;
import soot.jimple.LengthExpr;
import soot.jimple.ReturnStmt;
import soot.jimple.Stmt;
import soot.jimple.infoflow.data.Abstraction;
import soot.jimple.infoflow.data.AbstractionAtSink;
import soot.jimple.infoflow.data.AccessPath;
import soot.jimple.infoflow.data.AccessPath.BasePair;
import soot.jimple.infoflow.methodSummary.data.factory.SourceSinkFactory;
import soot.jimple.infoflow.methodSummary.data.sourceSink.FlowSink;
import soot.jimple.infoflow.methodSummary.data.sourceSink.FlowSource;
import soot.jimple.infoflow.methodSummary.data.summary.GapDefinition;
import soot.jimple.infoflow.methodSummary.data.summary.MethodFlow;
import soot.jimple.infoflow.methodSummary.data.summary.MethodSummaries;
import soot.jimple.infoflow.methodSummary.generator.GapManager;
import soot.jimple.infoflow.methodSummary.postProcessor.SummaryPathBuilder.SummaryResultInfo;
import soot.jimple.infoflow.methodSummary.postProcessor.SummaryPathBuilder.SummarySourceInfo;
import soot.jimple.infoflow.solver.cfg.IInfoflowCFG;
import soot.jimple.infoflow.util.BaseSelector;
import soot.jimple.infoflow.util.SootMethodRepresentationParser;
import soot.util.MultiMap;

public class InfoflowResultPostProcessor {
	private final boolean DEBUG = true;
	private final Logger logger = LoggerFactory.getLogger(InfoflowResultPostProcessor.class);

	private final IInfoflowCFG cfg;
	private final MultiMap<Abstraction, Stmt> collectedAbstractions;
	private final String method;
	private final SourceSinkFactory sourceSinkFactory;
	private final GapManager gapManager;
		
	public InfoflowResultPostProcessor(MultiMap<Abstraction, Stmt> collectedAbstractions,
			IInfoflowCFG cfg, String m, SourceSinkFactory sourceSinkFactory,
			GapManager gapManager) {
		this.collectedAbstractions = collectedAbstractions;
		this.cfg = cfg;
		this.method = m;
		this.sourceSinkFactory = sourceSinkFactory;
		this.gapManager = gapManager;
	}
	
	/**
	 * Post process the information collected during a Infoflow analysis.
	 * Extract all summary flow from collectedAbstractions.
	 * @return The generated method summaries
	 */
	public MethodSummaries postProcess() {
		MethodSummaries summaries = new MethodSummaries();
		postProcess(summaries);
		return summaries;
	}
	
	/**
	 * Post process the information collected during a Infoflow analysis.
	 * Extract all summary flow from collectedAbstractions.
	 * @param flos The method summary object in which to store the detected
	 * flows
	 */
	public MethodSummaries postProcess(MethodSummaries flows) {
		logger.info("start processing {} infoflow abstractions for method {}", 
				collectedAbstractions.size(), method);
		final SootMethod m = Scene.v().getMethod(method);
				
		// Create a context-sensitive path builder. Without context-sensitivity,
		// we get quite some false positives here.
		SummaryPathBuilder pathBuilder = new SummaryPathBuilder(cfg,
				Runtime.getRuntime().availableProcessors());
		
		int analyzedPaths = 0;
		int abstractionCount = 0;
		for (Abstraction a : collectedAbstractions.keySet()) {
			// If this abstraction is directly the source abstraction, we do not
			// need to construct paths
			if (a.getSourceContext() != null) {
				for (Stmt stmt : collectedAbstractions.get(a)) {
					// Make sure that we do not get a fake alias of a primitive value
					// from a gap.
					if (gapManager.getGapForCall(a.getSourceContext().getStmt()) != null) {
						if (!isAliasedField(a.getAccessPath(), a.getSourceContext().getAccessPath(),
								a.getSourceContext().getStmt()))
							continue;
					}
					
					processFlowSource(flows, m, a.getAccessPath(), stmt,
							pathBuilder.new SummarySourceInfo(a.getAccessPath(), a.getCurrentStmt(),
									a.getSourceContext().getUserData(),
									Collections.singletonList(a)));
				}
			}
			else {
				// In case we have the same abstraction in multiple places and we
				// extend it with external sink information regardless of the
				// original propagation, we need to clean up first
				a.clearPathCache();
				
				// Get the source info and process the flow
				pathBuilder.clear();
				pathBuilder.computeTaintPaths(Collections.singleton(
						new AbstractionAtSink(a, a.getCurrentStmt())));
				logger.info("Obtained {} source-to-sink connections.",
						pathBuilder.getResultInfos().size());
				
				// Reconstruct the sources
				for (Stmt stmt : collectedAbstractions.get(a)) {
					abstractionCount++;
					
					// If this abstraction is directly the source abstraction, we do not
					// need to construct paths
					if (a.getSourceContext() != null) {
						continue;
					}
					
					for (SummaryResultInfo si : pathBuilder.getResultInfos()) {
						final AccessPath sourceAP = si.getSourceInfo().getAccessPath();
						final AccessPath sinkAP = si.getSinkInfo().getAccessPath();
						final Stmt sourceStmt = si.getSourceInfo().getSource();
						
						// Check that we don't get any weird results
						if (sourceAP == null || sinkAP == null)
							throw new RuntimeException("Invalid access path");
						
						// We only take flows which are not identity flows.
						// If we have a flow from a gap parameter to the original
						// method parameter, the access paths are equal, but that's
						// ok in the case of aliasing.
						boolean isAliasedField = gapManager.getGapForCall(sourceStmt) != null
								&& isAliasedField(sinkAP, sourceAP, sourceStmt);
						if (!sinkAP.equals(sourceAP) || isAliasedField) {
							// Process the flow from this source
							processFlowSource(flows, m, sinkAP, stmt, si.getSourceInfo());
							analyzedPaths++;
						}
						
						// Clean up our stuff
						for (Abstraction abs : si.getSourceInfo().getAbstractionPath())
							abs.clearPathCache();
					}
				}
				
				// Free some memory
				pathBuilder.clear();
			}
		}
		
		pathBuilder.shutdown();
		
		// Compact the flow set to remove paths that are over-approximations of
		// other flows
		compactFlowSet(flows);
		
		// Check the generated summaries for validity
		flows.validate();
		
		logger.info("Result processing finished, analyzed {} paths from {} stored "
				+ "abstractions", analyzedPaths, abstractionCount);
		return flows;
	}
	
	/**
	 * Checks whether the two given access paths may alias at the given
	 * statement
	 * @param apAtSink The first access path
	 * @param apAtSource The second access path
	 * @param sourceStmt The statement at which to check for may-alias
	 * @return True if the two given access paths may alias at the given
	 * statement, otherwise false
	 */
	private boolean isAliasedField(AccessPath apAtSink,
			AccessPath apAtSource, Stmt sourceStmt) {
		// Only reference types can have aliases
		if (!(apAtSink.getLastFieldType() instanceof RefType))
			return false;
		
		// Return values are always passed on, regardless of aliasing
		if (sourceStmt instanceof DefinitionStmt)
			if (((DefinitionStmt) sourceStmt).getLeftOp() == apAtSource.getPlainValue())
				return true;
		
		// Strings are immutable
		RefType rt = (RefType) apAtSink.getLastFieldType();
		if (rt == RefType.v("java.lang.String"))
			return false;
		
		return true;
	}

	/**
	 * Compacts the flow set by removing flows that are over-approximations of
	 * others
	 * @param flows The flow set to compact
	 */
	private void compactFlowSet(MethodSummaries flows) {
		int flowsRemoved = 0;
		boolean hasChanged = false;
		do {
			hasChanged = false;
			for (Iterator<MethodFlow> flowIt = flows.iterator(); flowIt.hasNext(); ) {
				MethodFlow flow = flowIt.next();
				
				// Check if there is a more precise flow
				for (MethodFlow flow2 : flows)
					if (flow != flow2 && flow.isCoarserThan(flow2)) {
						flowIt.remove();
						flowsRemoved++;
						hasChanged = true;
						break;
					}
				
				if (hasChanged)
					break;
			}
		} while (hasChanged);
		
		logger.info("Removed {} flows in favour of more precise ones", flowsRemoved);
		
		// If we only have incoming flows into a gap, but no outgoing ones, we
		// can remove the gap and all its flows altogether
		for (GapDefinition gd : flows.getAllGaps()) {
			if (flows.getOutFlowsForGap(gd).isEmpty()) {
				flows.removeAll(flows.getInFlowsForGap(gd));
				flows.removeGap(gd);
			}
		}
	}

	/**
	 * Processes data from a given flow source that has arrived at a given
	 * statement
	 * @param flows The flows object to which to add the newly found flow
	 * @param ap The access path that has reached the given statement
	 * @param m The method in which the flow has been found
	 * @param stmt The statement at which the flow has arrived
	 * @param source The source from which the flow originated
	 */
	private void processFlowSource(MethodSummaries flows, final SootMethod m,
			AccessPath ap, Stmt stmt, SummarySourceInfo sourceInfo) {
		// Get the source information for this abstraction
		@SuppressWarnings("unchecked")
		Collection<FlowSource> sources = (Collection<FlowSource>) sourceInfo.getUserData();
		if (sources == null || sources.size() == 0)
			throw new RuntimeException("Link to source missing");
		
		// We can have multiple sources from a gap a.foo(b,b) on access path b
		for (FlowSource flowSource : sources) {
			if (flowSource == null)
				continue;
			
			// We need to reconstruct the original source access path
			Pair<AccessPath, Boolean> sourcePair = reconstructSourceAP(
					ap, sourceInfo.getAbstractionPath(), cfg.getMethodOf(stmt));
			if (sourcePair == null) {
				System.out.println("failed for: " + ap);
				return;
			}
			
			AccessPath sourceAP = sourcePair.getO1();
			boolean isAlias = sourcePair.getO2();
			
			// Create the flow source data object
			flowSource = sourceSinkFactory.createSource(flowSource.getType(),
					flowSource.getParameterIndex(), sourceAP, flowSource.getGap());
			
			// Depending on the statement at which the flow ended, we need to create
			// a different type of summary
			if (cfg.isExitStmt(stmt))
				processAbstractionAtReturn(flows, ap, m, flowSource, stmt, sourceAP, isAlias);
			else if (cfg.isCallStmt(stmt))
				processAbstractionAtCall(flows, ap, flowSource, stmt, sourceAP, isAlias);
			else
				throw new RuntimeException("Invalid statement for flow "
						+ "termination: " + stmt);
		}
	}
	
	/**
	 * Reconstructs the original access path for the given abstraction and
	 * propagation path. If for instance this.a.* was tainted and we have
	 * the following code
	 * 
	 * <code>
	 * 		b = this.a;
	 * 		c = b.x;
	 * 		d = c.y;
	 * 		return d;
	 * </code>
	 * 
	 * We reconstruct that the value being returned is in fact this.a.x.y.
	 * 
	 * @param sinkAP The final access path at the end of the propagation path
	 * @param path The propagation path
	 * @param startMethod The method in which the sink access path was recorded
	 * @return A pair consisting of the fully reconstructed access path and a
	 * boolean indicating whether the flow is a normal data flow (false) or
	 * whether the object referenced by sinkAP is an alias of the one referenced
	 * by the source AP (true).
	 */
	private Pair<AccessPath, Boolean> reconstructSourceAP(AccessPath sinkAP, List<Abstraction> path,
			SootMethod startMethod) {
		// Dump the path
		List<Stmt> stmts = new ArrayList<Stmt>();
		for (Abstraction abs : path)
			stmts.add(abs.getCurrentStmt());
		
		// Only heap objects can alias. Strings are immutable and thus never
		// alias
		boolean isAlias = true;
		{
			Type lastFieldType = sinkAP.getLastFieldType();
			if (lastFieldType instanceof RefType) {
				isAlias = !((RefType) lastFieldType).getSootClass()
						.getName().equals("java.lang.String");
			}
			else
				isAlias = lastFieldType instanceof RefLikeType;
		}
		
		// TODO: Static fields?
		
		List<SootMethod> callees = new ArrayList<>();
		AccessPath curAP = sinkAP;
		for (int pathIdx = path.size() - 1; pathIdx >= 0; pathIdx--) {
			final Abstraction abs = path.get(pathIdx);
			final Stmt stmt = abs.getCurrentStmt();
			final Stmt callSite = abs.getCorrespondingCallSite();
			boolean matched = false;
			
			// If we have reached the source definition, we can directly take
			// the access path
			if (stmt == null && abs.getSourceContext() != null)
				return new Pair<>(abs.getSourceContext().getAccessPath(), isAlias);
			
			// In case of a call-to-return edge, we have no information about
			// what happened in the callee, so we take the incoming access path
			if (stmt.containsInvokeExpr()) {
				if (callSite == stmt) {
					// only change base local
					Value newBase = (pathIdx > 0) ? path.get(pathIdx - 1).getAccessPath().getPlainValue()
							: abs.getAccessPath().getPlainValue();
					
					// If the incoming value is a primitive, we reset the field
					// list
					if (newBase.getType() instanceof PrimType)
						curAP = new AccessPath(newBase, true);
					else
						curAP = curAP.copyWithNewValue(newBase);
					matched = true;
				}
			}
			
			if (matched)
				continue;
			
			// Our call stack may run empty if we have a follow-returns-past-seeds case
			if (stmt.containsInvokeExpr()) {
				// Forward propagation, backwards reconstruction: We leave
				// methods when we reach the call site.
				Collection<SootMethod> curCallees = callees.isEmpty() ? null
						: Collections.singleton(callees.remove(0));
				if (curCallees == null && pathIdx < path.size() - 1)
					curCallees = Collections.singleton(cfg.getMethodOf(
							path.get(pathIdx + 1).getCurrentStmt()));
				if (curCallees == null)
					curCallees = cfg.getCalleesOfCallAt(stmt);
				
				// Match the access path from the caller back into the callee
				for (SootMethod callee : curCallees) {
					AccessPath newAP = mapAccessPathBackIntoCaller(curAP, stmt, callee);
					if (newAP != null) {
						curAP = newAP;
						matched = true;
						break;
					}
				}
				
				// If none of the possible callees worked, we're in trouble
				if (!matched)
					return null;
			}
			else if (callSite != null && callSite.containsInvokeExpr()) {
				// Forward propagation, backwards reconstruction: We enter
				// methods at the return site when we have a corresponding call site.
				SootMethod callee = cfg.getMethodOf(stmt);
				if (callees.isEmpty() || callee != callees.get(0))
					callees.add(0, callee);
				
				// Map the access path into the scope of the callee
				AccessPath newAP = mapAccessPathIntoCallee(curAP, stmt, callSite, callee,
						!abs.isAbstractionActive());
				if (newAP != null) {
					curAP = newAP;
					matched = true;
				}
				else
					return null;
			}
			else if (stmt instanceof AssignStmt) {
				final AssignStmt assignStmt = (AssignStmt) stmt;
				final Value leftOp = BaseSelector.selectBase(assignStmt.getLeftOp(), false);
				final Value rightOp = BaseSelector.selectBase(assignStmt.getRightOp(), false);
				
				// If the access path must matches on the left side, we
				// continue with the value from the right side.
				if (leftOp instanceof Local
						&& leftOp == curAP.getPlainValue()
						&& !assignStmt.containsInvokeExpr()) {
					// Get the next value from the right side of the assignment
					final Value[] rightOps = BaseSelector.selectBaseList(assignStmt.getRightOp(), false);
					Value rop = null;
					if (rightOps.length == 1)
						rop = rightOps[0];
					else {
						int prevIdx = pathIdx - 1;
						scan : while (pathIdx >= 0) {
							Abstraction prevAbs = path.get(prevIdx);
							Value base = prevAbs.getAccessPath().getPlainValue();
							for (Value rv : rightOps)
								if (base == rv) {
									rop = rv;
									break scan;
								}
						}
					}
					
					curAP = curAP.copyWithNewValue(rop, null, false, true);
					matched = true;
				}
				else if (assignStmt.getLeftOp() instanceof InstanceFieldRef) {
					InstanceFieldRef ifref = (InstanceFieldRef) assignStmt.getLeftOp();
					AccessPath matchedAP = matchAccessPath(curAP, ifref.getBase(), ifref.getField());
					if (matchedAP != null) {
						curAP = matchedAP.copyWithNewValue(assignStmt.getRightOp(),
								matchedAP.getFirstFieldType(), true, false);
						matched = true;
					}
				}
				
				if (matched || abs.isAbstractionActive())
					continue;
				
				// For aliasing relationships, we also need to check the right
				// side
				if (rightOp instanceof Local
						&& rightOp == curAP.getPlainValue()
						&& !assignStmt.containsInvokeExpr()
						&& !(assignStmt.getRightOp() instanceof LengthExpr)) {
					// Get the next value from the right side of the assignment
					final Value[] leftOps = BaseSelector.selectBaseList(assignStmt.getLeftOp(), false);
					Value lop = null;
					if (leftOps.length == 1)
						lop = leftOps[0];
					else {
						int prevIdx = pathIdx - 1;
						scan : while (pathIdx >= 0) {
							Abstraction prevAbs = path.get(prevIdx);
							Value base = prevAbs.getAccessPath().getPlainValue();
							for (Value rv : leftOps)
								if (base == rv) {
									lop = rv;
									break scan;
								}
						}
					}
					
					curAP = curAP.copyWithNewValue(lop, null, false, false);
					matched = true;
				}
				else if (assignStmt.getRightOp() instanceof InstanceFieldRef) {
					InstanceFieldRef ifref = (InstanceFieldRef) assignStmt.getRightOp();
					AccessPath matchedAP = matchAccessPath(curAP, ifref.getBase(), ifref.getField());
					if (matchedAP != null) {
						curAP = matchedAP.copyWithNewValue(assignStmt.getLeftOp(),
								matchedAP.getFirstFieldType(), true, false);
						matched = true;
					}
				}
			}
		}
		return new Pair<>(curAP, isAlias);
	}

	private AccessPath matchAccessPath(AccessPath curAP, Value base, SootField field) {
		// The base object must match in any case
		if (curAP.getPlainValue() != base)
			return null;
				
		// If we have no field, we may have a taint-all flag
		if (curAP.isLocal()) {
			if (curAP.getTaintSubFields() || field == null)
				return new AccessPath(base, field, true);
		}
		
		// If we have a field, it must match
		if (curAP.isInstanceFieldRef()) {
			if (curAP.getFirstField() == field) {
				return curAP;
			}
			else {
				// Get the bases for this type
				final Collection<BasePair> bases =
						AccessPath.getBaseForType(base.getType());
				if (bases != null) {
					for (BasePair xbase : bases) {
						if (xbase.getFields()[0] == field) {
							// Build the access path against which we have
							// actually matched
							SootField[] cutFields = new SootField
									[curAP.getFieldCount() + xbase.getFields().length];
							Type[] cutFieldTypes = new Type[cutFields.length];
							
							System.arraycopy(xbase.getFields(), 0, cutFields, 0, xbase.getFields().length);
							System.arraycopy(curAP.getFields(), 0, cutFields,
									xbase.getFields().length, curAP.getFieldCount());
							
							System.arraycopy(xbase.getTypes(), 0, cutFieldTypes, 0, xbase.getTypes().length);
							System.arraycopy(curAP.getFieldTypes(), 0, cutFieldTypes,
									xbase.getFields().length, curAP.getFieldCount());

							return new AccessPath(curAP.getPlainValue(),
									cutFields, curAP.getBaseType(), cutFieldTypes,
									curAP.getTaintSubFields(), false, false);
						}
					}
				}
				
			}
		}
		
		return null;
	}

	/**
	 * Maps an access path from a call site into the respective callee
	 * @param curAP The current access path in the scope of the caller
	 * @param stmt The statement entering the callee
	 * @param callSite The call site corresponding to the callee
	 * @param callee The callee to enter
	 * @return The new callee-side access path if it exists, otherwise null
	 * if the given access path has no corresponding AP in the scope of the
	 * callee.
	 */
	private AccessPath mapAccessPathIntoCallee(AccessPath curAP,
			final Stmt stmt, final Stmt callSite, SootMethod callee,
			boolean isBackwards) {
		// Map the return value into the scope of the callee
		if (stmt instanceof ReturnStmt) {
			ReturnStmt retStmt = (ReturnStmt) stmt;
			if (callSite instanceof AssignStmt
					&& ((AssignStmt) callSite).getLeftOp() == curAP.getPlainValue()) {
				return curAP.copyWithNewValue(retStmt.getOp());
			}
		}
		
		// Map the "this" fields into the callee
		if (!callee.isStatic() && callSite.getInvokeExpr() instanceof InstanceInvokeExpr) {
			InstanceInvokeExpr iiExpr = (InstanceInvokeExpr) callSite.getInvokeExpr();
			if (iiExpr.getBase() == curAP.getPlainValue()) {
				Local thisLocal = callee.getActiveBody().getThisLocal();
				return curAP.copyWithNewValue(thisLocal);
			}
		}
		
		// Map the parameters into the callee. Note that parameters as
		// such cannot return taints from methods, only fields reachable
		// through them. (nope, not true for alias propagation)
		if (!curAP.isLocal() || isBackwards)
			for (int i = 0; i < callSite.getInvokeExpr().getArgCount(); i++) {
				if (callSite.getInvokeExpr().getArg(i) == curAP.getPlainValue()) {
					Local paramLocal = callee.getActiveBody().getParameterLocal(i);
					return curAP.copyWithNewValue(paramLocal);
				}
			}
				
		// Map the parameters back to arguments when we are entering a method
		// during backwards propagation
		if (!curAP.isLocal() && !isBackwards) {
			SootMethod curMethod = cfg.getMethodOf(stmt);
			for (int i = 0; i < callSite.getInvokeExpr().getArgCount(); i++) {
				Local paramLocal = curMethod.getActiveBody().getParameterLocal(i);
				if (paramLocal == curAP.getPlainValue()) {
					return curAP.copyWithNewValue(callSite.getInvokeExpr().getArg(i),
							curMethod.getParameterType(i), false);
				}
			}
		}
		
		return null;
	}
	
	/**
	 * Matches an access path from the scope of the callee back into the scope
	 * of the caller
	 * @param curAP The access path to map
	 * @param stmt The call statement
	 * @param callee The callee from which we return
	 * @return The new access path in the scope of the caller if applicable,
	 * null if there is no corresponding access path in the caller.
	 */
	private AccessPath mapAccessPathBackIntoCaller(AccessPath curAP,
			final Stmt stmt, SootMethod callee) {
		boolean matched = false;
		
		// Static initializers do not modify access paths on call and return
		if (callee.isStaticInitializer())
			return null;
		
		// Special treatment for doPrivileged()
		if (stmt.getInvokeExpr().getMethod().getName().equals("doPrivileged")) {
			if (!callee.isStatic())
				if (curAP.getPlainValue() == callee.getActiveBody().getThisLocal())
					return curAP.copyWithNewValue(stmt.getInvokeExpr().getArg(0));
			
			return null;
		}
		
		// Make sure that we don't end up with a senseless callee
		if (!callee.getSubSignature().equals(stmt.getInvokeExpr().getMethod().getSubSignature())
				&& !isThreadCall(stmt.getInvokeExpr().getMethod(), callee))
			throw new RuntimeException(String.format("Invalid callee on stack. Caller "
					+ "was %s, callee was %s", stmt.getInvokeExpr().getMethod().getSubSignature(),
					callee));
		
		// Map the parameters back into the caller
		for (int i = 0; i < stmt.getInvokeExpr().getArgCount(); i++) {
			Local paramLocal = callee.getActiveBody().getParameterLocal(i);
			if (paramLocal == curAP.getPlainValue()) {
				// We cannot map back to a constant expression at the call site
				if (stmt.getInvokeExpr().getArg(i) instanceof Constant)
					return null;
				curAP = curAP.copyWithNewValue(stmt.getInvokeExpr().getArg(i));
				matched = true;
			}
		}
		
		// Map the "this" local back into the caller
		if (!callee.isStatic() && stmt.getInvokeExpr() instanceof InstanceInvokeExpr) {
			Local thisLocal = callee.getActiveBody().getThisLocal();
			if (thisLocal == curAP.getPlainValue()) {
				curAP = curAP.copyWithNewValue(((InstanceInvokeExpr)
						stmt.getInvokeExpr()).getBase());
				matched = true;
			}
		}
		
		if (matched)
			return curAP;
		
		// Map the return value into the scope of the caller. If we are inside
		// the aliasing part of the path, we might leave methods "the wrong way".
		if (stmt instanceof AssignStmt) {
			AssignStmt assign = (AssignStmt) stmt;
			for (Unit u : callee.getActiveBody().getUnits()) {
				if (u instanceof ReturnStmt) {
					ReturnStmt rStmt = (ReturnStmt) u;
					if (rStmt.getOp() == curAP.getPlainValue()) {
						curAP = curAP.copyWithNewValue(assign.getLeftOp());
						matched = true;
					}
				}
			}
		}
		
		return matched ? curAP : null;
	}
	
	/**
	 * Simplistic check to see whether the given formal callee and actual callee
	 * can be in a thread-start relationship
	 * @param callSite The method at the call site
	 * @param callee The actual callee
	 * @return True if this can be a thread-start call edge, otherwise false
	 */
	private boolean isThreadCall(SootMethod callSite, SootMethod callee) {
		return (callSite.getName().equals("start") && callee.getName().equals("run"));
	}

	/**
	 * Processes an abstraction at a method call. This is a partial summary that
	 * ends at a gap which can for instance be a callback into unknown code.
	 * @param flows The flows object to which to add the newly found flow
	 * @param apAtCall The access path that has reached the method call
	 * @param source The source at which the data flow started
	 * @param stmt The statement at which the call happened
	 * @param sourceAP The access path of the flow source
	 * @param isAlias True if source and sink alias, otherwise false
	 */
	private void processAbstractionAtCall(MethodSummaries flows, AccessPath apAtCall,
			FlowSource source, Stmt stmt, AccessPath sourceAP,
			boolean isAlias) {
		// Create a gap
		GapDefinition gd = gapManager.getOrCreateGapForCall(flows, stmt);
		
		// Check whether we have the base object
		if (apAtCall.isLocal())
			if (stmt.getInvokeExpr() instanceof InstanceInvokeExpr) {
				InstanceInvokeExpr iinv = (InstanceInvokeExpr) stmt.getInvokeExpr();
				Local baseLocal = (Local) iinv.getBase();
				if (baseLocal == apAtCall.getPlainValue()) {
					FlowSink sink = sourceSinkFactory.createGapBaseObjectSink(gd,
							apAtCall.getBaseType());
					addFlow(source, sink, isAlias, flows);
				}
			}
		
		// The sink may be a parameter in the call to the gap method
		for (int i = 0; i < stmt.getInvokeExpr().getArgCount(); i++) {
			Value p = stmt.getInvokeExpr().getArg(i);
			if (apAtCall.getPlainValue() == p) {
				FlowSink sink = sourceSinkFactory.createParameterSink(i, apAtCall, gd);
				addFlow(source, sink, isAlias, flows);
			}
		}

		// The sink may be a local field on the base object
		if (apAtCall.getFieldCount() > 0 && stmt.getInvokeExpr() instanceof InstanceInvokeExpr) {
			InstanceInvokeExpr iinv = (InstanceInvokeExpr) stmt.getInvokeExpr();
			if (apAtCall.getPlainValue() == iinv.getBase()) {
				FlowSink sink = sourceSinkFactory.createFieldSink(apAtCall);
				addFlow(source, sink, isAlias, flows);
			}
		}
	}
	
	/**
	 * Processes an abstraction at the end of a method. This gives full
	 * summaries for the whole method
	 * @param flows The flows object to which to add the newly found flow
	 * @param apAtReturn The access path that has reached the end of the method
	 * @param m The method in which the flow has been found
	 * @param source The source at which the data flow started
	 * @param stmt The statement at which the flow left the method
	 * @param sourceAP The access path of the flow source
	 * @param isAlias True if source and sink alias, otherwise false
	 */
	private void processAbstractionAtReturn(MethodSummaries flows, AccessPath apAtReturn,
			SootMethod m, FlowSource source, Stmt stmt, AccessPath sourceAP,
			boolean isAlias) {
		// Was this the value returned by the method?
		if (stmt instanceof ReturnStmt) {
			ReturnStmt retStmt = (ReturnStmt) stmt;
			if (apAtReturn.getPlainValue() == retStmt.getOp()) {
				FlowSink sink = sourceSinkFactory.createReturnSink(apAtReturn);
				addFlow(source, sink, isAlias, flows);
			}
		}
		
		// The sink may be a parameter
		if (!apAtReturn.isLocal()
				|| apAtReturn.getTaintSubFields()
				|| apAtReturn.getBaseType() instanceof ArrayType)
			for (int i = 0; i < m.getParameterCount(); i++) {
				Local p = m.getActiveBody().getParameterLocal(i);
				if (apAtReturn.getPlainValue() == p) {
					FlowSink sink = sourceSinkFactory.createParameterSink(i, apAtReturn);
					addFlow(source, sink, isAlias, flows);
				}
			}
		
		// The sink may be a local field
		if (!m.isStatic() && apAtReturn.getPlainValue() ==
				m.getActiveBody().getThisLocal()) {
			FlowSink sink = sourceSinkFactory.createFieldSink(apAtReturn);
			addFlow(source, sink, isAlias, flows);
		}
		
		// The sink may be a field on a value obtained from a gap
		if (apAtReturn.isInstanceFieldRef()) {
			Set<GapDefinition> referencedGaps = gapManager.getGapDefinitionsForLocalUse(
					apAtReturn.getPlainValue());
			if (referencedGaps != null && !referencedGaps.isEmpty())
				for (GapDefinition gap : referencedGaps) {
					FlowSink sink = sourceSinkFactory.createFieldSink(apAtReturn, gap);
					addFlow(source, sink, isAlias, flows);
				}
			
			referencedGaps = gapManager.getGapDefinitionsForLocalDef(
					apAtReturn.getPlainValue());
			if (referencedGaps != null && !referencedGaps.isEmpty())
				for (GapDefinition gap : referencedGaps) {
					FlowSink sink = sourceSinkFactory.createReturnSink(apAtReturn, gap);
					addFlow(source, sink, isAlias, flows);
				}
		}
	}
	
	/**
	 * Checks whether this flow has equal sources and sinks, i.e. is propagated
	 * through the method as-is.
	 * @param source The source to check
	 * @param sink The sink to check
	 * @return True if the source is equivalent to the sink, otherwise false
	 */
	private boolean isIdentityFlow(FlowSource source, FlowSink sink) {
		if (sink.isReturn())
			return false;
		if (sink.isField() && source.isParameter())
			return false;
		if (sink.isParameter() && (source.isField() || source.isThis()))
			return false;
		if (source.getGap() != sink.getGap())
			return false;
		
		if (sink.getParameterIndex() != source.getParameterIndex())
			return false;
		
		// If the sink has an access path, but not the source, or vice versa,
		// this cannot be an identity flow
		if ((sink.getAccessPath() == null && source.getAccessPath() != null)
				|| (sink.getAccessPath() != null && source.getAccessPath() == null))
			return false;
		
		// Compare the access paths
		if (sink.getAccessPath() != null) {
			if (sink.getAccessPath().length != source.getAccessPath().length)
				return false;
			for (int i = 0; i < sink.getAccessPath().length; i++) {
				if (!source.getAccessPath()[i].equals(sink.getAccessPath()[i]))
					return false;
			}
		}
		return true;
	}
	
	/**
	 * Adds a flow from the given source to the given sink to the given method
	 * summary
	 * @param source The source at which the data flow starts
	 * @param sink The sink at which the data flow ends
	 * @param isAlias True if the source and sink alias, otherwise false
	 * @param summaries The method summary to which to add the data flow
	 */
	private void addFlow(FlowSource source, FlowSink sink, boolean isAlias,
			MethodSummaries summaries) {
		// Convert the method signature into a subsignature
		String methodSubSig = SootMethodRepresentationParser.v()
				.parseSootMethodString(method).getSubSignature();
		
		// Ignore identity flows
		if (isIdentityFlow(source, sink))
			return;
		
		MethodFlow mFlow = new MethodFlow(methodSubSig, source, sink, isAlias);
		if (summaries.addFlow(mFlow))
			debugMSG(source, sink);
	}
	
	private void debugMSG(FlowSource source, FlowSink sink) {
		if (DEBUG) {
			System.out.println("\nmethod: " + method);
			System.out.println("source: " + source.toString());
			System.out.println("sink  : " + sink.toString());

			System.out.println("------------------------------------");
		}
	}

}
