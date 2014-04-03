package soot.jimple.infoflow.methodSummary.util;

import static soot.jimple.infoflow.methodSummary.data.impl.FlowSinkAndSourceFactory.createFlowFieldSink;
import static soot.jimple.infoflow.methodSummary.data.impl.FlowSinkAndSourceFactory.createFlowParamterSink;
import static soot.jimple.infoflow.methodSummary.data.impl.FlowSinkAndSourceFactory.createFlowReturnSink;
import heros.InterproceduralCFG;

import java.util.Collections;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import soot.ArrayType;
import soot.Local;
import soot.PointsToAnalysis;
import soot.PointsToSet;
import soot.Scene;
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
import soot.jimple.infoflow.methodSummary.SummarySourceSinkManager;
import soot.jimple.infoflow.methodSummary.data.AbstractMethodFlow;
import soot.jimple.infoflow.methodSummary.data.IFlowSink;
import soot.jimple.infoflow.methodSummary.data.IFlowSource;
import soot.jimple.infoflow.methodSummary.data.MethodSummaries;
import soot.jimple.infoflow.methodSummary.data.impl.DefaultMethodFlow;

public class InfoflowResultProcessor {
	private final Logger logger = LoggerFactory.getLogger(InfoflowResultProcessor.class);

	private InterproceduralCFG<Unit, SootMethod> cfg;
	private Set<Abstraction> result;
	private boolean DEBUG = true;
	private String method;

	public InfoflowResultProcessor(Set<Abstraction> result2, InterproceduralCFG<Unit, SootMethod> cfg, String m,
			SummarySourceSinkManager manager) {
		this.result = result2;
		this.cfg = cfg;
		this.method = m;
	}

	public MethodSummaries process() {
		MethodSummaries flows = new MethodSummaries();
		logger.info("start processing infoflow abstractions");
		final SootMethod m = Scene.v().getMethod(method);
		final PointsToAnalysis pTa = Scene.v().getPointsToAnalysis();
		
		IAbstractionPathBuilder pathBuilder = new DefaultPathBuilderFactory().createPathBuilder
				(Runtime.getRuntime().availableProcessors());
		
		for (Abstraction a : result) {
			logger.debug("abstraction: " + a.toString());
			pathBuilder.getResults().clear();
			pathBuilder.computeTaintSources(Collections.singleton(new AbstractionAtSink
					(a, NullConstant.v(), Jimple.v().newNopStmt())));
			for (Set<SourceInfo> sourceInfos : pathBuilder.getResults().getResults().values())
				for (SourceInfo si : sourceInfos) {
					if (si.getContext() == null || si.getSource() == null)
						continue;
	
					// Get the source
					IFlowSource source = (IFlowSource) si.getUserData();
					if (source == null)
						throw new RuntimeException("Link to source missing");
					
					// Get the sink
					IFlowSink sink = null;
	
					PointsToSet basePT = pTa.reachingObjects(a.getAccessPath().getPlainValue());
					// The sink may be a parameter
					for (int i = 0; i < m.getParameterCount(); i++) {
						Local p = m.getActiveBody().getParameterLocal(i);
						//boolean isPrimitiveType = m.getParameterType(i) instanceof PrimType ;
						boolean isArrayType = m.getParameterType(i) instanceof ArrayType;
						PointsToSet pPT = pTa.reachingObjects(p);
						if (pPT.hasNonEmptyIntersection(basePT)) {
							if (a.getAccessPath().isLocal()){
								if(isArrayType)
									sink = createFlowParamterSink(m, i, null, a.getAccessPath().getTaintSubFields());
							}
							else if (a.getAccessPath().getFieldCount() == 1)
								sink = createFlowParamterSink(m, i, a.getAccessPath().getFirstField(), a.getAccessPath()
										.getTaintSubFields());
							else
								sink = createFlowParamterSink(m, i, a.getAccessPath().getFirstField(), true);
						}
						
						if (source != null && sink != null){
							addFlow(source, sink, flows);
							sink = null;
						}
	                        
					}
	
					// check field sink
					if (a.getAccessPath().isInstanceFieldRef() && !m.isStatic()
							&& a.getAccessPath().getPlainValue() == m.getActiveBody().getThisLocal()) {
						if (a.getAccessPath().getFieldCount() == 1)
							sink = createFlowFieldSink(a.getAccessPath().getFirstField(), null, a.getAccessPath()
									.getTaintSubFields());
						else if (a.getAccessPath().getFieldCount() == 2)
							sink = createFlowFieldSink(a.getAccessPath().getFirstField(), a.getAccessPath().getFields()[1],
									a.getAccessPath().getTaintSubFields());
						else
							sink = createFlowFieldSink(a.getAccessPath().getFirstField(), a.getAccessPath().getFields()[1],
									true);
						if (source != null && sink != null){
							addFlow(source, sink, flows);
							sink = null;
						}
					}
	
					// check return sink
					if (a.getAccessPath().getPlainValue() instanceof Local) {
						for (Unit u : m.getActiveBody().getUnits())
							if (cfg.isExitStmt(u))
								for (ValueBox vb : u.getUseBoxes())
									if (vb.getValue() == a.getAccessPath().getPlainValue())
										if (a.getAccessPath().isLocal())
											sink = createFlowReturnSink(a.getAccessPath().getTaintSubFields());
										else if (a.getAccessPath().getFieldCount() == 1)
											sink = createFlowReturnSink(a.getAccessPath().getFirstField(), a
													.getAccessPath().getTaintSubFields());
										else
											sink = createFlowReturnSink(a.getAccessPath().getFirstField(), true);
						if (source != null && sink != null){
							addFlow(source, sink, flows);
							sink = null;
						}
					}
	
				}
		}

		logger.info("Result processing finished");
		return flows;
	}

	private boolean isIdentityFlow(IFlowSource source, IFlowSink sink) {
		if (source.hasAccessPath() != sink.hasAccessPath())
			return false;
		if (!safeEquals(source.getAccessPath().fieldIdx(0), sink.getAccessPath().fieldIdx(0)))
			return false;
		
		if (source.isParamter() != sink.isParamter())
			return false;
		if (source.getParamterIndex() != sink.getParamterIndex())
			return false;
		if (!safeEquals(source.getParaType(), sink.getParaType()))
			return false;
		
		if (source.isField() != sink.isField())
			return false;
		if (!safeEquals(source.getField(), sink.getField()))
			return false;
		
		if (source.isThis() != sink.isThis())
			return false;
		
		return true;
	}
	
	private void addFlow(IFlowSource source, IFlowSink sink, MethodSummaries summaries) {
		// Ignore identity flows
		if (isIdentityFlow(source, sink))
			return;
		
		AbstractMethodFlow mFlow = new DefaultMethodFlow(method, source, sink);
		summaries.addFlowForMethod(method, mFlow);
		debugMSG(source, sink);
	}

	private boolean safeEquals(String accessPath, String accessPath2) {
		if (accessPath == accessPath2)
			return true;
		if (accessPath == null || accessPath2 == null)
			return false;
		return accessPath.equals(accessPath2);
	}

	private void debugMSG(IFlowSource source, IFlowSink sink) {
		if (DEBUG) {
			System.out.println("\nmethod: " + method);
			System.out.println("source: " + source.toString());
			System.out.println("sink: " + sink.toString());
			System.out.println("------------------------------------");
		}
	}

}