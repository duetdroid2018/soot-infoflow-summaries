package soot.jimple.infoflow.methodSummary;

import static soot.jimple.infoflow.methodSummary.data.impl.FlowSinkAndSourceFactory.createFlowFieldSource;
import static soot.jimple.infoflow.methodSummary.data.impl.FlowSinkAndSourceFactory.createFlowParamterSource;
import static soot.jimple.infoflow.methodSummary.data.impl.FlowSinkAndSourceFactory.createFlowThisSource;
import heros.InterproceduralCFG;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import soot.Local;
import soot.MethodOrMethodContext;
import soot.PatchingChain;
import soot.PointsToAnalysis;
import soot.PointsToSet;
import soot.Scene;
import soot.SootField;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.jimple.DefinitionStmt;
import soot.jimple.InstanceFieldRef;
import soot.jimple.ParameterRef;
import soot.jimple.Stmt;
import soot.jimple.ThisRef;
import soot.jimple.infoflow.methodSummary.data.impl.Source;
import soot.jimple.infoflow.source.SourceInfo;
import soot.jimple.toolkits.callgraph.ReachableMethods;

public class SourceModel {
	private final int summaryAccessPathLength;
	private Set<Source>[] sources;
	private boolean init = false;
	private SootMethod method;
	private Collection<SootField> fields;
	private PointsToAnalysis pta;

	public SourceModel(SootMethod m, int apLength, Collection<SootField> fields) {
		summaryAccessPathLength = apLength;
		this.method = m;
		this.fields = fields;
		sources = new HashSet[summaryAccessPathLength];
		for (int i = 0; i < summaryAccessPathLength; i++) {
			sources[i] = new HashSet<Source>();
		}

		pta = Scene.v().getPointsToAnalysis();
		if (method.hasActiveBody()) {
			for (int i = 0; i < method.getParameterCount(); i++) {
				sources[0].add(new Source(createFlowParamterSource(method, i, null), pta.reachingObjects(method
						.getActiveBody().getParameterLocal(i))));
			}
		}
		for (SootField f : fields) {
			sources[0].add(new Source(createFlowFieldSource(f, null), pta.reachingObjects(method.getActiveBody()
					.getThisLocal(), f)));
		}

	}

	/**
	 * builds a modle of all soucres up to the limited access path
	 * 
	 */
	private void buildSourceModel() {
		List<MethodOrMethodContext> eps = new ArrayList<MethodOrMethodContext>(Scene.v().getEntryPoints());
		ReachableMethods reachableMethods = new ReachableMethods(Scene.v().getCallGraph(), eps.iterator(), null);
		reachableMethods.update();
		boolean repeat = true;
		while (repeat == true) {
			repeat = false;
			for (Iterator<MethodOrMethodContext> iter = reachableMethods.listener(); iter.hasNext();) {
				SootMethod m = iter.next().method();
				if (m.hasActiveBody()) {
					buildSourceModelMethod(m);
				}
			}
		}
	}
	private void buildSourceModelMethod(SootMethod m ){
		PatchingChain<Unit> units = m.getActiveBody().getUnits();
		for (Unit u : units) {
			Stmt s = (Stmt) u;
			// we check for every stmt if it is a soucre
			if (s instanceof DefinitionStmt) {
				Value righOp = ((DefinitionStmt) s).getRightOp();
				if (righOp instanceof InstanceFieldRef) {
					InstanceFieldRef fiedRef = (InstanceFieldRef) righOp;
					if (fiedRef.getBase() instanceof Local) {
						PointsToSet localPt = pta.reachingObjects((Local) fiedRef.getBase());
						for (int i = 0; i < summaryAccessPathLength; i++) {
							for (Set<Source> sourceSet : sources) {
								for(Source source : sourceSet){
									if(source.pointsTo(localPt)){
										addNewSource(i, source, fiedRef, localPt);
									}
								}
							}
						}
					}
				}

			}
		}
	}
	private void addNewSource(int apl, Source oldSource, InstanceFieldRef fieldRef, PointsToSet localPt){
		
	}
	
}
