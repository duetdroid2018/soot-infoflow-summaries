package soot.jimple.infoflow.methodSummary.postProcessor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import soot.Local;
import soot.PointsToAnalysis;
import soot.Scene;
import soot.SootMethod;
import soot.Value;
import soot.jimple.AssignStmt;
import soot.jimple.InstanceFieldRef;
import soot.jimple.InstanceInvokeExpr;
import soot.jimple.ReturnStmt;
import soot.jimple.Stmt;
import soot.jimple.infoflow.data.Abstraction;
import soot.jimple.infoflow.data.AbstractionAtSink;
import soot.jimple.infoflow.data.AccessPath;
import soot.jimple.infoflow.methodSummary.data.FlowSink;
import soot.jimple.infoflow.methodSummary.data.FlowSource;
import soot.jimple.infoflow.methodSummary.data.MethodFlow;
import soot.jimple.infoflow.methodSummary.data.factory.SourceSinkFactory;
import soot.jimple.infoflow.methodSummary.data.summary.MethodSummaries;
import soot.jimple.infoflow.methodSummary.postProcessor.SummaryPathBuilder.SummaryResultInfo;
import soot.jimple.infoflow.methodSummary.postProcessor.SummaryPathBuilder.SummarySourceInfo;
import soot.jimple.infoflow.solver.IInfoflowCFG;
import soot.jimple.infoflow.util.BaseSelector;

public class InfoflowResultPostProcessor {
	private final Logger logger = LoggerFactory.getLogger(InfoflowResultPostProcessor.class);

	private final IInfoflowCFG cfg;
	private final Map<Abstraction, Stmt> collectedAbstractions;
	private final boolean DEBUG = true;
	private final String method;
	private final SourceSinkFactory sourceSinkFactory;
	
	final PointsToAnalysis pTa = Scene.v().getPointsToAnalysis();

	public InfoflowResultPostProcessor(Map<Abstraction, Stmt> collectedAbstractions,
			IInfoflowCFG cfg, String m, SourceSinkFactory sourceSinkFactory) {
		this.collectedAbstractions = collectedAbstractions;
		this.cfg = cfg;
		this.method = m;
		this.sourceSinkFactory = sourceSinkFactory;
	}

	/**
	 * Post process the information collected during a Infoflow analyse.
	 * Extract all summary flow from collectedAbstractions.
	 */
	public MethodSummaries postProcess() {
		MethodSummaries flows = new MethodSummaries();
		logger.info("start processing infoflow abstractions");
		final SootMethod m = Scene.v().getMethod(method);
		
		// Create a context-sensitive path builder. Without context-sensitivity,
		// we get quite some false positives here.
		SummaryPathBuilder pathBuilder = new SummaryPathBuilder(cfg,
				Runtime.getRuntime().availableProcessors());
		
		for (Entry<Abstraction, Stmt> entry : collectedAbstractions.entrySet()) {
			Abstraction a = entry.getKey();
			Stmt stmt = entry.getValue();
			
			// In case we have the same abstraction in multiple places and we
			// extend it with external sink information regardless of the
			// original propagation, we need to clean up first
			a.clearPathCache();
			
			// Get the source info and process the flow
			pathBuilder.clear();
			pathBuilder.computeTaintPaths(Collections.singleton(new AbstractionAtSink(a,
					a.getCurrentStmt())));
			
			for (SummaryResultInfo si : pathBuilder.getResultInfos()) {
				final AccessPath sourceAP = si.getSourceInfo().getAccessPath();
				final AccessPath sinkAP = si.getSinkInfo().getAccessPath();
				
				// Check that we don't get any weird results
				if (sourceAP == null || sinkAP == null)
					throw new RuntimeException("Invalid access path");
				
				/*
				if ((a.toString().contains("size") || a.toString().contains("modCount"))
						&& si.getPath().toString().contains("elementData")) {
					pathBuilder.clear();
					pathBuilder.computeTaintPaths(Collections.singleton(new AbstractionAtSink(a,
							a.getCurrentStmt())));
					System.out.println("x");
				}
				*/
										
				// Process the flow from this source
				if (!sinkAP.equals(sourceAP))
					processFlowSource(flows, m, sinkAP, stmt, si.getSourceInfo());
			}
		}
		
		pathBuilder.shutdown();
		logger.info("Result processing finished");
		return flows;
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
		List<FlowSource> sources = (List<FlowSource>) sourceInfo.getUserData();
		if (sources == null || sources.size() == 0)
			throw new RuntimeException("Link to source missing");
		if (sources.size() > 1)
			throw new RuntimeException("Link to source ambiguous");
		
		FlowSource flowSource = sources.get(0);
		if (flowSource == null)
			return;
		
		// We need to reconstruct the original source access path
		AccessPath sourceAP = reconstructSourceAP(ap, sourceInfo.getAbstractionPath());
		flowSource = sourceSinkFactory.createSource(flowSource.getType(),
				flowSource.getParameterIndex(), sourceAP);
		
		// Depending on the statement at which the flow ended, we need to create
		// a different type of summary
		if (cfg.isExitStmt(stmt))
			processAbstractionAtReturn(flows, ap, m, flowSource, stmt, sourceAP);
		else if (cfg.isCallStmt(stmt))
			processAbstractionAtCall(flows, ap, flowSource, stmt);
		else
			throw new RuntimeException("Invalid statement for flow "
					+ "termination: " + stmt);
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
	 * @return The fully reconstructed access path
	 */
	private AccessPath reconstructSourceAP(AccessPath sinkAP, List<Abstraction> path) {
		// TODO: Static fields?
		
		List<SootMethod> callees = new ArrayList<>();
		AccessPath curAP = sinkAP;
		for (int pathIdx = path.size() - 1; pathIdx >= 0; pathIdx--) {
			final Abstraction abs = path.get(pathIdx);
			final Stmt stmt = abs.getCurrentStmt();
			final Stmt callSite = abs.getCorrespondingCallSite();
			boolean matched = false;
			
			// In case of a call-to-return edge, we have no information about
			// what happened in the callee, so we take the incoming access path
			if (stmt.containsInvokeExpr()) {
				if (callSite == stmt) {
					// only change base local
					Value newBase = (pathIdx > 0) ? path.get(pathIdx - 1).getAccessPath().getPlainValue()
							: abs.getAccessPath().getPlainValue();
					curAP = curAP.copyWithNewValue(newBase);
					matched = true;
				}
			}
			
			if (matched)
				continue;
			
			if (stmt.containsInvokeExpr() && !callees.isEmpty()) {
				SootMethod callee = callees.remove(0);
				
				// Make sure that we don't end up with a senseless callee
				if (!callee.getSubSignature().equals(stmt.getInvokeExpr().getMethod().getSubSignature()))
					throw new RuntimeException("Invalid callee on stack"); 
				
				// Map the parameters back into the caller
				for (int i = 0; i < stmt.getInvokeExpr().getArgCount(); i++) {
					Local paramLocal = callee.getActiveBody().getParameterLocal(i);
					if (paramLocal == curAP.getPlainValue()) {
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
			}
			else if (stmt instanceof AssignStmt) {
				final AssignStmt assignStmt = (AssignStmt) stmt;
				final Value leftOp = BaseSelector.selectBase(assignStmt.getLeftOp(), false);
				
				// If the access path must matches on the left side, we
				// continue with the value from the right side.
				if (leftOp instanceof Local && leftOp == curAP.getPlainValue()) {
					// Get the next value from the right side of the assignment
					final Value[] rightOps = BaseSelector.selectBaseList(assignStmt.getRightOp(), false);
					Value rightOp = null;
					if (rightOps.length == 1)
						rightOp = rightOps[0];
					else {
						int prevIdx = pathIdx - 1;
						scan : while (pathIdx >= 0) {
							Abstraction prevAbs = path.get(prevIdx);
							Value base = prevAbs.getAccessPath().getPlainValue();
							for (Value rv : rightOps)
								if (base == rv) {
									rightOp = rv;
									break scan;
								}
						}
					}
					
					curAP = curAP.copyWithNewValue(rightOp);
					matched = true;
				}
				else if (assignStmt.getLeftOp() instanceof InstanceFieldRef) {
					InstanceFieldRef ifref = (InstanceFieldRef) assignStmt.getLeftOp();
					if (ifref.getBase() == curAP.getPlainValue()
							&& ifref.getField() == curAP.getFirstField()) {
						curAP = curAP.copyWithNewValue(assignStmt.getRightOp(), null, true);
						matched = true;
					}
				}
				
				if (matched)
					continue;
				
				// For aliasing relationships, we also need to check the right
				// side
				if (assignStmt.getRightOp() instanceof InstanceFieldRef) {
					InstanceFieldRef ifref = (InstanceFieldRef) assignStmt.getRightOp();
					if (ifref.getBase() == curAP.getPlainValue()
							&& ifref.getField() == curAP.getFirstField()) {
						curAP = curAP.copyWithNewValue(assignStmt.getLeftOp(), null, true);
						matched = true;
					}
				}
				
			}
			else if (callSite != null) {
				SootMethod callee = cfg.getMethodOf(stmt);
				callees.add(0, callee);
				
				// Map the return value back into the scope of the callee
				if (stmt instanceof ReturnStmt) {
					ReturnStmt retStmt = (ReturnStmt) stmt;
					if (callSite instanceof AssignStmt
							&& ((AssignStmt) callSite).getLeftOp() == curAP.getPlainValue()) {
						curAP = curAP.copyWithNewValue(retStmt.getOp());
						matched = true;
					}
				}
								
				// Map the "this" fields into the callee
				if (!callee.isStatic() && callSite.getInvokeExpr() instanceof InstanceInvokeExpr) {
					InstanceInvokeExpr iiExpr = (InstanceInvokeExpr) callSite.getInvokeExpr();
					if (iiExpr.getBase() == curAP.getPlainValue()) {
						Local thisLocal = callee.getActiveBody().getThisLocal();
						curAP = curAP.copyWithNewValue(thisLocal);
						matched = true;
					}
				}
				
				// Map the parameters into the callee. Note that parameters as
				// such cannot return taints from methods, only fields reachable
				// through them
				if (!curAP.isLocal())
					for (int i = 0; i < callSite.getInvokeExpr().getArgCount(); i++) {
						if (callSite.getInvokeExpr().getArg(i) == curAP.getPlainValue()) {
							Local paramLocal = callee.getActiveBody().getParameterLocal(i);
							curAP = curAP.copyWithNewValue(paramLocal);
							matched = true;
						}
					}
			}
		}
		return curAP;
	}

	/**
	 * Processes an abstraction at a method call. This is a partial summary that
	 * ends at a gap which can for instance be a callback into unknown code.
	 * @param flows The flows object to which to add the newly found flow
	 * @param apAtCall The access path that has reached the method call
	 * @param source The source at which the data flow started
	 * @param stmt The statement at which the call happened
	 */
	private void processAbstractionAtCall(MethodSummaries flows, AccessPath apAtCall,
			FlowSource source, Stmt stmt) {
		System.out.println("x");
	}
	
	/**
	 * Processes an abstraction at the end of a method. This gives full
	 * summaries for the whole method
	 * @param flows The flows object to which to add the newly found flow
	 * @param apAtReturn The access path that has reached the end of the method
	 * @param m The method in which the flow has been found
	 * @param source The source at which the data flow started
	 * @param stmt The statement at which the flow left the method
	 */
	private void processAbstractionAtReturn(MethodSummaries flows, AccessPath apAtReturn,
			SootMethod m, FlowSource source, Stmt stmt, AccessPath sourceAP) {
		// Was this the value returned by the method?
		if (stmt instanceof ReturnStmt) {
			ReturnStmt retStmt = (ReturnStmt) stmt;
			if (apAtReturn.getPlainValue() == retStmt.getOp()) {
				FlowSink sink = sourceSinkFactory.createReturnSink(apAtReturn);
				addFlow(source, sink, flows);
			}
		}
		
		// The sink may be a parameter
		for (int i = 0; i < m.getParameterCount(); i++) {
			Local p = m.getActiveBody().getParameterLocal(i);
			if (apAtReturn.getPlainValue() == p) {
				FlowSink sink = sourceSinkFactory.createParameterSink(i, apAtReturn);
				addFlow(source, sink, flows);
			}
		}

		// The sink may be a local field
		if (!m.isStatic() && apAtReturn.getPlainValue() == m.getActiveBody().getThisLocal()) {
			FlowSink sink = sourceSinkFactory.createFieldSink(apAtReturn);
			addFlow(source, sink, flows);
		}
	}
	
	private boolean isIdentityFlow(FlowSource source, FlowSink sink) {
		if (sink.isReturn())
			return false;
		if (sink.isField() && source.isParameter())
			return false;
		if (sink.isParameter() && (source.isField() || source.isThis()))
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

	private void addFlow(FlowSource source, FlowSink sink, MethodSummaries summaries) {
		// Ignore identity flows
		if (isIdentityFlow(source, sink))
			return;

		MethodFlow mFlow = new MethodFlow(method, source, sink);
		if (summaries.addFlowForMethod(method, mFlow))
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
