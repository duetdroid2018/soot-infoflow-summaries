package soot.jimple.infoflow.methodSummary;

import static soot.jimple.infoflow.methodSummary.data.impl.FlowSinkAndSourceFactory.createFlowParamterSource;
import static soot.jimple.infoflow.methodSummary.data.impl.FlowSinkAndSourceFactory.createFlowThisSource;
import heros.InterproceduralCFG;
import heros.solver.IDESolver;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import soot.Local;
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
	private SourceModel sModel = null;
	
	
	public SummarySourceSinkManager(String mSig) {
		this.methodSig = mSig;
		summaryAccessPathLength = 5;
		
	}

	public SummarySourceSinkManager(String method, MethodSummaries flows) {
		this.methodSig = method;
		summaryAccessPathLength = 5;
	}


	
	@Override
	public SourceInfo getSourceInfo(Stmt sCallSite, InterproceduralCFG<Unit, SootMethod> cfg) {
		if (method == null) {
			method = Scene.v().getMethod(methodSig);
			if (!method.isStatic())
				ptsThis = Scene.v().getPointsToAnalysis().reachingObjects
						(method.getActiveBody().getThisLocal());
		}
		if(sModel == null){
			sModel = new SourceModel(method, summaryAccessPathLength, getClassFields());
			System.out.println(sModel.toString());
			System.out.println();
		}
		
		SootMethod m = cfg.getMethodOf(sCallSite);
		if(m.toString().contains("dataParameter3"))
			System.out.println();
		if (m.toString().equals("<dummyMainClass: void dummyMainMethod()>"))
			return null;

		
		if (sCallSite instanceof DefinitionStmt) {
			DefinitionStmt jstmt = (DefinitionStmt) sCallSite;
			Value rightOp = jstmt.getRightOp();
			if(rightOp instanceof InstanceFieldRef){
				Source si = sModel.isSource((Local) ((InstanceFieldRef) rightOp).getBase(),((InstanceFieldRef) rightOp).getField());
				if(si != null){
					System.out.println("source: " + sCallSite + " # " + m.getSignature());
					return new SourceInfo(si.getStar(), si.getSourceInfo());
				}
				
			}
		}

//		// If this is the dummy main method, we skip it
		
		if (sCallSite instanceof DefinitionStmt) {
			DefinitionStmt jstmt = (DefinitionStmt) sCallSite;
			Value rightOp = jstmt.getRightOp();
					
			
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
