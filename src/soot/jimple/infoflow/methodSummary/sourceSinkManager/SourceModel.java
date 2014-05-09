package soot.jimple.infoflow.methodSummary.sourceSinkManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
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
import soot.jimple.infoflow.methodSummary.data.DefaultFlowSource;
import soot.jimple.infoflow.methodSummary.sourceSinkFactory.SourceSinkFactory;
import soot.jimple.toolkits.callgraph.ReachableMethods;

/**
 * Identifies all possible sources for a method m up to a defined access path length.
 * 
 * l = x[.f] is a source with access path length 0 if:
 * 	x is 
 * 		- a parameter of m
 * 		- the this local of m
 * l = x.f is a source with access path length n if:
 * 	x points to a local x' and x' is a source with apl n-1		
 * 
 * Requirement: Soot must be live
 */
public class SourceModel {
	private final int summaryAccessPathLength;
	
	//found sources, list position = apl
	private List<Set<SourceDataInternal>> sources;
	
	private SootMethod method;
	private Collection<SootField> fields;
	private PointsToAnalysis pta;
	private Local thisLocal;
	private PointsToSet thisPt;

	public SourceModel(SootMethod method, int apLength, Collection<SootField> fields) {
		summaryAccessPathLength = apLength +1;
		this.method = method;
		this.fields = fields;
		sources = new ArrayList<Set<SourceDataInternal>>(apLength);
		boolean skip = !(method.hasActiveBody() && method.isConcrete() && !method.isStatic());
		if (!skip) {
			init();
		}
	}
	private void init(){
		pta = Scene.v().getPointsToAnalysis();
		thisLocal = method.getActiveBody().getThisLocal();
		thisPt = pta.reachingObjects(thisLocal);
		
		for (int i = 0; i < summaryAccessPathLength; i++) {
			sources.add(i, new HashSet<SourceDataInternal>());
		}

		for (int i = 0; i < method.getParameterCount(); i++) {
			Local p = method.getActiveBody().getParameterLocal(i);
			PointsToSet ptp = pta.reachingObjects(p);
			sources.get(0).add(new SourceDataInternal(SourceSinkFactory.createParamterSource(method, i, null),p, p,null, ptp, false));
		}
		sources.get(0).add(new SourceDataInternal(SourceSinkFactory.createThisSource(), thisLocal, thisLocal, null,thisPt, false));
		buildSourceModel();
	}

	/**
	 * checks for a x.f if it is a source
	 * if x.f is a source it returns:
	 * 	the FlowSourceForSummary for all possible sources and
	 * 	the information if we need to taint sub fields
	 *  
	 * Since source identification works with points to it can happen
	 * that we identify multiple sources with x.f 
	 */
	public SourceData isSource(Local x, SootField f) {
		boolean matchedLocal = false;
		List<DefaultFlowSource> res = new LinkedList<DefaultFlowSource>();
		boolean taintSubFields = false;
		for (int i = 1; i < sources.size(); i++) {
			for (SourceDataInternal s : sources.get(i)) {
				if (x.equals(s.getFieldBase())){
					matchedLocal = true;
					if( (f == null && s.getField() == null) || (f != null && f.equals(s.getField()))){
						res.add(s.getSourceInfo());
						taintSubFields |= s.isTaintSubFields();
						//return s;
					}
					
				}
					
			}
		}
		if(matchedLocal && res.size() == 0){
			throw new RuntimeException("the local: " +x + " is a source but we dont have it in our source model" );
		}
		if(res.size() == 0)
			return null;
		
		return new SourceData(res, taintSubFields);
	}
	
	
	/**
	 * Builds a model of all sources up to the limited access path
	 * 
	 * (1) Iterate over all reachable methods.
	 * 		(1a) identify all sources in methods
	 * (2) if a new sources was found: 
	 * 		repeat (1) 
	 */
	private void buildSourceModel() {
		List<MethodOrMethodContext> eps = new ArrayList<MethodOrMethodContext>(Scene.v().getEntryPoints());
		ReachableMethods reachableMethods = new ReachableMethods(Scene.v().getCallGraph(), eps.iterator(), null);
		reachableMethods.update();
		boolean repeat = true;
		int count = 1;
		//if we find a new sources we need to repeat, since this source can be the base of a new source
		while (repeat == true) {
			repeat = false;
			for (Iterator<MethodOrMethodContext> iter = reachableMethods.listener(); iter.hasNext();) {
				SootMethod m = iter.next().method();
				if (m.hasActiveBody()) {
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

	

	
	/**
	 * Checks for every stmt in a method m if it is a source with apl > 0
	 * @param m
	 * @return
	 */
	private boolean buildSourceModelMethod(SootMethod m) {
		boolean change = false;
		PatchingChain<Unit> units = m.getActiveBody().getUnits();
		for (Unit u : units) {
			if(u instanceof Stmt){
				Stmt s = (Stmt) u;
				change |= checkIfStmtIsSource(s);
			}
		}
		return change;
	}
	
	private boolean checkIfStmtIsSource(Stmt s){
		if(s.toString().contains("first")){
			int i = 3;
		}
			
		if (s instanceof DefinitionStmt) {
			DefinitionStmt stmt = (DefinitionStmt) s;
			Value righOp = stmt.getRightOp();
			if (righOp instanceof InstanceFieldRef) {
				//we must have ... = x.f if we have a source with apl > 0
				InstanceFieldRef fiedRef = (InstanceFieldRef) righOp;
				if (fiedRef.getBase() instanceof Local) { 
					Local base = (Local) fiedRef.getBase();
					PointsToSet localPt = pta.reachingObjects((Local) fiedRef.getBase());
					Local leftOp = (Local) stmt.getLeftOp();
					for (int i = 0; i < summaryAccessPathLength - 1; i++) {
						checkIfIsSourceWithAPLOfN(i, base, localPt, leftOp, fiedRef);
					}
					
					
				}
			}

		}
		return false;
	}
	
	private boolean checkIfIsSourceWithAPLOfN(int aplN, Local base, PointsToSet basePt, Local leftOp, InstanceFieldRef fieldRef){
		Set<SourceDataInternal> sourceSet = sources.get(aplN);
		for (SourceDataInternal source : sourceSet) {
			if (source.pointsTo(basePt, base)) {
				return addNewSource(aplN, source, base, fieldRef,pta.reachingObjects(leftOp), leftOp);
			}
		}
		return false;
	}
	

	private boolean addNewSource(int apl, SourceDataInternal oldSource, Local base, InstanceFieldRef fieldRef, PointsToSet localPt, Value l) {
		boolean star = apl + 1 >= summaryAccessPathLength;
		return sources.get(apl + 1).add(new SourceDataInternal(
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
				for (SourceDataInternal s : sources.get(i)) {
					buf.append(s.getSourceInfo().toString() + "\n");
				}
			}
		}

		return buf.toString();
	}
}
