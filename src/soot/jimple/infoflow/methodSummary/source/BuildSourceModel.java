package soot.jimple.infoflow.methodSummary.source;

import java.util.ArrayList;
import java.util.Collection;
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
import soot.jimple.infoflow.methodSummary.data.factory.SourceSinkFactory;
import soot.jimple.toolkits.callgraph.ReachableMethods;

/**
 * Identifies all possible sources for a method m up to a defined access path length.
 * 
 * l = x is a source with access path length 0 if:
 * 	x is 
 * 		- a parameter of m
 * 		- the this local of m
 * l = x.f is a source with access path length n if:
 * 	x points to a local x' and x' is a source with apl n-1		
 * 
 * Requirement: Soot must be live
 */
public class BuildSourceModel {
	private final int summaryAccessPathLength;
	private SootMethod method;
	//private Collection<SootField> fields;
	private PointsToAnalysis pta;
	private Local thisLocal;
	private PointsToSet thisPt;

	private SourceModel sourceModel;
	
	public BuildSourceModel(SootMethod method, int apLength, Collection<SootField> fields) {
		summaryAccessPathLength = apLength +1;
		sourceModel = new SourceModel(apLength);
		this.method = method;
		//this.fields = fields;
		
		boolean skip = !(method.hasActiveBody() && method.isConcrete() && !method.isStatic());
		if (!skip) {
			System.out.println("Bulding Source Model for: " + method.getSignature());
			System.out.println(method.getActiveBody().toString());
			buildModel();
		}
	}
	
	private SourceModel buildModel(){
		pta = Scene.v().getPointsToAnalysis();
		thisLocal = method.getActiveBody().getThisLocal();
		thisPt = pta.reachingObjects(thisLocal);
		for (int i = 0; i < method.getParameterCount(); i++) {
			Local p = method.getActiveBody().getParameterLocal(i);
			PointsToSet ptp = pta.reachingObjects(p);
			sourceModel.addSource(0,new SourceDataInternal(SourceSinkFactory.createParamterSource(method, i, null),p, p,null, ptp, false));
		}
		sourceModel.addSource(0,new SourceDataInternal(SourceSinkFactory.createThisSource(), thisLocal, thisLocal, null,thisPt, false));
		buildSourceModel();
		return sourceModel;
	}

	public SourceModel getModel(){
		return sourceModel;
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
				change |= isStmtASource(s);
			}
		}
		return change;
	}
	
	private boolean isStmtASource(Stmt s){
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
						isStmtASourceWithAPLN(i, base, localPt, leftOp, fiedRef);
					}
					
					
				}
			}

		}
		return false;
	}
	
	private boolean isStmtASourceWithAPLN(int aplN, Local base, PointsToSet basePt, Local leftOp, InstanceFieldRef fieldRef){
		Set<SourceDataInternal> sourceSet = sourceModel.getSources(aplN);
		for (SourceDataInternal source : sourceSet) {
			if (source.pointsTo(basePt, base)) {
				return addNewSource(aplN, source, base, fieldRef,pta.reachingObjects(leftOp), leftOp);
			}
		}
		return false;
	}
	

	private boolean addNewSource(int apl, SourceDataInternal oldSource, Local base, InstanceFieldRef fieldRef, PointsToSet localPt, Value l) {
		boolean taintSubFields = apl + 1 >= summaryAccessPathLength;
		return sourceModel.addSource(apl + 1, 
				new SourceDataInternal(
				oldSource.getSourceInfo().createNewSource(fieldRef.getField()), 
				base,
				(Local) l,
				fieldRef.getField(),
				localPt, 
				taintSubFields));
	}

	@Override
	public String toString() {
		return sourceModel.toString();
	}
}
