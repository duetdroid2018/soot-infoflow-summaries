package soot.jimple.infoflow.methodSummary.postProcessor;

import static soot.jimple.infoflow.methodSummary.data.factory.SourceSinkFactory.createFieldSink;
import static soot.jimple.infoflow.methodSummary.data.factory.SourceSinkFactory.createParamterSink;
import static soot.jimple.infoflow.methodSummary.data.factory.SourceSinkFactory.createReturnSink;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import soot.ArrayType;
import soot.Local;
import soot.PointsToAnalysis;
import soot.PointsToSet;
import soot.Scene;
import soot.SootField;
import soot.SootMethod;
import soot.jimple.NullConstant;
import soot.jimple.ReturnStmt;
import soot.jimple.Stmt;
import soot.jimple.infoflow.InfoflowResults.SourceInfo;
import soot.jimple.infoflow.data.Abstraction;
import soot.jimple.infoflow.data.AbstractionAtSink;
import soot.jimple.infoflow.data.pathBuilders.DefaultPathBuilderFactory;
import soot.jimple.infoflow.data.pathBuilders.DefaultPathBuilderFactory.PathBuilder;
import soot.jimple.infoflow.data.pathBuilders.IAbstractionPathBuilder;
import soot.jimple.infoflow.methodSummary.data.FlowSink;
import soot.jimple.infoflow.methodSummary.data.FlowSource;
import soot.jimple.infoflow.methodSummary.data.MethodFlow;
import soot.jimple.infoflow.methodSummary.data.summary.MethodSummaries;
import soot.jimple.infoflow.solver.IInfoflowCFG;

public class InfoflowResultPostProcessor {
	private final Logger logger = LoggerFactory.getLogger(InfoflowResultPostProcessor.class);

	private final IInfoflowCFG cfg;
	private final Map<Abstraction, Stmt> collectedAbstractions;
	private final boolean DEBUG = true;
	private final String method;
	private final int summaryAPLength;
	final PointsToAnalysis pTa = Scene.v().getPointsToAnalysis();

	public InfoflowResultPostProcessor(Map<Abstraction, Stmt> collectedAbstractions,
			IInfoflowCFG cfg, String m, int sAPL) {
		this.collectedAbstractions = collectedAbstractions;
		this.cfg = cfg;
		this.method = m;
		this.summaryAPLength = sAPL;
	}

	/**
	 * Post process the information collected during a Infoflow analyse.
	 * Extract all summary flow from collectedAbstractions.
	 */
	@SuppressWarnings("unchecked")
	public MethodSummaries postProcess() {
		MethodSummaries flows = new MethodSummaries();
		logger.info("start processing infoflow abstractions");
		final SootMethod m = Scene.v().getMethod(method);
		
		// Create a context-sensitive path builder. Without context-sensitivity,
		// we get quite some false positives here.
		IAbstractionPathBuilder pathBuilder =
				new DefaultPathBuilderFactory(PathBuilder.ContextSensitive,
						true).createPathBuilder(Runtime.getRuntime().availableProcessors(), cfg);

		for (Entry<Abstraction, Stmt> entry : collectedAbstractions.entrySet()) {
			Abstraction a = entry.getKey();
			Stmt stmt = entry.getValue();
			
			// In case we have the same abstraction in multiple places and we
			// extend it with external sink information regardless of the
			// original propagation, we need to clean up first
			a.clearPathCache();
			
			// Get the source info and process the flow
			pathBuilder.getResults().clear();
			pathBuilder.computeTaintPaths(Collections.singleton(new AbstractionAtSink(a,
					NullConstant.v(), a.getCurrentStmt())));
			
			for (Set<SourceInfo> sourceInfos : pathBuilder.getResults().getResults().values()) {
				for (SourceInfo si : sourceInfos) {
					if (si.getContext() == null || si.getSource() == null)
						continue;
					
					// Get the source data
					List<FlowSource> sources = (List<FlowSource>) si.getUserData();
					if (sources == null || sources.size() == 0)
						throw new RuntimeException("Link to source missing");
					
					for (FlowSource source : sources)
						processFlowSource(flows, m, a, stmt, source);
				}
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
	 * @param a The abstraction that has reached the given statement
	 * @param m The method in which the flow has been found
	 * @param stmt The statement at which the flow has arrived
	 * @param source The source from which the flow originated
	 */
	private void processFlowSource(MethodSummaries flows, final SootMethod m,
			Abstraction a, Stmt stmt, FlowSource source) {
		if (source != null)
			if (cfg.isExitStmt(stmt))
				processAbstractionAtReturn(flows, a, m, source, stmt);
			else if (cfg.isCallStmt(stmt))
				processAbstractionAtCall(flows, a, source, stmt);
			else
				throw new RuntimeException("Invalid statement for flow "
						+ "termination: " + stmt);
	}
	
	/**
	 * Processes an abstraction at a method call. This is a partial summary that
	 * ends at a gap which can for instance be a callback into unknown code.
	 * @param flows The flows object to which to add the newly found flow
	 * @param a The abstraction that has reached the method call
	 * @param source The source at which the data flow started
	 * @param stmt The statement at which the call happened
	 */
	private void processAbstractionAtCall(MethodSummaries flows, Abstraction a,
			FlowSource source, Stmt stmt) {
		System.out.println("x");
	}
	
	/**
	 * Processes an abstraction at the end of a method. This gives full
	 * summaries for the whole method
	 * @param flows The flows object to which to add the newly found flow
	 * @param a The abstraction that has reached the end of the method
	 * @param m The method in which the flow has been found
	 * @param source The source at which the data flow started
	 * @param stmt The statement at which the flow left the method
	 */
	private void processAbstractionAtReturn(MethodSummaries flows, Abstraction a,
			SootMethod m, FlowSource source, Stmt stmt) {
		// Get the sink
		PointsToSet basePT = pTa.reachingObjects(a.getAccessPath().getPlainValue());
		
		// The sink may be a parameter
		for (int i = 0; i < m.getParameterCount(); i++) {
			Local p = m.getActiveBody().getParameterLocal(i);
			PointsToSet pPT = pTa.reachingObjects(p);
			
			FlowSink sink = null;
			if (pPT.hasNonEmptyIntersection(basePT)) {
				if (a.getAccessPath().isLocal()
						&& m.getParameterType(i) instanceof ArrayType)
					sink = createParamterSink(m, i, java.util.Collections.<SootField> emptyList(), a
							.getAccessPath().getTaintSubFields());
				else if (a.getAccessPath().getFieldCount() < summaryAPLength)
					sink = createParamterSink(m, i, a.getAccessPath().getFields(), a.getAccessPath()
							.getTaintSubFields());
				else
					sink = createParamterSink(m, i, cutAPLength(a.getAccessPath().getFields()), true);

			}
			
			if (sink != null)
				addFlow(source, sink, flows);
		}

		// check field sink
		if (a.getAccessPath().isInstanceFieldRef() && !m.isStatic()
				&& a.getAccessPath().getPlainValue() == m.getActiveBody().getThisLocal()) {
			FlowSink sink = null;
			if (a.getAccessPath().getFieldCount() < summaryAPLength)
				// we can save the complete ap in the summary file
				sink = createFieldSink(a.getAccessPath().getFields(), a.getAccessPath().getTaintSubFields());
			else
				// we have to cut the ap sience the ap is longer then the set limit
				sink = createFieldSink(cutAPLength(a.getAccessPath().getFields()), true);
			
			if (sink != null)
				addFlow(source, sink, flows);
		}

		// check return sink
		if (stmt instanceof ReturnStmt) {
			ReturnStmt retStmt = (ReturnStmt) stmt;
			if (retStmt.getOp() == a.getAccessPath().getPlainValue()) {
				FlowSink sink = null;
				if (a.getAccessPath().isLocal())
					sink = createReturnSink(a.getAccessPath().getTaintSubFields());
				else if (a.getAccessPath().getFieldCount() < summaryAPLength)
					sink = createReturnSink(a.getAccessPath().getFields(), a.getAccessPath()
							.getTaintSubFields());
				else
					sink = createReturnSink(cutAPLength(a.getAccessPath().getFields()), true);
				
				if (sink != null)
					addFlow(source, sink, flows);
			}
		}
	}

	private SootField[] cutAPLength(SootField[] fields) {
		SootField f[] = new SootField[summaryAPLength];
		for (int i = 0; i < summaryAPLength; i++) {
			f[i] = fields[i];
		}
		return f;
	}

	private boolean isIdentityFlow(FlowSource source, FlowSink sink) {
		if (sink.isReturn())
			return false;
		if (sink.isField()) {
			if (source.isParameter())
				return false;
		}
		if (sink.isParameter()) {
			if (source.isField() || source.isThis())
				return false;
		}
		if (sink.getParameterIndex() != source.getParameterIndex())
			return false;
		if (sink.getFieldCount() != source.getFieldCount())
			return false;
		for (int i = 0; i < sink.getFieldCount(); i++) {
			if (!source.getFields().get(i).equals(sink.getFields().get(i)))
				return false;
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
