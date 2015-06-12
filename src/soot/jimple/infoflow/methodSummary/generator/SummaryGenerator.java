package soot.jimple.infoflow.methodSummary.generator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import soot.G;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.jimple.Stmt;
import soot.jimple.infoflow.IInfoflow.CallgraphAlgorithm;
import soot.jimple.infoflow.Infoflow;
import soot.jimple.infoflow.InfoflowManager;
import soot.jimple.infoflow.cfg.DefaultBiDiICFGFactory;
import soot.jimple.infoflow.config.IInfoflowConfig;
import soot.jimple.infoflow.data.Abstraction;
import soot.jimple.infoflow.data.pathBuilders.DefaultPathBuilderFactory;
import soot.jimple.infoflow.data.pathBuilders.DefaultPathBuilderFactory.PathBuilder;
import soot.jimple.infoflow.entryPointCreators.BaseEntryPointCreator;
import soot.jimple.infoflow.entryPointCreators.SequentialEntryPointCreator;
import soot.jimple.infoflow.handlers.ResultsAvailableHandler;
import soot.jimple.infoflow.methodSummary.DefaultSummaryConfig;
import soot.jimple.infoflow.methodSummary.data.GapDefinition;
import soot.jimple.infoflow.methodSummary.data.MethodFlow;
import soot.jimple.infoflow.methodSummary.data.factory.SourceSinkFactory;
import soot.jimple.infoflow.methodSummary.data.summary.MethodSummaries;
import soot.jimple.infoflow.methodSummary.handler.SummaryTaintPropagationHandler;
import soot.jimple.infoflow.methodSummary.postProcessor.InfoflowResultPostProcessor;
import soot.jimple.infoflow.methodSummary.source.SummarySourceSinkManager;
import soot.jimple.infoflow.results.InfoflowResults;
import soot.jimple.infoflow.solver.cfg.IInfoflowCFG;
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

	// the access path length that is used in infoflow
	protected int accessPathLength = 5;

	// the access path length that is used in the summaries.
	protected int summaryAPLength = accessPathLength - 1;

	protected boolean ignoreFlowsInSystemPackages = false;
	protected boolean enableImplicitFlows = false;
	protected boolean enableExceptionTracking = false;
	protected boolean enableStaticFieldTracking = false;
	protected boolean flowSensitiveAliasing = true;
	protected boolean useRecursiveAccessPaths = true;

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
	 * 
	 * @param classpath
	 *            The classpath from which to load the given classes
	 * @param classNames
	 *            The classes for which to create summaries
	 * @return The generated method summaries
	 */
	public MethodSummaries createMethodSummaries(String classpath,
			Collection<String> classNames) {
		return createMethodSummaries(classpath, classNames, null);
	}

	/**
	 * Generates the summaries for the given set of classes
	 * 
	 * @param classpath
	 *            The classpath from which to load the given classes
	 * @param classNames
	 *            The classes for which to create summaries
	 * @param handler
	 *            The handler that shall be invoked when all methods inside one
	 *            class have been summarized
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
				if (sm.isPublic() || sm.isProtected())
					methods.add(sm.getSignature());
			
			// We also need to analyze methods of parent classes
			SootClass curClass = sc;
			while (curClass.hasSuperclass()) {
				curClass = curClass.getSuperclass();
				if (!curClass.isAbstract())
					break;
				
				for (SootMethod sm : curClass.getMethods())
					if (sm.isPublic() || sm.isProtected())
						methods.add(sm.getSignature());
			}
		}
		
		// Make sure that we don't have any strange leftovers
		G.reset();
		
		// We share one gap manager across all method analyses
		final GapManager gapManager = new GapManager();

		// Do the actual analysis
		MethodSummaries summaries = new MethodSummaries();
		for (Entry<String, Collection<String>> entry : methodsToAnalyze
				.entrySet()) {
			MethodSummaries classSummaries = new MethodSummaries();
			for (String methodSig : entry.getValue()) {
				MethodSummaries newSums = createMethodSummary(classpath,
						methodSig, entry.getKey(), gapManager);
				if (handler != null)
					handler.onMethodFinished(methodSig, classSummaries);
				classSummaries.merge(newSums);
			}
			
			// Clean up the gaps
			cleanupGaps(classSummaries);
			
			if (handler != null)
				handler.onClassFinished(entry.getKey(), classSummaries);
			summaries.merge(classSummaries);
		}
		
		// Calculate the dependencies
		calculateDependencies(summaries);
		
		return summaries;	
	}
	
	/**
	 * Removes all gaps with no flows in and out from the given method summary
	 * object
	 * @param summaries The summary object from which to remove the unused gaps
	 */
	private void cleanupGaps(MethodSummaries summaries) {
		Set<GapDefinition> gaps = new HashSet<GapDefinition>(summaries.getAllGaps());
		for (GapDefinition gd : gaps) {
			boolean gapIsUsed = false;
			for (MethodFlow flow : summaries.getAllFlows())
				if (flow.source().getGap() == gd || flow.sink().getGap() == gd) {
					gapIsUsed = true;
					break;
				}
			if (!gapIsUsed)
				summaries.removeGap(gd);
		}
	}

	/**
	 * Calculates the external dependencies of the given summary set
	 * @param summaries The summary set for which to calculate the
	 * dependencies
	 */
	private void calculateDependencies(MethodSummaries summaries) {
		for (MethodFlow flow : summaries) {
			if (flow.source().hasAccessPath())
				for (String className : flow.source().getAccessPath())
					checkAndAddDependency(summaries, className);
			if (flow.sink().hasAccessPath())
				for (String className : flow.sink().getAccessPath())
					checkAndAddDependency(summaries, className);
		}
	}
	
	/**
	 * Checks whether we don't have any summaries for the class of the given
	 * method. If so, a dependency on that class is added.
	 * @param summaries The method summaries to which to add the dependency
	 * @param methodSignature The signature of a method in a possible dependency
	 * class
	 */
	private void checkAndAddDependency(MethodSummaries summaries,
			String methodSignature) {
		//Check that we don't have summaries for the given class
		final String className = Scene.v().signatureToClass(methodSignature);
		for (MethodFlow flow : summaries)
			if (Scene.v().signatureToClass(flow.methodSig()).equals(className))
				return;
		
		summaries.addDependency(className);
	}

	/**
	 * Creates a method summary for the method m
	 * 
	 * It is assumed that only the default constructor of c is executed before m
	 * is called.
	 * 
	 * The result of that assumption is that some fields of c may be null. A
	 * null field is not identified as a source and there for will not create a
	 * Field -> X flow.
	 * 
	 * @param classpath
	 *            The classpath containing the classes to summarize
	 * @param methodSig
	 *            method for which a summary will be created
	 * @return summary of method m
	 */
	public MethodSummaries createMethodSummary(String classpath, String methodSig) {
		return createMethodSummary(classpath, methodSig,
				"",
				Collections.<String> emptyList(),
				new GapManager());
	}
	
	/**
	 * Creates a method summary for the method m
	 * 
	 * It is assumed that only the default constructor of c is executed before m
	 * is called.
	 * 
	 * The result of that assumption is that some fields of c may be null. A
	 * null field is not identified as a source and there for will not create a
	 * Field -> X flow.
	 * 
	 * @param classpath
	 *            The classpath containing the classes to summarize
	 * @param methodSig
	 *            method for which a summary will be created
	 * @param parentClass
	 * 			  The parent class on which the method to be analyzed shall be
	 * 			  invoked
	 * @param gapManager
	 * 			  The gap manager to be used for creating new gaps 
	 * @return summary of method m
	 */
	private MethodSummaries createMethodSummary(String classpath,
			String methodSig, String parentClass, GapManager gapManager) {
		return createMethodSummary(classpath, methodSig,
				parentClass,
				Collections.<String> emptyList(),
				gapManager);
	}

	/**
	 * Creates a method summary for the method m.
	 * 
	 * It is assumed that all method in mDependencies and the default
	 * constructor of c is executed before m is executed.
	 * 
	 * That allows e.g. to call a setter before a getter method is analyzed and
	 * there for the getter field is not null.
	 * 
	 * @param classpath
	 *            The classpath containing the classes to summarize
	 * @param methodSig
	 *            method for which a summary will be created
	 * @param parentClass
	 * 			  The parent class on which the method to be analyzed shall be
	 * 			  invoked
	 * @param mDependencies
	 *            all methods which will be "executed" before m
	 * @param gapManager
	 * 			  The gap manager to be used for creating new gaps 
	 * @return summary of method m
	 */
	private MethodSummaries createMethodSummary(String classpath,
			final String methodSig, final String parentClass,
			List<String> mDependencies, final GapManager gapManager) {
		System.out.println("Computing method summary for " + methodSig);
		
		final SourceSinkFactory sourceSinkFactory = new SourceSinkFactory(
				summaryAPLength);
		final SummarySourceSinkManager manager = new SummarySourceSinkManager(
				methodSig, parentClass, sourceSinkFactory);
		final MethodSummaries summaries = new MethodSummaries();
		
		final Infoflow infoflow = initInfoflow(summaries, gapManager);
		
		final SummaryTaintPropagationHandler listener = new SummaryTaintPropagationHandler(
				methodSig, parentClass, Collections.singleton(DUMMY_MAIN_SIG),
				gapManager);
		infoflow.addTaintPropagationHandler(listener);

		infoflow.addResultsAvailableHandler(new ResultsAvailableHandler() {
			@Override
			public void onResultsAvailable(IInfoflowCFG cfg,
					InfoflowResults results) {
				InfoflowResultPostProcessor processor = new InfoflowResultPostProcessor(
						listener.getResult(), cfg, methodSig, sourceSinkFactory, gapManager);
				processor.postProcess(summaries);
			}
		});

		List<String> ms = new LinkedList<String>();
		ms.add(methodSig);
		if (analyseMethodsTogether) {
			addDependentMethods(methodSig, ms, mDependencies);
		}
		try {
			infoflow.computeInfoflow(null, classpath, createEntryPoint(ms, parentClass), manager);
		}
		catch (Exception e) {
			System.err.println("Could not generate summary for method " + ms);
			e.printStackTrace();
			throw e;
		}
		return summaries;
	}

	private void addDependentMethods(String sig, List<String> methods,
			List<String> mDependencies) {
		if (mDependencies != null) {
			for (String s : mDependencies) {
				if (!s.equals(sig))
					methods.add(s);
			}
		}
	}

	private BaseEntryPointCreator createEntryPoint(
			Collection<String> entryPoints, String parentClass) {
		SequentialEntryPointCreator dEntryPointCreater = new SequentialEntryPointCreator(
				entryPoints);
		
		List<String> substClasses = new ArrayList<String>(substitutedWith);
		if (parentClass != null && !parentClass.isEmpty())
			substClasses.add(parentClass);
		dEntryPointCreater.setSubstituteClasses(substClasses);
		dEntryPointCreater.setSubstituteCallParams(true);
		
		return dEntryPointCreater;
	}
	
	/**
	 * Initializes the data flow tracker
	 * @param summaries The summary data object to receive the flows
	 * @param gapManager The gap manager to be used when handling callbacks
	 * @return The initialized data flow engine
	 */
	protected Infoflow initInfoflow(MethodSummaries summaries, GapManager gapManager) {
		// Disable the default path reconstruction. However, still make sure to
		// retain the contents of the callees.
		Infoflow iFlow = new Infoflow("", false, new DefaultBiDiICFGFactory(),
				new DefaultPathBuilderFactory(PathBuilder.None, false) {

					@Override
					public boolean supportsPathReconstruction() {
						return true;
					}

				});
		Infoflow.setAccessPathLength(accessPathLength);
		Infoflow.setMergeNeighbors(true);
		
		iFlow.setEnableImplicitFlows(enableImplicitFlows);
		iFlow.setEnableExceptionTracking(enableExceptionTracking);
		iFlow.setEnableStaticFieldTracking(enableStaticFieldTracking);
		iFlow.setFlowSensitiveAliasing(flowSensitiveAliasing);
		
		final SummaryGenerationTaintWrapper summaryWrapper =
				new SummaryGenerationTaintWrapper(summaries, gapManager);
		if (taintWrapper == null)
			iFlow.setTaintWrapper(summaryWrapper);
		else {
			ITaintPropagationWrapper wrapper = new ITaintPropagationWrapper() {
				
				@Override
				public void initialize(InfoflowManager manager) {
					
				}
				
				@Override
				public Set<Abstraction> getTaintsForMethod(Stmt stmt,
						Abstraction d1, Abstraction taintedPath) {
					Set<Abstraction> taints = taintWrapper.getTaintsForMethod(
							stmt, d1, taintedPath);
					if (taints != null && !taints.isEmpty())
						return taints;

					return summaryWrapper.getTaintsForMethod(stmt, d1, taintedPath);
				}

				@Override
				public boolean isExclusive(Stmt stmt,
						Abstraction taintedPath) {
					return taintWrapper.isExclusive(stmt, taintedPath)
							|| summaryWrapper.isExclusive(stmt, taintedPath);
				}

				@Override
				public boolean supportsCallee(SootMethod method) {
					return taintWrapper.supportsCallee(method)
							|| summaryWrapper.supportsCallee(method);
				}

				@Override
				public boolean supportsCallee(Stmt callSite) {
					return taintWrapper.supportsCallee(callSite)
							|| summaryWrapper.supportsCallee(callSite);
				}

				@Override
				public int getWrapperHits() {
					// Statistics are not supported by this taint wrapper
					return -1;
				}

				@Override
				public int getWrapperMisses() {
					// Statistics are not supported by this taint wrapper
					return -1;
				}

				@Override
				public Set<Abstraction> getAliasesForMethod(Stmt stmt,
						Abstraction d1, Abstraction taintedPath) {
					Set<Abstraction> absSet = taintWrapper.getAliasesForMethod(
							stmt, d1, taintedPath);
					if (absSet != null && !absSet.isEmpty())
						return absSet;
					
					return taintWrapper.getAliasesForMethod(stmt, d1, taintedPath);
				}

			};
			iFlow.setTaintWrapper(wrapper);
		}

		iFlow.setCallgraphAlgorithm(cfgAlgo);
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

	public void setIgnoreFlowsInSystemPackages(
			boolean ignoreFlowsInSystemPackages) {
		this.ignoreFlowsInSystemPackages = ignoreFlowsInSystemPackages;
	}

	public void setUseRecursiveAccessPaths(boolean useRecursiveAccessPaths) {
		this.useRecursiveAccessPaths = useRecursiveAccessPaths;
	}

	public void setAnalyseMethodsTogether(boolean analyseMethodsTogether) {
		this.analyseMethodsTogether = analyseMethodsTogether;
	}

}
