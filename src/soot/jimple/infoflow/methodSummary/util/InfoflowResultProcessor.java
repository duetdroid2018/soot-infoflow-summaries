package soot.jimple.infoflow.methodSummary.util;

import static soot.jimple.infoflow.methodSummary.data.impl.FlowSinkAndSourceFactory.createFlowFieldSink;
import static soot.jimple.infoflow.methodSummary.data.impl.FlowSinkAndSourceFactory.createFlowFieldSource;
import static soot.jimple.infoflow.methodSummary.data.impl.FlowSinkAndSourceFactory.createFlowParamterSink;
import static soot.jimple.infoflow.methodSummary.data.impl.FlowSinkAndSourceFactory.createFlowParamterSource;
import static soot.jimple.infoflow.methodSummary.data.impl.FlowSinkAndSourceFactory.createFlowReturnSink;
import static soot.jimple.infoflow.methodSummary.data.impl.FlowSinkAndSourceFactory.createFlowThisSource;
import heros.InterproceduralCFG;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import soot.ArrayType;
import soot.Local;
import soot.PointsToAnalysis;
import soot.PointsToSet;
import soot.Scene;
import soot.SootClass;
import soot.SootField;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.jimple.ParameterRef;
import soot.jimple.ReturnStmt;
import soot.jimple.ThisRef;
import soot.jimple.infoflow.data.Abstraction;
import soot.jimple.infoflow.data.Abstraction.SourceContextAndPath;
import soot.jimple.infoflow.methodSummary.SummarySourceSinkManager;
import soot.jimple.infoflow.methodSummary.data.AbstractFlowSink;
import soot.jimple.infoflow.methodSummary.data.AbstractFlowSource;
import soot.jimple.infoflow.methodSummary.data.AbstractMethodFlow;
import soot.jimple.infoflow.methodSummary.data.impl.DefaultMethodFlow;
import soot.jimple.internal.JInstanceFieldRef;
import soot.jimple.internal.JimpleLocal;

public class InfoflowResultProcessor {
	private final Logger logger = LoggerFactory.getLogger(InfoflowResultProcessor.class);

	private InterproceduralCFG<Unit, SootMethod> cfg;
	private Set<Abstraction> result;
	private boolean DEBUG = true;
	private Map<SootMethod, Set<AbstractMethodFlow>> flows = new HashMap<SootMethod, Set<AbstractMethodFlow>>(127);
	private List<AbstractMethodFlow> foundFlows = new LinkedList<AbstractMethodFlow>();
	private String method;
	private boolean ignoreExceptions = true;
	
	private void process() {

		logger.info("start processing infoflow abstractions");
		SootMethod m = Scene.v().getMethod(method);

		for (Abstraction a : result) {
			logger.debug("abstraction: " + a.toString());
			Set<SourceContextAndPath> paths = null;
			try {
				paths = a.getSources();
			} catch (RuntimeException e) {
				String methodMsg = ((m != null) ? ("in method " + m.toString() + " we ") : "");
				HandleException.handleException(InfoflowResultProcessor.class.getName(), methodMsg
						+ "couldn't process abstraction: " + a.toString()
						+ " in InfoflowResultProcessor since there is an error in getPath", e);
				e.printStackTrace();
				if (!ignoreExceptions)
					throw e;
			}
			if (paths != null) {
				for (SourceContextAndPath scp : paths) {
					AbstractFlowSink sink = null;
					AbstractFlowSource source = null;
					logger.debug("path: " + scp.toString());
					if (scp.getStmt() == null || scp.getValue() == null)
						continue;

					Value v = scp.getValue();

					PointsToAnalysis pTa = Scene.v().getPointsToAnalysis();
					// paramter source:
					if (v instanceof ParameterRef) {
						source = createFlowParamterSource(m, ((ParameterRef) v).getIndex(), null);
					}

					if (v instanceof JInstanceFieldRef) {
						JInstanceFieldRef fieldRef = (JInstanceFieldRef) v;
						if (fieldRef.getBase() instanceof JimpleLocal) {
							Local base = (Local) fieldRef.getBase();

							// field source apl = 1
							PointsToSet basePT = pTa.reachingObjects(base);
							for (SootField f : getClassFields(m.getDeclaringClass())) {
								if (!f.isStatic() && !m.isStatic()) {
									PointsToSet pointsToField = pTa
											.reachingObjects(m.getActiveBody().getThisLocal(), f);
									if (basePT.hasNonEmptyIntersection(pointsToField)) {
										source = createFlowFieldSource(f, fieldRef.getField());
									}
								} else {

									// TODO static field
								}
							}

							// field source apl = 0
							if (!m.isStatic()) {
								PointsToSet mThisPT = pTa.reachingObjects(m.getActiveBody().getThisLocal());
								if (mThisPT.hasNonEmptyIntersection(basePT)) {
									source = createFlowFieldSource(fieldRef.getField(), null);
								}
							}

							// paramter source apl = 1
							for (int i = 0; i < m.getParameterCount(); i++) {
								Local p = m.getActiveBody().getParameterLocal(i);
								PointsToSet paraPT = pTa.reachingObjects(p);
								if (basePT.hasNonEmptyIntersection(paraPT)) {
									source = createFlowParamterSource(m, i, fieldRef.getField());
								}
							}
						}
					}
					// this flow IdentityRef
					if (v instanceof ThisRef && cfg.getMethodOf(scp.getStmt()).equals(m)) {
						source = createFlowThisSource();
					}

					/*
					 * ############# calc sinks now ##############
					 */
					if (source == null)
						continue;
					Local sinkL = a.getAccessPath().getPlainLocal();
					PointsToSet basePT = pTa.reachingObjects(a.getAccessPath().getPlainLocal());
					// check if para sink apl = 0
					for (int i = 0; i < m.getParameterCount(); i++) {
						Local p = m.getActiveBody().getParameterLocal(i);

						// no PointTo should be needed here
						// primitive types cant be sinks
						if (sinkL.equals(p) && (m.getParameterType(i) instanceof ArrayType)) {
							// para Sink
							sink = createFlowParamterSink(m, i, null, a.getAccessPath().getTaintSubFields());
						}

					}
					// check if para sink apl = 1
					if (a.getAccessPath().isInstanceFieldRef()) {

						for (int i = 0; i < m.getParameterCount(); i++) {
							Local p = m.getActiveBody().getParameterLocal(i);
							PointsToSet pPT = pTa.reachingObjects(p);
							if (pPT.hasNonEmptyIntersection(basePT)) {
								// para Sink
								if (a.getAccessPath().getFieldCount() < 2) {
									sink = createFlowParamterSink(m, i, a.getAccessPath().getFirstField(), a
											.getAccessPath().getTaintSubFields());
								} else {
									sink = createFlowParamterSink(m, i, a.getAccessPath().getFirstField(), true);
								}

							}
						}
					}

					// check field sink
					if (a.getAccessPath().isInstanceFieldRef()) {
						List<SootField> fields = getClassFields(m.getDeclaringClass());
						// apl 0
						if (a.getAccessPath().getFieldCount() == 1) {
							if (fields.contains(a.getAccessPath().getFirstField())) {
								if (a.getAccessPath().getFirstField().isStatic()) {
									sink = createFlowFieldSink(a.getAccessPath().getFirstField(), null, a
											.getAccessPath().getTaintSubFields());
								} else if (!m.isStatic()) {
									PointsToSet thisLPL = pTa.reachingObjects(m.getActiveBody().getThisLocal());
									if (basePT.hasNonEmptyIntersection(thisLPL)) {
										sink = createFlowFieldSink(a.getAccessPath().getFirstField(), null, a
												.getAccessPath().getTaintSubFields());
									}
								}

							}
						}
						// apl 1
						Local l = a.getAccessPath().getPlainLocal();
						PointsToSet sinkBasePT = pTa.reachingObjects(l);
						for (SootField f : fields) {
							if (!m.isStatic() && !f.isStatic()) {
								PointsToSet pointsToField = pTa.reachingObjects(m.getActiveBody().getThisLocal(), f);
								if (sinkBasePT.hasNonEmptyIntersection(pointsToField)) {
									if (a.getAccessPath().getFieldCount() < 2) {
										sink = createFlowFieldSink(f, a.getAccessPath().getFirstField(), a
												.getAccessPath().getTaintSubFields());
									} else {
										sink = createFlowFieldSink(f, a.getAccessPath().getFirstField(), true);
									}

								}
							}

						}

					}

					// check return sink
					if (a.getAccessPath().getPlainLocal() instanceof JimpleLocal) {
						for (Unit u : m.getActiveBody().getUnits()) {
							if (u instanceof ReturnStmt) {
								ReturnStmt returnStmt = (ReturnStmt) u;
								Value v2 = returnStmt.getOp();
								if (v2 instanceof JimpleLocal) {
									if (v2.equals(a.getAccessPath().getPlainLocal())) {
										if (!a.getAccessPath().isInstanceFieldRef()) {
											// apl 0
											sink = createFlowReturnSink(a.getAccessPath().getTaintSubFields());
										} else {
											// apl 1
											if (a.getAccessPath().getFieldCount() < 2) {
												sink = createFlowReturnSink(a.getAccessPath().getFirstField(), a
														.getAccessPath().getTaintSubFields());
											} else {
												sink = createFlowReturnSink(a.getAccessPath().getFirstField(), true);
											}

										}

									}
								}
							}
						}
					}
					if (source != null && sink != null) {
						addFlow(source, sink, m);

					}
				}

			} else {

			}
		}
		//TODO kill flows
		//Iterate over all souces check if we have a flow for them
		//if we dont have a flow they got killed. -> add them as a kill flow.
		
		logger.info("Finish result processing");
	}

	private List<SootField> getClassFields(SootClass clz) {
		List<SootField> res = new LinkedList<SootField>();
		List<SootClass> impler = Scene.v().getActiveHierarchy().getSubclassesOfIncluding(clz);
		for (SootClass c : impler) {
			res.addAll(c.getFields());
		}
		return res;
	}

	private void addFlow(AbstractFlowSource source, AbstractFlowSink sink, SootMethod m) {
		if (!sink.taintSubFields()) {
			if (source.isField() && sink.isField() && source.getField() == sink.getField() && source.getAccessPath() == sink.getAccessPath()) {
				return;
			}
			if (source.isParamter() && sink.isParamter() && source.getParamterIndex() == sink.getParamterIndex() && source.getAccessPath() == sink.getAccessPath()) {
				return;
			}
		}
		AbstractMethodFlow mFlow = new DefaultMethodFlow(m.getSignature(), source, sink);
		if (foundFlows.contains(mFlow)) {
			return;
		}
		debugMSG(source, sink, m);
		foundFlows.add(mFlow);
		if (flows.containsKey(m)) {
			flows.get(m).add(mFlow);
		} else {
			Set<AbstractMethodFlow> tmp = new HashSet<AbstractMethodFlow>();
			tmp.add(mFlow);
			flows.put(m, tmp);
		}
	}

	private void debugMSG(AbstractFlowSource source, AbstractFlowSink sink, SootMethod m) {

		if (DEBUG) {
			if (m != null)
				System.out.println("\nmethod: " + m.toString());
			System.out.println("source: " + source.toString());
			System.out.println("sink:   " + sink.toString());
			System.out.println("------------------------------------");
		}
	}

	public Set<AbstractMethodFlow> getMethodFlows(SootMethod m) {
		return flows.get(m);
	}

	public Set<SootMethod> getMethods() {
		return flows.keySet();
	}

	public InfoflowResultProcessor(Set<Abstraction> result2, InterproceduralCFG<Unit, SootMethod> cfg, String m,
			boolean ignoreExceptions, SummarySourceSinkManager manager) {
		this.result = result2;
		this.cfg = cfg;
		this.method = m;
		this.ignoreExceptions = ignoreExceptions;
		if (result != null)
			process();
	}

	public Map<SootMethod, Set<AbstractMethodFlow>> getOutResult() {
		return flows;
	}

	@SuppressWarnings("unused")
	private String abstractionPrityPrintDebug(Abstraction a, SourceContextAndPath scp) {
		return "\n----\nSource: " + scp.getStmt().toString() + "\nsink: " + a.getAccessPath().toString() + " _ "
				+ a.getActivationUnit() + "\n---";
	}

}
