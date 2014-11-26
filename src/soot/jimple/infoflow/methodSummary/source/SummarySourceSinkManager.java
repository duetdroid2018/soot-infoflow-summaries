package soot.jimple.infoflow.methodSummary.source;

import heros.InterproceduralCFG;
import heros.solver.IDESolver;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import soot.Scene;
import soot.SootClass;
import soot.SootField;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.jimple.DefinitionStmt;
import soot.jimple.ParameterRef;
import soot.jimple.ReturnStmt;
import soot.jimple.ReturnVoidStmt;
import soot.jimple.Stmt;
import soot.jimple.ThisRef;
import soot.jimple.infoflow.methodSummary.data.factory.SourceSinkFactory;
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
	
	private boolean debug = false;
	

	private final Logger logger = LoggerFactory.getLogger(SummarySourceSinkManager.class);
	private final String methodSig;
	private final SourceSinkFactory sourceSinkFactory;
	
	private SootMethod method = null;
	
	public SummarySourceSinkManager(String mSig, SourceSinkFactory sourceSinkFactory) {
		this.methodSig = mSig;
		this.sourceSinkFactory = sourceSinkFactory;
	}
	
	@Override
	public SourceInfo getSourceInfo(Stmt sCallSite, InterproceduralCFG<Unit, SootMethod> cfg) {
		// Initialize the method we are interested in
		if(method == null)
			method = Scene.v().getMethod(methodSig);
		
		// If this is not the method we are looking for, we skip it
		SootMethod currentMethod = cfg.getMethodOf(sCallSite);
		if (currentMethod != method)
			return null;
		
		if (sCallSite instanceof DefinitionStmt) {
			DefinitionStmt jstmt = (DefinitionStmt) sCallSite;
			Value rightOp = jstmt.getRightOp();
			
			//check if we have a source with apl = 0 (this or parameter source)
			if (currentMethod == method && rightOp instanceof ParameterRef) {
				ParameterRef pref = (ParameterRef) rightOp;
				logger.debug("source: " + sCallSite + " " + currentMethod.getSignature());
				if(debug)
					System.out.println("source: " + sCallSite + " " + currentMethod.getSignature());
				return new SourceInfo(true, Collections.singletonList(
						sourceSinkFactory.createParameterSource(pref.getIndex())));
			}
			else if (currentMethod == method && rightOp instanceof ThisRef) {
				if(debug)
					System.out.println("source: (this)" + sCallSite + " " + currentMethod.getSignature());				
				return new SourceInfo(true, Collections.singletonList(
						sourceSinkFactory.createThisSource()));
			}
		}
		return null;
	}
	
	@Override
	public boolean isSink(Stmt sCallSite, InterproceduralCFG<Unit, SootMethod> cfg) {
		// Initialize the method we are interested in
		if(method == null)
			method = Scene.v().getMethod(methodSig);
		
		// If this is not the method we are looking for, we skip it
		SootMethod currentMethod = cfg.getMethodOf(sCallSite);
		if (currentMethod != method)
			return false;
		
		return sCallSite instanceof ReturnStmt
				|| sCallSite instanceof ReturnVoidStmt;
	}
	
}
