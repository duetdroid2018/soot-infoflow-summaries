package soot.jimple.infoflow.methodSummary;

import static soot.jimple.infoflow.methodSummary.data.impl.FlowSinkAndSourceFactory.createFlowFieldSource;
import static soot.jimple.infoflow.methodSummary.data.impl.FlowSinkAndSourceFactory.createFlowParamterSource;
import static soot.jimple.infoflow.methodSummary.data.impl.FlowSinkAndSourceFactory.createFlowThisSource;
import heros.InterproceduralCFG;
import heros.solver.IDESolver;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import soot.Local;
import soot.MethodOrMethodContext;
import soot.PatchingChain;
import soot.PointsToSet;
import soot.Scene;
import soot.SootClass;
import soot.SootField;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.jimple.DefinitionStmt;
import soot.jimple.InstanceFieldRef;
import soot.jimple.ParameterRef;
import soot.jimple.ReturnStmt;
import soot.jimple.ReturnVoidStmt;
import soot.jimple.Stmt;
import soot.jimple.ThisRef;
import soot.jimple.infoflow.methodSummary.data.MethodSummaries;
import soot.jimple.infoflow.methodSummary.data.impl.Source;
import soot.jimple.infoflow.source.ISourceSinkManager;
import soot.jimple.infoflow.source.SourceInfo;
import soot.jimple.toolkits.callgraph.ReachableMethods;

import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

/**
 * SourceSinkManager for computing library summaries
 * 
 * @author Malte Viering
 * @author Steven Arzt
 */
public class SummarySourceSinkManager implements ISourceSinkManager {

	protected final LoadingCache<SootClass, Collection<SootField>> classToFields =
		IDESolver.DEFAULT_CACHE_BUILDER.build(new CacheLoader<SootClass, Collection<SootField>>() {
			@Override
			public Collection<SootField> load(SootClass sc) throws Exception {
				List<SootField> res = new LinkedList<SootField>();
				List<SootClass> impler = Scene.v().getActiveHierarchy().getSuperclassesOfIncluding(method.getDeclaringClass());
				for(SootClass c : impler)
					res.addAll(c.getFields());
				return res;
			}
		});
	
	private final Logger logger = LoggerFactory.getLogger(SummarySourceSinkManager.class);
	private final String methodSig;
	
	private SootMethod method = null;
	private PointsToSet ptsThis = null;
	private final int summaryAccessPathLength;
	private Set<Source>[] sources ;
	public SummarySourceSinkManager(String mSig) {
		this.methodSig = mSig;
		summaryAccessPathLength = 1;
	}

	public SummarySourceSinkManager(String method, MethodSummaries flows) {
		this.methodSig = method;
		summaryAccessPathLength = 1;
	}


	
	@Override
	public SourceInfo getSourceInfo(Stmt sCallSite, InterproceduralCFG<Unit, SootMethod> cfg) {
		if (method == null) {
			method = Scene.v().getMethod(methodSig);
			if (!method.isStatic())
				ptsThis = Scene.v().getPointsToAnalysis().reachingObjects
						(method.getActiveBody().getThisLocal());
		}
		
		// If this is the dummy main method, we skip it
		SootMethod m = cfg.getMethodOf(sCallSite);
		if (m.toString().equals("<dummyMainClass: void dummyMainMethod()>"))
			return null;
		
		if (sCallSite instanceof DefinitionStmt) {
			DefinitionStmt jstmt = (DefinitionStmt) sCallSite;
			Value rightOp = jstmt.getRightOp();
			
			// Check for field reads
			if (rightOp instanceof InstanceFieldRef && ptsThis != null) {
				InstanceFieldRef fieldRef = (InstanceFieldRef) rightOp;
				Local fieldBase = (Local) fieldRef.getBase();
				PointsToSet fieldBasePT = Scene.v().getPointsToAnalysis().reachingObjects(fieldBase);
				
				//field source apl = 2
				for (SootField f : getClassFields()) {
					if (!f.isStatic()) {
						PointsToSet pointsToField = Scene.v().getPointsToAnalysis().reachingObjects
								(method.getActiveBody().getThisLocal(), f);
						if (fieldBasePT.hasNonEmptyIntersection(pointsToField)) {
							System.out.println("source: (this)." + f  +"." + fieldRef.getField() + "  #  " + sCallSite);
							
							return new SourceInfo(true, createFlowFieldSource(f, fieldRef.getField()));
						}
					}
					// Field source apl = 1
					if (fieldBasePT.hasNonEmptyIntersection(ptsThis)) {
						System.out.println("source: (this)." + fieldRef.getField() + "  #  " + sCallSite);
						SourceInfo si =new SourceInfo(false, createFlowFieldSource(fieldRef.getField(), null));
						return si;
					}
				}
				//Scene.v().getPointsToAnalysis().reachingObjects(m.getActiveBody().getThisLocal()).hasNonEmptyIntersection(pTsPara)
				// Check for parameter field reads
				for (int i = 0 ; i < method.getParameterCount(); i++){
					Local para = method.getActiveBody().getParameterLocal(i);
					PointsToSet pTsPara = Scene.v().getPointsToAnalysis().reachingObjects(para);
					if (fieldBasePT.hasNonEmptyIntersection(pTsPara)) {
						System.out.println("source: " + fieldBase +"(Paramter)." +fieldRef.getField() + "  #  " + sCallSite);
						return new SourceInfo(true, createFlowParamterSource(method, i, fieldRef.getField()));
					}
				}
			}
			
			
			SootMethod currentMethod = cfg.getMethodOf(sCallSite);

			// Check for direct parameter accesses
			if (currentMethod == method && rightOp instanceof ParameterRef) {
				ParameterRef pref = (ParameterRef) rightOp;
				logger.debug("source: " + sCallSite + " " + m.getSignature());
				System.out.println("source: " + sCallSite + " " + m.getSignature());
				return new SourceInfo(false, createFlowParamterSource(method, pref.getIndex(), null));
			}
			else if (currentMethod == method && rightOp instanceof ThisRef) {
				System.out.println("source: (this)" + sCallSite + " " + m.getSignature());
				return new SourceInfo(false, createFlowThisSource());
			}
		}
		return null;
	}
	
	@Override
	public boolean isSink(Stmt sCallSite, InterproceduralCFG<Unit, SootMethod> cfg) {
		SootMethod m = cfg.getMethodOf(sCallSite);
		if (m.getSignature().contains("dummyMainClass: void dummyMainMethod()"))
			return false;

		if (isReturnSink(sCallSite, cfg)) {
			logger.debug("sink: " + sCallSite + " method: " + m.getSignature());
			System.out.println("sink: " + sCallSite + " method: " + m.getSignature());
			return true;
		}

		return false;
	}

	private boolean isReturnSink(Stmt sCallSite, InterproceduralCFG<Unit, SootMethod> cfg) {
		if (sCallSite instanceof ReturnStmt || sCallSite instanceof ReturnVoidStmt) {
			if (methodSig != null && methodSig.contains(cfg.getMethodOf(sCallSite).getSignature()))
				return true;
		}
		return false;
	}
	
	private Collection<SootField> getClassFields(){
		return classToFields.getUnchecked(method.getDeclaringClass());
	}
	
}
