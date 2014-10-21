package soot.jimple.infoflow.methodSummary.generator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import soot.G;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.jimple.infoflow.IInfoflow.CallgraphAlgorithm;
import soot.jimple.infoflow.BiDirICFGFactory;
import soot.jimple.infoflow.Infoflow;
import soot.jimple.infoflow.InfoflowResults;
import soot.jimple.infoflow.config.IInfoflowConfig;
import soot.jimple.infoflow.data.pathBuilders.DefaultPathBuilderFactory;
import soot.jimple.infoflow.data.pathBuilders.DefaultPathBuilderFactory.PathBuilder;
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
import soot.options.Options;

/**
 * Class for generating library summaries
 * 
 * @author Malte Viering
 * @author Steven Arzt
 */
public class SummaryGenerator {

	public static final String DUMMY_MAIN_SIG = "<dummyMainClass: void dummyMainMethod()>";

	//the access path length that is used in infoflow
	protected int accessPathLength = 1; // 5;

	//the access path length that is used in the summaries. 
	protected int summaryAPLength = accessPathLength - 1;

	protected boolean ignoreFlowsInSystemPackages = false;
	protected boolean enableImplicitFlows = false;
	protected boolean enableExceptionTracking = false;
	protected boolean enableStaticFieldTracking = false;
	protected boolean flowSensitiveAliasing = false;
	protected boolean useRecursiveAccessPaths = false;
	protected boolean forceTaintSubFields = false;

	protected CallgraphAlgorithm cfgAlgo = CallgraphAlgorithm.SPARK;
	protected boolean debug = false;
	protected ITaintPropagationWrapper taintWrapper;
	protected IInfoflowConfig config;
	protected List<String> substitutedWith = new LinkedList<String>();
	private boolean analyseMethodsTogether = true;

	public SummaryGenerator() {
	}
	
	/**
	 * Generates the summaries for the given set of classes
	 * @param classpath The classpath from which to load the given classes
	 * @param classNames The classes for which to create summaries
	 * @return The generated method summaries
	 */
	public MethodSummaries createMethodSummaries(String classpath,
			Collection<String> classNames) {
		return createMethodSummaries(classpath, classNames, null);
	}
	
	/**
	 * Generates the summaries for the given set of classes
	 * @param classpath The classpath from which to load the given classes
	 * @param classNames The classes for which to create summaries
	 * @param handler The handler that shall be invoked when all methods inside
	 * one class have been summarized
	 * @return The generated method summaries
	 */
	public MethodSummaries createMethodSummaries(String classpath,
			Collection<String> classNames, IClassSummaryHandler handler) {
		G.reset();
		
		Options.v().set_src_prec(Options.src_prec_class);
		Options.v().set_output_format(Options.output_format_none);
		Options.v().set_soot_classpath(classpath);
		Options.v().set_whole_program(false);
		Options.v().set_allow_phantom_refs(true);
		
		for (String className : classNames)
			Scene.v().addBasicClass(className, SootClass.SIGNATURES);
		Scene.v().loadNecessaryClasses();
		
		// Collect all the public methods in the given classes. We cannot
		// directly start the summary generation as this resets Soot.
		Map<String, Collection<String>> methodsToAnalyze = new HashMap<>();
		for (String className : classNames) {
			Collection<String> methods = new ArrayList<>();
			methodsToAnalyze.put(className, methods);

			SootClass sc = Scene.v().getSootClass(className);
			for (SootMethod sm : sc.getMethods())
				methods.add(sm.getSignature());
		}
		
		// Do the actual analysis
		MethodSummaries summaries = new MethodSummaries();
		for (Entry<String, Collection<String>> entry : methodsToAnalyze.entrySet()) {
			MethodSummaries classSummaries = new MethodSummaries();
			for (String methodSig : entry.getValue()) {
				
				if (!methodSig.contains("addAll(int"))
					continue;
				
				MethodSummaries newSums = createMethodSummary(classpath, methodSig);
				if (handler != null)
					handler.onMethodFinished(methodSig, classSummaries);
				classSummaries.merge(newSums);
			}
			
			if (handler != null)
				handler.onClassFinished(entry.getKey(), classSummaries);
			summaries.merge(classSummaries);
		}
		return summaries;
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
	 * @param classpath
	 * 			  The classpath containing the classes to summarize
	 * @param methodSig
	 *            method for which a summary will be created
	 * @return summary of method m
	 */
	public MethodSummaries createMethodSummary(String classpath, String methodSig) {
		return createMethodSummary(classpath, methodSig,  Collections.<String>emptyList());
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
	 * @param classpath
	 * 			  The classpath containing the classes to summarize
	 * @param methodSig
	 *            method for which a summary will be created
	 * @param mDependencies
	 *            all methods which will be "executed" before m
	 * @return summary of method m
	 */
	public MethodSummaries createMethodSummary(String classpath,
			final String methodSig, List<String> mDependencies) {
		
		final SummarySourceSinkManager manager = new SummarySourceSinkManager(methodSig,
				summaryAPLength,forceTaintSubFields);
		final MethodSummaries summaries = new MethodSummaries();
		final Infoflow infoflow = initInfoflow();
		
		final SummaryTaintPropagationHandler listener = new SummaryTaintPropagationHandler(methodSig);
		infoflow.addTaintPropagationHandler(listener);
		
		infoflow.addResultsAvailableHandler(new ResultsAvailableHandler() {
			@Override
			public void onResultsAvailable(IInfoflowCFG cfg, InfoflowResults results) {
				InfoflowResultPostProcessor processor = new InfoflowResultPostProcessor(listener.getResult(),
						cfg, methodSig, summaryAPLength);
				summaries.merge(processor.postProcess());
			}
		});
		
		List<String> ms = new LinkedList<String>();
		ms.add(methodSig);
		if (analyseMethodsTogether) {
			addDependentMethods(methodSig, ms, mDependencies);
		}
		infoflow.computeInfoflow(null, classpath, createEntryPoint(ms), manager);
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
	
	private BaseEntryPointCreator createEntryPoint(Collection<String> entryPoints) {
		DefaultEntryPointCreator dEntryPointCreater = new DefaultEntryPointCreator(entryPoints);
		dEntryPointCreater.setSubstituteClasses(substitutedWith);
		dEntryPointCreater.setSubstituteCallParams(true);
		return dEntryPointCreater;
	}

	protected Infoflow initInfoflow() {
		Infoflow iFlow = new Infoflow("", false, new BiDirICFGFactory() {
			
			@Override
			public IInfoflowCFG buildBiDirICFG(CallgraphAlgorithm callgraphAlgorithm) {
				// We encapsulate the ICFG to take care of situations in which the
				// original callgraph algorithm does not find any callees.
				return new SummaryCFG();
			}
			
		}, new DefaultPathBuilderFactory(PathBuilder.None, false));
		Infoflow.setAccessPathLength(accessPathLength);

		iFlow.setEnableImplicitFlows(enableImplicitFlows);
		iFlow.setEnableExceptionTracking(enableExceptionTracking);
		iFlow.setEnableStaticFieldTracking(enableStaticFieldTracking);
		iFlow.setFlowSensitiveAliasing(flowSensitiveAliasing);
		iFlow.setTaintWrapper(taintWrapper);
		iFlow.setCallgraphAlgorithm(cfgAlgo);
//		iFlow.setMethodsExcludedFromFlowPropagation(java.util.Collections.singletonList(DUMMY_MAIN_SIG));
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

	public boolean isForceTaintSubFields() {
		return forceTaintSubFields;
	}

	public void setForceTaintSubFields(boolean forceTaintSubFields) {
		this.forceTaintSubFields = forceTaintSubFields;
	}
	

}
