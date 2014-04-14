package soot.jimple.infoflow.methodSummary;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import soot.Scene;
import soot.SootMethod;
import soot.Unit;
import soot.jimple.infoflow.IInfoflow.CallgraphAlgorithm;
import soot.jimple.infoflow.Infoflow;
import soot.jimple.infoflow.InfoflowResults;
import soot.jimple.infoflow.config.IInfoflowConfig;
import soot.jimple.infoflow.entryPointCreators.BaseEntryPointCreator;
import soot.jimple.infoflow.entryPointCreators.DefaultEntryPointCreator;
import soot.jimple.infoflow.handlers.ResultsAvailableHandler;
import soot.jimple.infoflow.methodSummary.data.MethodSummaries;
import soot.jimple.infoflow.methodSummary.util.InfoflowResultProcessor;
import soot.jimple.infoflow.methodSummary.util.SummaryTaintPropagationHandler;
import soot.jimple.infoflow.solver.IInfoflowCFG;
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
	protected int summaryAPLength = accessPathLength - 1;
	protected boolean enableImplicitFlows = false;
	protected boolean enableExceptionTracking = false;
	protected boolean enableStaticFieldTracking = false;
	protected boolean flowSensitiveAliasing = false;
	protected CallgraphAlgorithm cfgAlgo = CallgraphAlgorithm.SPARK;
	protected boolean debug = false;
	protected ITaintPropagationWrapper taintWrapper;
	protected IInfoflowConfig config;
	protected String path;
	protected List<String> substitutedWith = new LinkedList<String>();

	public SummaryGenerator() {
		substitutedWith.add("java.util.LinkedList");
		substitutedWith.add("java.util.HashMap");
		// substitutedWith.add("java.util.TreeMap");
		initDefPath();
	}

	public MethodSummaries createMethodSummary(final String m) {
		return createMethodSummary(m, null, new SummarySourceSinkManager(m));
	}

	public MethodSummaries createMethodSummary(final String m, List<String> mDependencies) {
		return createMethodSummary(m, mDependencies, new SummarySourceSinkManager(m));
	}

	private MethodSummaries createMethodSummary(final String sig, List<String> mDependencies,
			final SummarySourceSinkManager manager) {
		final MethodSummaries summaries = new MethodSummaries();

		Infoflow infoflow = initInfoflow();
		final SummaryTaintPropagationHandler listener = new SummaryTaintPropagationHandler(sig);
		infoflow.addTaintPropagationHandler(listener);
		infoflow.addResultsAvailableHandler(new ResultsAvailableHandler() {

			@Override
			public void onResultsAvailable(IInfoflowCFG cfg, InfoflowResults results) {
				InfoflowResultProcessor processor = new InfoflowResultProcessor(listener.getResult(), cfg, sig,
						manager, summaryAPLength);
				summaries.merge(processor.process());

			}

		});
		infoflow.computeInfoflow(null, path, createEntryPoint(), Collections.singletonList(sig), manager);
		return summaries;
	}

	private BaseEntryPointCreator createEntryPoint() {
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
		iFlow.setCallgraphAlgorithm(cfgAlgo);
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
			final String pathSep = System.getProperty("path.separator");
			path = System.getProperty("java.home") + File.separator + "lib" + File.separator + "rt.jar" + pathSep
					+ f.getCanonicalPath() + File.separator + "bin" + pathSep + f.getCanonicalPath() + File.separator
					+ "build" + File.separator + "testclasses" + pathSep + f.getCanonicalPath() + File.separator
					+ "lib";
			/*
			 * path = "D:\\Temp\\odex-phone\\android-phone.jar" + System.getProperty("path.separator") +
			 * f.getCanonicalPath() + File.separator + "bin" + System.getProperty("path.separator") +
			 * f.getCanonicalPath() + File.separator + "lib";
			 */
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
