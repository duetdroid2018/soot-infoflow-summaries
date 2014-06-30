package soot.jimple.infoflow.methodSummary.postProcessor;

import static soot.jimple.infoflow.methodSummary.data.factory.SourceSinkFactory.createFieldSink;
import static soot.jimple.infoflow.methodSummary.data.factory.SourceSinkFactory.createParamterSink;
import static soot.jimple.infoflow.methodSummary.data.factory.SourceSinkFactory.createReturnSink;
import heros.InterproceduralCFG;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
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
import soot.Unit;
import soot.ValueBox;
import soot.jimple.Jimple;
import soot.jimple.NullConstant;
import soot.jimple.infoflow.InfoflowResults.SourceInfo;
import soot.jimple.infoflow.data.Abstraction;
import soot.jimple.infoflow.data.AbstractionAtSink;
import soot.jimple.infoflow.data.pathBuilders.DefaultPathBuilderFactory;
import soot.jimple.infoflow.data.pathBuilders.IAbstractionPathBuilder;
import soot.jimple.infoflow.methodSummary.data.FlowSink;
import soot.jimple.infoflow.methodSummary.data.FlowSource;
import soot.jimple.infoflow.methodSummary.data.MethodFlow;
import soot.jimple.infoflow.methodSummary.data.impl.DefaultMethodFlow;
import soot.jimple.infoflow.methodSummary.data.summary.MethodSummaries;

public class InfoflowResultPostProcessor {
	private final Logger logger = LoggerFactory.getLogger(InfoflowResultPostProcessor.class);

	private final InterproceduralCFG<Unit, SootMethod> cfg;
	private final Set<Abstraction> collectedAbstractions;
	private final boolean DEBUG = true;
	private final String method;
	private final int summaryAPLength;
	final PointsToAnalysis pTa = Scene.v().getPointsToAnalysis();

	public InfoflowResultPostProcessor(Set<Abstraction> collectedAbstractions, InterproceduralCFG<Unit, SootMethod> cfg,
			String m, int sAPL) {
		this.collectedAbstractions = collectedAbstractions;
		this.cfg = cfg;
		this.method = m;
		this.summaryAPLength = sAPL;
	}

	/**
	 * Post process the information collected during a Infoflow analyse.
	 * Extract all summary flow from collectedAbstractions.
	 */
	public MethodSummaries postProcess() {
		MethodSummaries flows = new MethodSummaries();
		System.out.println();
		logger.info("start processing infoflow abstractions");
		final SootMethod m = Scene.v().getMethod(method);

		IAbstractionPathBuilder pathBuilder = new DefaultPathBuilderFactory().createPathBuilder(Runtime.getRuntime()
				.availableProcessors());

		for (Abstraction a : collectedAbstractions) {
			logger.debug("abstraction: " + a.toString());
			pathBuilder.getResults().clear();
			pathBuilder.computeTaintSources(Collections.singleton(new AbstractionAtSink(a, NullConstant.v(), Jimple.v()
					.newNopStmt())));
			for (Set<SourceInfo> sourceInfos : pathBuilder.getResults().getResults().values()) {
				for (SourceInfo si : sourceInfos) {
					if (si.getContext() == null || si.getSource() == null)
						continue;

					// Get the source data
					List<FlowSource> sources = new LinkedList<FlowSource>();
					//safe to do since we control all use data
					sources = ((List<FlowSource>) si.getUserData());
					if (sources.size() == 0)
						throw new RuntimeException("Link to source missing");
					for (FlowSource source : sources) {
						processSourceAbstraction(flows, a, m, source);
					}
				}
			}
		}

		logger.info("Result processing finished");
		return flows;
	}

	private void processSourceAbstraction(MethodSummaries flows, Abstraction a, SootMethod m, FlowSource source) {
		// Get the sink
		FlowSink sink = null;

		PointsToSet basePT = pTa.reachingObjects(a.getAccessPath().getPlainValue());
		// The sink may be a parameter
		for (int i = 0; i < m.getParameterCount(); i++) {
			Local p = m.getActiveBody().getParameterLocal(i);

			PointsToSet pPT = pTa.reachingObjects(p);
			
			if (pPT.hasNonEmptyIntersection(basePT)) {
				if (a.getAccessPath().isLocal()) {
					//an array can be a sink (normally only parameter.field+ can be sinks)
					if ( m.getParameterType(i) instanceof ArrayType)
						sink = createParamterSink(m, i, java.util.Collections.<SootField> emptyList(), a
								.getAccessPath().getTaintSubFields());
				} else if (a.getAccessPath().getFieldCount() < summaryAPLength)
					sink = createParamterSink(m, i, a.getAccessPath().getFields(), a.getAccessPath()
							.getTaintSubFields());
				else {
					sink = createParamterSink(m, i, cutAPLength(a.getAccessPath().getFields()), true);
				}

			}

			if (source != null && sink != null) {
				addFlow(source, sink, flows);
				sink = null;
			}

		}

		// check field sink
		if (a.getAccessPath().isInstanceFieldRef() && !m.isStatic()
				&& a.getAccessPath().getPlainValue() == m.getActiveBody().getThisLocal()) {
			if (a.getAccessPath().getFieldCount() < summaryAPLength) {
				// we can save the complete ap in the summary file
				sink = createFieldSink(a.getAccessPath().getFields(), a.getAccessPath().getTaintSubFields());

			} else {
				// we have to cut the ap sience the ap is longer then the set limit
				sink = createFieldSink(cutAPLength(a.getAccessPath().getFields()), true);
			}

			if (source != null && sink != null) {
				addFlow(source, sink, flows);
				sink = null;
			}
		}

		// check return sink
		for (Unit u : m.getActiveBody().getUnits())
			if (cfg.isExitStmt(u))
				for (ValueBox vb : u.getUseBoxes())
					if (vb.getValue() == a.getAccessPath().getPlainValue())
						if (a.getAccessPath().isLocal())
							sink = createReturnSink(a.getAccessPath().getTaintSubFields());
						else if (a.getAccessPath().getFieldCount() < summaryAPLength) {
							sink = createReturnSink(a.getAccessPath().getFields(), a.getAccessPath()
									.getTaintSubFields());
						} else {
							sink = createReturnSink(cutAPLength(a.getAccessPath().getFields()), true);
						}

		if (source != null && sink != null) {
			addFlow(source, sink, flows);
			sink = null;
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

		MethodFlow mFlow = new DefaultMethodFlow(method, source, sink);
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
