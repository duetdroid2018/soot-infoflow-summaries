package soot.jimple.infoflow.methodSummary;

import heros.InterproceduralCFG;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import soot.Local;
import soot.PointsToAnalysis;
import soot.PointsToSet;
import soot.Scene;
import soot.SootClass;
import soot.SootField;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.jimple.IdentityRef;
import soot.jimple.IdentityStmt;
import soot.jimple.ParameterRef;
import soot.jimple.ReturnStmt;
import soot.jimple.ReturnVoidStmt;
import soot.jimple.Stmt;
import soot.jimple.ThisRef;
import soot.jimple.infoflow.methodSummary.data.AbstractFlowSource;
import soot.jimple.infoflow.methodSummary.data.AbstractMethodFlow;
import soot.jimple.infoflow.source.ISourceSinkManager;
import soot.jimple.infoflow.source.SourceInfo;
import soot.jimple.internal.JAssignStmt;
import soot.jimple.internal.JInstanceFieldRef;
import static soot.jimple.infoflow.methodSummary.data.impl.FlowSinkAndSourceFactory.*;

/**
 * 
 * @author mv
 * 
 */
public class SummarySourceSinkManager implements ISourceSinkManager {

	private boolean allFieldsAsSource = true;
	private boolean certainFieldsAsSource = true;
	private boolean paramterAsSource = true;
	private boolean thisAsSource = true;
	private final Logger logger = LoggerFactory.getLogger(SummarySourceSinkManager.class);
	private final String methodSig;
	private SootMethod method = null;
	private Local mThisLocal;
	private PointsToSet objBasePointsTo = null;
	private Set<String> fieldSinks = null;

	private Set<AbstractFlowSource> foundSources = new HashSet<AbstractFlowSource>();
	
	
	public SummarySourceSinkManager(String mSig) {
		this.methodSig = mSig;

	}

	public SummarySourceSinkManager(String method, Map<String, Set<AbstractMethodFlow>> flows) {
		this.methodSig = method;
		allFieldsAsSource = false;
		certainFieldsAsSource = true;
		this.fieldSinks = new HashSet<String>();
		for (String s : flows.keySet()) {
			for (AbstractMethodFlow flow : flows.get(s)) {
				if (flow.sink().isField())
					fieldSinks.add(flow.sink().getField());
			}
		}
	}

	@Override
	public SourceInfo getSourceInfo(Stmt sCallSite, InterproceduralCFG<Unit, SootMethod> cfg) {
		PointsToAnalysis pA = Scene.v().getPointsToAnalysis();
		if (method == null) {
			method = Scene.v().getMethod(methodSig);
			if (!method.isStatic() && !method.isAbstract()) {
				mThisLocal = method.getActiveBody().getThisLocal();
				objBasePointsTo = pA.reachingObjects(method.getActiveBody().getThisLocal());
			}
		}



		SootMethod m = cfg.getMethodOf(sCallSite);

		if (m.toString().equals("<dummyMainClass: void dummyMainMethod()>"))
			return null;

		if (sCallSite instanceof JAssignStmt) {
			JAssignStmt jstmt = (JAssignStmt) sCallSite;
			Value rightOp = jstmt.getRightOp();
			if (rightOp instanceof JInstanceFieldRef) {
				JInstanceFieldRef fieldRef = (JInstanceFieldRef) rightOp;
				Value u = fieldRef.getBase();

				if (u instanceof Local) {
					Local fieldBase = (Local) u;
					PointsToSet fielBasePT = pA.reachingObjects(fieldBase);
					
					
					//field source apl = 1
					for (SootField f : getClassFields()) {
						if (!f.isStatic() && mThisLocal != null) {
							PointsToSet pointsToField = pA.reachingObjects(mThisLocal, f);
							if (fielBasePT.hasNonEmptyIntersection(pointsToField)) {
								System.out.println("source: (this)." + f  +"." + fieldRef.getField() + "  #  " + sCallSite);
								foundSources.add(createFlowFieldSource(f, fieldRef.getField()));
								return new SourceInfo(true);
							}
						}else if(f.isStatic()){
							pA.reachingObjects(f);
						}
					}
					
					//field source apl = 0
					if(objBasePointsTo != null && fielBasePT != null &&objBasePointsTo.hasNonEmptyIntersection(fielBasePT)){
						System.out.println("source: (this)." + fieldRef.getField() + "  #  " + sCallSite);
						foundSources.add(createFlowFieldSource(fieldRef.getField(), null));
						return new SourceInfo(false);
					}
			
					//paramter source apl = 1
					for( int i = 0 ; i < method.getParameterCount(); i++){					
						Local para = method.getActiveBody().getParameterLocal(i);
						PointsToSet pTsPara = pA.reachingObjects(para);
						if (fielBasePT.hasNonEmptyIntersection(pTsPara)) {
							System.out.println("source: " + fieldBase +"(Paramter)." +fieldRef.getField() + "  #  " + sCallSite);
							foundSources.add(createFlowParamterSource(method,i , fieldRef.getField()));
							return new SourceInfo(true);
						}
					}	
				}
			}
		}
		if (paramterAsSource && getParameterIdxIfParameter(sCallSite, cfg) != -1) {
			logger.debug("source: " + sCallSite + " " + m.getSignature());
			System.out.println("source: " + sCallSite + " " + m.getSignature());
			foundSources.add(createFlowParamterSource(method, getParameterIdxIfParameter(sCallSite,cfg), null));
			return new SourceInfo(false);
		} else if (thisAsSource && isRelevantIdentityRef(sCallSite, cfg)) {
			System.out.println("source: (this)" + sCallSite + " " + m.getSignature());
			foundSources.add(createFlowThisSource());
			return new SourceInfo(false);
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


	private List<SootField> getClassFields(){
		List<SootField> res = new LinkedList<SootField>();
		List<SootClass> impler = Scene.v().getActiveHierarchy().getSubclassesOfIncluding(method.getDeclaringClass());
		for(SootClass c : impler){
			res.addAll(c.getFields());
		}
		return res;
	}
	
	private int getParameterIdxIfParameter(Stmt sCallSite, InterproceduralCFG<Unit, SootMethod> cfg) {
		if (sCallSite instanceof IdentityStmt) {
			IdentityStmt is = (IdentityStmt) sCallSite;
			if (is.getRightOp() instanceof ParameterRef) {
				
				if (this.methodSig != null && this.methodSig.contains(cfg.getMethodOf(sCallSite).getSignature())) {
					return ((ParameterRef) is.getRightOp()).getIndex();
				}
			}
		}
		return -1;
	}

	private boolean isRelevantIdentityRef(Stmt sCallSite, InterproceduralCFG<Unit, SootMethod> cfg) {
		if (sCallSite instanceof IdentityStmt) {
			IdentityStmt is = (IdentityStmt) sCallSite;
			if (is.getRightOp() instanceof IdentityRef) { // && thisAsSource) {
				if (this.methodSig != null && this.methodSig.contains(cfg.getMethodOf(sCallSite).getSignature())) {
					Value rOp = is.getRightOp();
					if (rOp instanceof ThisRef)
						return true;
				}
			}
		}
		return false;
	}

	public boolean isThisAsSource() {
		return thisAsSource;
	}

	public void setThisAsSource(boolean thisAsSource) {
		this.thisAsSource = thisAsSource;
	}

	public boolean isAllFieldsAsSource() {
		return allFieldsAsSource;
	}

	public void setAllFieldsAsSource(boolean allFieldsAsSource) {
		this.allFieldsAsSource = allFieldsAsSource;
	}

	public boolean isParamterAsSource() {
		return paramterAsSource;
	}

	public void setParamterAsSource(boolean paramterAsSource) {
		this.paramterAsSource = paramterAsSource;
	}

	public boolean isCertainFieldsAsSource() {
		return certainFieldsAsSource;
	}

	public void setCertainFieldsAsSource(boolean certainFieldsAsSource) {
		this.certainFieldsAsSource = certainFieldsAsSource;
	}
	public Set<AbstractFlowSource> getFoundSources() {
		return foundSources;
	}

}
