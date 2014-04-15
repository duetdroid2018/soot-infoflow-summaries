package soot.jimple.infoflow.methodSummary;

import static soot.jimple.infoflow.methodSummary.data.impl.FlowSinkAndSourceFactory.createFlowParamterSource;
import static soot.jimple.infoflow.methodSummary.data.impl.FlowSinkAndSourceFactory.createFlowThisSource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
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
import soot.jimple.Stmt;
import soot.jimple.infoflow.methodSummary.data.impl.Source;
import soot.jimple.toolkits.callgraph.ReachableMethods;

public class SourceModel {
	private final int summaryAccessPathLength;
	private List<Set<Source>> sources;
	private SootMethod method;
	private Collection<SootField> fields;
	private PointsToAnalysis pta;
	private Local thisLocal;
	private PointsToSet ptThis;

	public SourceModel(SootMethod m, int apLength, Collection<SootField> fields) {
		summaryAccessPathLength = apLength;
		this.method = m;
		this.fields = fields;
		sources = new ArrayList<Set<Source>>(apLength);
		boolean skip = !(m.hasActiveBody() && m.isConcrete() && !m.isStatic());
		if (!skip) {
			pta = Scene.v().getPointsToAnalysis();
			thisLocal = method.getActiveBody().getThisLocal();
			ptThis = pta.reachingObjects(thisLocal);
			for (int i = 0; i < summaryAccessPathLength; i++) {
				sources.add(i, new HashSet<Source>());
			}

			for (int i = 0; i < method.getParameterCount(); i++) {
				Local p = method.getActiveBody().getParameterLocal(i);
				PointsToSet ptp = pta.reachingObjects(p);
				sources.get(0).add(new Source(createFlowParamterSource(method, i, null),p, p,null, ptp, false));
			}
			sources.get(0).add(new Source(createFlowThisSource(), thisLocal, thisLocal, null,ptThis, false));
			buildSourceModel();
		}
	}

	/**
	 * builds a model of all sources up to the limited access path
	 * 
	 */
	private void buildSourceModel() {
		List<MethodOrMethodContext> eps = new ArrayList<MethodOrMethodContext>(Scene.v().getEntryPoints());
		ReachableMethods reachableMethods = new ReachableMethods(Scene.v().getCallGraph(), eps.iterator(), null);
		reachableMethods.update();
		boolean repeat = true;
		int count = 1;
		while (repeat == true) {
			repeat = false;
			for (Iterator<MethodOrMethodContext> iter = reachableMethods.listener(); iter.hasNext();) {
				SootMethod m = iter.next().method();
				if (m.hasActiveBody()) {
					if (m.getSignature().contains("methodSummary"))
						System.out.println();
					if (buildSourceModelMethod(m)) {
						if (count < summaryAccessPathLength) {
							count++;
							repeat = true;
						}
					}
				}
			}
		}
	}

	private boolean buildSourceModelMethod(SootMethod m) {
		boolean changes = false;
		PatchingChain<Unit> units = m.getActiveBody().getUnits();
		for (Unit u : units) {
			Stmt s = (Stmt) u;
			// we check for every stmt if it is a soucre
			if (s instanceof DefinitionStmt) {
				DefinitionStmt stmt = (DefinitionStmt) s;
				Value righOp = stmt.getRightOp();
				if (righOp instanceof InstanceFieldRef) {
					InstanceFieldRef fiedRef = (InstanceFieldRef) righOp;
					if (fiedRef.getBase() instanceof Local) {
						PointsToSet localPt = pta.reachingObjects((Local) fiedRef.getBase());
						for (int i = 0; i < summaryAccessPathLength - 1; i++) {
							Set<Source> sourceSet = sources.get(i);
							for (Source source : sourceSet) {
								if (source.pointsTo(localPt, (Local) fiedRef.getBase())) {
									changes |= addNewSource(i, source, (Local) fiedRef.getBase(), fiedRef,
											pta.reachingObjects((Local) stmt.getLeftOp()), stmt.getLeftOp());
								}
							}

						}
					}
				}

			}
		}
		return changes;
	}

	private boolean addNewSource(int apl, Source oldSource, Local base, InstanceFieldRef fieldRef, PointsToSet localPt, Value l) {
		boolean star = apl + 1 >= summaryAccessPathLength;
		return sources.get(apl + 1).add(new Source(
				oldSource.getSourceInfo().createNewSource(fieldRef.getField()), 
				base,
				(Local) l,
				fieldRef.getField(),
				localPt, 
				star));
	}

	@Override
	public String toString() {
		StringBuffer buf = new StringBuffer();
		for (int i = 1; i < summaryAccessPathLength; i++) {
			buf.append("APL: " + i + "\n");
			if (sources.size() >= i && sources.get(i) != null) {
				for (Source s : sources.get(i)) {
					buf.append(s.getSourceInfo().toString() + "\n");
				}
			}
		}

		return buf.toString();
	}

	public Source isSource(Local l, SootField f) {
		boolean matchedLocal = false;
		for (int i = 1; i < sources.size(); i++) {
			for (Source s : sources.get(i)) {
				if (l.equals(s.getFieldBase())){
					matchedLocal = true;
					if( (f == null && s.getField() == null) || (f != null && f.equals(s.getField()))){
						return s;
					}
					
				}
					
			}
		}
		if(matchedLocal){
			throw new RuntimeException("the local: " +l + " is a source but we dont have it in our source model" );
		}
		return null;
	}
}
