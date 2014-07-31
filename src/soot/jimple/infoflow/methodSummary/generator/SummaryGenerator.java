package soot.jimple.infoflow.methodSummary.generator;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import soot.jimple.infoflow.IInfoflow.CallgraphAlgorithm;
import soot.jimple.infoflow.Infoflow;
import soot.jimple.infoflow.InfoflowResults;
import soot.jimple.infoflow.config.IInfoflowConfig;
import soot.jimple.infoflow.entryPointCreators.BaseEntryPointCreator;
import soot.jimple.infoflow.entryPointCreators.DefaultEntryPointCreator;
import soot.jimple.infoflow.handlers.ResultsAvailableHandler;
import soot.jimple.infoflow.methodSummary.DefaultSummaryConfig;
import soot.jimple.infoflow.methodSummary.data.summary.MethodSummaries;
import soot.jimple.infoflow.methodSummary.handler.SummaryTaintPropagationHandler;
import soot.jimple.infoflow.methodSummary.postProcessor.InfoflowResultPostProcessor;
import soot.jimple.infoflow.methodSummary.source.SummarySourceSinkManager;
import soot.jimple.infoflow.solver.IInfoflowCFG;
import soot.jimple.infoflow.taintWrappers.ITaintPropagationWrapper;

/**
 * Class for generating library summaries
 * 
 * @author Malte Viering
 * @author Steven Arzt
 */
public class SummaryGenerator {

	public static final String DUMMY_MAIN_SIG = "<dummyMainClass: void dummyMainMethod()>";

	//the access path length that is used in infoflow
	protected int accessPathLength = 5;

	//the access path length that is used in the summaries. 
	protected int summaryAPLength = accessPathLength - 1;

	protected boolean ignoreFlowsInSystemPackages = false;
	protected boolean enableImplicitFlows = false;
	protected boolean enableExceptionTracking = false;
	protected boolean enableStaticFieldTracking = false;
	protected boolean flowSensitiveAliasing = false;
	protected boolean useRecursiveAccessPaths = false;

	protected CallgraphAlgorithm cfgAlgo = CallgraphAlgorithm.SPARK;
	protected boolean debug = false;
	protected ITaintPropagationWrapper taintWrapper;
	protected IInfoflowConfig config;
	protected String path;
	protected List<String> substitutedWith = new LinkedList<String>();
	private boolean analyseMethodsTogether = true;

	public SummaryGenerator() {
		initDefPath();
	}

	/**
	 * Creates a method summary for the method m
	 * 
	 * It is assumed that only the default constructor of c is executed before
	 * m is called.
	 * 
	 * The result of that assumption is that some fields of c may be null. 
	 * A null field is not identified as a source and there for will not create a Field -> X
	 * flow.
	 * 
	 * @param m
	 *            method for which a summary will be created
	 * @return summary of method m
	 */
	public MethodSummaries createMethodSummary(final String m) {
		return createMethodSummary(m, null, new SummarySourceSinkManager(m, summaryAPLength));
	}

	/**
	 * Creates a method summary for the method m.
	 * 
	 * It is assumed that all method in mDependencies and the default
	 * constructor of c is executed before
	 * m is executed.
	 * 
	 * That allows e.g. to call a setter before a getter method is analyzed and
	 * there for the getter field is not null.
	 * 
	 * @param m
	 *            method for which a summary will be created
	 * @param mDependencies
	 *            all methods which will be "executed" before m
	 * @return summary of method m
	 */
	public MethodSummaries createMethodSummary(final String m, List<String> mDependencies) {
		return createMethodSummary(m, mDependencies, new SummarySourceSinkManager(m, summaryAPLength));
	}

	private MethodSummaries createMethodSummary(final String sig, List<String> mDependencies, final SummarySourceSinkManager manager) {
		final MethodSummaries summaries = new MethodSummaries();

		Infoflow infoflow = initInfoflow();
		final SummaryTaintPropagationHandler listener = new SummaryTaintPropagationHandler(sig);
		infoflow.addTaintPropagationHandler(listener);
		
		infoflow.addResultsAvailableHandler(getResultAvialableHandler(sig,summaries,listener));
		
		List<String> ms = new LinkedList<String>();
		if (analyseMethodsTogether) {
			addDependentMethods(sig, ms, mDependencies);
		}
		ms.add(sig);
		infoflow.computeInfoflow(null, path, createEntryPoint(ms), manager);
		return summaries;
	}
	
	private void addDependentMethods(String sig, List<String> methods, List<String> mDependencies){
		if (mDependencies != null) {
			for (String s : mDependencies) {
				if (!s.equals(sig))
					methods.add(s);
			}
		}
	}
	
	
	
	private ResultsAvailableHandler getResultAvialableHandler(final String sig,final MethodSummaries summaries, final SummaryTaintPropagationHandler listener){
		return new ResultsAvailableHandler() {
			@Override
			public void onResultsAvailable(IInfoflowCFG cfg, InfoflowResults results) {
				InfoflowResultPostProcessor processor = new InfoflowResultPostProcessor(listener.getResult(), cfg, sig, summaryAPLength);
				summaries.merge(processor.postProcess());
			}
		};
	}

	private BaseEntryPointCreator createEntryPoint(Collection<String> entryPoints) {
		DefaultEntryPointCreator dEntryPointCreater = new DefaultEntryPointCreator(entryPoints);
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
		iFlow.setMethodsExcludedFromFlowPropagation(java.util.Collections.singletonList(DUMMY_MAIN_SIG));
		iFlow.setIgnoreFlowsInSystemPackages(ignoreFlowsInSystemPackages);
		Infoflow.setUseRecursiveAccessPaths(useRecursiveAccessPaths);

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
			path = System.getProperty("java.home") + File.separator + "lib" + File.separator + "rt.jar" + pathSep + f.getCanonicalPath()
					+ File.separator + "bin" + pathSep + f.getCanonicalPath() + File.separator + "build" + File.separator + "testclasses" + pathSep
					+ f.getCanonicalPath() + File.separator + "lib";
			path = "D:\\realSDK\\android.jar" +
					  System.getProperty("path.separator") +
					  f.getCanonicalPath() + File.separator + "bin" +
					  System.getProperty("path.separator") +
					  f.getCanonicalPath() + File.separator + "lib";
			
//			  path = "D:\\Temp\\odex-phone\\android-phone.jar" +
//			  System.getProperty("path.separator") +
//			  f.getCanonicalPath() + File.separator + "bin" +
//			 System.getProperty("path.separator") +
//			 f.getCanonicalPath() + File.separator + "lib";
//			
		} catch (IOException e) {
			e.printStackTrace();
			path = System.getProperty("java.home") + File.separator + "lib" + File.separator + "rt.jar";
		}
	}
	
	public String getPath(){
		return path;
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

	public void setSummaryAPLength(int summaryAPLength) {
		this.summaryAPLength = summaryAPLength;
	}

	public void setIgnoreFlowsInSystemPackages(boolean ignoreFlowsInSystemPackages) {
		this.ignoreFlowsInSystemPackages = ignoreFlowsInSystemPackages;
	}

	public void setUseRecursiveAccessPaths(boolean useRecursiveAccessPaths) {
		this.useRecursiveAccessPaths = useRecursiveAccessPaths;
	}

	public void setAnalyseMethodsTogether(boolean analyseMethodsTogether) {
		this.analyseMethodsTogether = analyseMethodsTogether;
	}

}
