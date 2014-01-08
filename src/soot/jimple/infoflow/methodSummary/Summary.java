package soot.jimple.infoflow.methodSummary;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import soot.SootMethod;
import soot.Unit;
import soot.jimple.infoflow.Infoflow;
import soot.jimple.infoflow.InfoflowResults;
import soot.jimple.infoflow.config.IInfoflowConfig;
import soot.jimple.infoflow.entryPointCreators.BaseEntryPointCreator;
import soot.jimple.infoflow.entryPointCreators.DefaultEntryPointCreator;
import soot.jimple.infoflow.handlers.ResultsAvailableHandler;
import soot.jimple.infoflow.methodSummary.data.AbstractMethodFlow;
import soot.jimple.infoflow.methodSummary.util.InfoflowResultProcessor;
import soot.jimple.infoflow.methodSummary.util.MergeSummaries;
import soot.jimple.infoflow.methodSummary.util.SummaryTaintPropagationHandler;
import soot.jimple.infoflow.source.ISourceSinkManager;
import soot.jimple.infoflow.taintWrappers.EasyTaintWrapper;
import soot.jimple.infoflow.taintWrappers.ITaintPropagationWrapper;
import soot.jimple.infoflow.taintWrappers.IdentityTaintWrapper;
import soot.jimple.toolkits.ide.icfg.BiDiInterproceduralCFG;

/**
 * 
 * @author mv
 * 
 */
public class Summary {
	
	protected int accessPathLength = 3;
	protected boolean enableImplicitFlows = false;
	protected boolean enableExceptionTracking = true;
	protected boolean enableStaticFieldTracking = true;
	protected boolean flowSensitiveAliasing = true;
	protected boolean ignoreExceptions = true;
	protected boolean useTainWrapper = false;
	protected boolean debug = false;
	protected ITaintPropagationWrapper taintWrapper;
	protected IInfoflowConfig config;
	protected String path;
	protected List<String> substitutedWith = new LinkedList<String>();
	

	protected Map<String, Set<AbstractMethodFlow>> summeries = new HashMap<String, Set<AbstractMethodFlow>>(127);

	public Summary() {
		summeries = new HashMap<String, Set<AbstractMethodFlow>>(127);
		substitutedWith.add("java.util.LinkedList");
		substitutedWith.add("java.util.HashMap");
		//substitutedWith.add("java.util.TreeMap");
		initDefPath();
	}

	public Map<String, Set<AbstractMethodFlow>> createMethodSummary(final String m) {
		return createMethodSummary(m, createSourceSinkManger(m));
	}

	public Map<String, Set<AbstractMethodFlow>> createMethodSummary(final String m, SummarySourceSinkManager manager) {
		return createMethodSummary(m, manager, null);
	}

	public Map<String, Set<AbstractMethodFlow>> createMethodSummary(final String m, final SummarySourceSinkManager manager,
			IdentityTaintWrapper wrapper) {
		if (wrapper != null) {
			useTainWrapper = true;
			taintWrapper = wrapper;
		}
		Infoflow infoflow = initInfoflow();
		String sig = m;
		final SummaryTaintPropagationHandler listener = new SummaryTaintPropagationHandler(m);
		infoflow.addTaintPropagationHandler(listener);
		infoflow.addResultsAvailableHandler(new ResultsAvailableHandler() {
			SummaryTaintPropagationHandler l = listener;

			@Override
			public void onResultsAvailable(BiDiInterproceduralCFG<Unit, SootMethod> cfg, InfoflowResults results) {
				InfoflowResultProcessor processor = new InfoflowResultProcessor(l.getResult(), cfg, m,ignoreExceptions,manager);
				Map<String, Set<AbstractMethodFlow>> tmp = new HashMap<String, Set<AbstractMethodFlow>>(
						(int) (processor.getOutResult().size() * 1.3));
				for (SootMethod m : processor.getOutResult().keySet()) {
					tmp.put(m.getSignature(), processor.getOutResult().get(m));
				}
				//??
				//summeries.putAll(tmp);
				summeries = MergeSummaries.putAll(summeries, tmp);
				
				
				processor = null;
			}
		});
		BaseEntryPointCreator dEntryPointCreater =createEntryPoint();
		ISourceSinkManager sourceSinkManger = manager;
		infoflow.computeInfoflow(path, dEntryPointCreater, java.util.Collections.singletonList(sig), sourceSinkManger);
		return summeries;
	}
	private BaseEntryPointCreator createEntryPoint(){
		DefaultEntryPointCreator dEntryPointCreater = new DefaultEntryPointCreator();
		dEntryPointCreater.setSubstituteClasses(substitutedWith);
		dEntryPointCreater.setSubstituteCallParams(true);
		return dEntryPointCreater;
	}

	protected Infoflow initInfoflow() {
		Infoflow iFlow = new Infoflow();
		iFlow.setAccessPathLength(accessPathLength);
		iFlow.setEnableImplicitFlows(enableImplicitFlows);
		iFlow.setEnableExceptionTracking(enableExceptionTracking);
		iFlow.setEnableStaticFieldTracking(enableStaticFieldTracking);
		iFlow.setFlowSensitiveAliasing(flowSensitiveAliasing);
		if (useTainWrapper) {
			if (taintWrapper == null) {
				try {
					iFlow.setTaintWrapper(new EasyTaintWrapper(new File("EasyTaintWrapperSourceWithoutX.txt")));
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				iFlow.setTaintWrapper(taintWrapper);
			}
		}
		if (config == null) {
			iFlow.setSootConfig(new DefaultSummaryConfig());
		} else {
			iFlow.setSootConfig(config);
		}
		iFlow.setStopAfterFirstFlow(false);
		return iFlow;

	}

	public void setPath(String p) {
		path = p;
	}

	protected void initDefPath() {
		File f = new File(".");
		try {
			path = System.getProperty("java.home") + File.separator + "lib" + File.separator + "rt.jar"
					+ System.getProperty("path.separator") + f.getCanonicalPath() + File.separator + "bin"
					+ System.getProperty("path.separator") + f.getCanonicalPath() + File.separator + "lib";
		} catch (IOException e) {
			e.printStackTrace();
			path = System.getProperty("java.home") + File.separator + "lib" + File.separator + "rt.jar";
		}
	}

	public void setUseTainWrapper(boolean useTainWrapper) {
		this.useTainWrapper = useTainWrapper;
	}

	public void setTaintWrapper(ITaintPropagationWrapper taintWrapper) {
		this.taintWrapper = taintWrapper;
	}

	public void setConfig(IInfoflowConfig config) {
		this.config = config;
	}

	

	private SummarySourceSinkManager createSourceSinkManger(String m) {
		return new SummarySourceSinkManager(m);
	}

	public Map<String, Set<AbstractMethodFlow>> getSummary() {
		return summeries;
	}

	public List<String> getSubstitutedWith() {
		return substitutedWith;
	}

	public void setSubstitutedWith(List<String> substitutedWith) {
		this.substitutedWith = substitutedWith;
	}

	public int getAccessPathLength() {
		return accessPathLength;
	}

	public void setAccessPathLength(int accessPathLength) {
		this.accessPathLength = accessPathLength;
	}

	public boolean isEnableImplicitFlows() {
		return enableImplicitFlows;
	}

	public void setEnableImplicitFlows(boolean enableImplicitFlows) {
		this.enableImplicitFlows = enableImplicitFlows;
	}

	public boolean isEnableExceptionTracking() {
		return enableExceptionTracking;
	}

	public void setEnableExceptionTracking(boolean enableExceptionTracking) {
		this.enableExceptionTracking = enableExceptionTracking;
	}

	public boolean isEnableStaticFieldTracking() {
		return enableStaticFieldTracking;
	}

	public void setEnableStaticFieldTracking(boolean enableStaticFieldTracking) {
		this.enableStaticFieldTracking = enableStaticFieldTracking;
	}

	public boolean isFlowSensitiveAliasing() {
		return flowSensitiveAliasing;
	}

	public void setFlowSensitiveAliasing(boolean flowSensitiveAliasing) {
		this.flowSensitiveAliasing = flowSensitiveAliasing;
	}

	public boolean isIgnoreExceptions() {
		return ignoreExceptions;
	}

	public void setIgnoreExceptions(boolean ignoreExceptions) {
		this.ignoreExceptions = ignoreExceptions;
	}
	
}
