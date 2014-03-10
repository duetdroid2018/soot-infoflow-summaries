package soot.jimple.infoflow.methodSummary;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import soot.SootMethod;
import soot.Unit;
import soot.jimple.infoflow.Infoflow;
import soot.jimple.infoflow.InfoflowResults;
import soot.jimple.infoflow.config.IInfoflowConfig;
import soot.jimple.infoflow.entryPointCreators.BaseEntryPointCreator;
import soot.jimple.infoflow.entryPointCreators.DefaultEntryPointCreator;
import soot.jimple.infoflow.handlers.ResultsAvailableHandler;
import soot.jimple.infoflow.methodSummary.data.MethodSummaries;
import soot.jimple.infoflow.methodSummary.util.InfoflowResultProcessor;
import soot.jimple.infoflow.methodSummary.util.SummaryTaintPropagationHandler;
import soot.jimple.infoflow.taintWrappers.ITaintPropagationWrapper;
import soot.jimple.toolkits.ide.icfg.BiDiInterproceduralCFG;

/**
 * Class for generating library summaries
 * 
 * @author Malte Viering
 * @author Steven Arzt
 */
public class SummaryGenerator {
	
	protected int accessPathLength = 3;
	protected boolean enableImplicitFlows = false;
	protected boolean enableExceptionTracking = false;
	protected boolean enableStaticFieldTracking = false;
	protected boolean flowSensitiveAliasing = false;
	protected boolean debug = false;
	protected ITaintPropagationWrapper taintWrapper;
	protected IInfoflowConfig config;
	protected String path;
	protected List<String> substitutedWith = new LinkedList<String>();
	
	public SummaryGenerator() {
		substitutedWith.add("java.util.LinkedList");
		substitutedWith.add("java.util.HashMap");
		//substitutedWith.add("java.util.TreeMap");
		initDefPath();
	}

	public MethodSummaries createMethodSummary(final String m) {
		return createMethodSummary(m, new SummarySourceSinkManager(m));
	}
	
	public MethodSummaries createMethodSummary(final String sig, final SummarySourceSinkManager manager) {
		final MethodSummaries summaries = new MethodSummaries();
		
		Infoflow infoflow = initInfoflow();
		final SummaryTaintPropagationHandler listener = new SummaryTaintPropagationHandler(sig);
		infoflow.addTaintPropagationHandler(listener);
		infoflow.addResultsAvailableHandler(new ResultsAvailableHandler() {

			@Override
			public void onResultsAvailable(BiDiInterproceduralCFG<Unit, SootMethod> cfg, InfoflowResults results) {
				InfoflowResultProcessor processor = new InfoflowResultProcessor
						(listener.getResult(), cfg, sig, manager);
				summaries.merge(processor.process());
			}
			
		});
		infoflow.computeInfoflow(null, path, createEntryPoint(), Collections.singletonList(sig), manager);
		return summaries;
	}
	
	private BaseEntryPointCreator createEntryPoint(){
		DefaultEntryPointCreator dEntryPointCreater = new DefaultEntryPointCreator();
		dEntryPointCreater.setSubstituteClasses(substitutedWith);
		dEntryPointCreater.setSubstituteCallParams(true);
		return dEntryPointCreater;
	}

	protected Infoflow initInfoflow() {
		Infoflow iFlow = new Infoflow();
		Infoflow.setAccessPathLength(accessPathLength);
				
		iFlow.setEnableImplicitFlows(enableImplicitFlows);
		iFlow.setEnableExceptionTracking(enableExceptionTracking);
		iFlow.setEnableStaticFieldTracking(enableStaticFieldTracking);
		iFlow.setFlowSensitiveAliasing(flowSensitiveAliasing);
		iFlow.setTaintWrapper(taintWrapper);

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
//			path = System.getProperty("java.home") + File.separator + "lib" + File.separator + "rt.jar"
			path = "D:\\Temp\\odex-phone\\android-phone.jar"
					+ System.getProperty("path.separator") + f.getCanonicalPath() + File.separator + "bin"
					+ System.getProperty("path.separator") + f.getCanonicalPath() + File.separator + "lib";
		} catch (IOException e) {
			e.printStackTrace();
			path = System.getProperty("java.home") + File.separator + "lib" + File.separator + "rt.jar";
		}
	}
	
	public void setTaintWrapper(ITaintPropagationWrapper taintWrapper) {
		this.taintWrapper = taintWrapper;
	}

	public void setConfig(IInfoflowConfig config) {
		this.config = config;
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
	
}
