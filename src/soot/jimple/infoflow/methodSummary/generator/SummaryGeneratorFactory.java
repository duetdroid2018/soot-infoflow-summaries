package soot.jimple.infoflow.methodSummary.generator;

import java.io.IOException;

import soot.jimple.infoflow.taintWrappers.EasyTaintWrapper;

public class SummaryGeneratorFactory {

	/**
	 * summary generator settings
	 */
	private final int accessPathLength =2;
	private final int summaryAPLength = 1;
	private final boolean ignoreFlowsInSystemPackages = false;
	private final boolean enableImplicitFlows = true;
	private final boolean enableExceptionTracking = true;
	private final boolean flowSensitiveAliasing = true;
	private final boolean useRecursiveAccessPaths = false;
	private final boolean analyseMethodsTogether = true;
	private final boolean useTaintWrapper = false;
	private final boolean forceTaintSubFields = false;

	/**
	 * Initializes the summary generator object
	 * @return The initialized summary generator object
	 */
	public SummaryGenerator initSummaryGenerator() {
		SummaryGenerator s = new SummaryGenerator();
		s.setAccessPathLength(accessPathLength);
		s.setSummaryAPLength(summaryAPLength);
		s.setIgnoreFlowsInSystemPackages(ignoreFlowsInSystemPackages);
		s.setEnableExceptionTracking(enableExceptionTracking);
		s.setEnableImplicitFlows(enableImplicitFlows);
		s.setFlowSensitiveAliasing(flowSensitiveAliasing);
		s.setUseRecursiveAccessPaths(useRecursiveAccessPaths);
		s.setAnalyseMethodsTogether(analyseMethodsTogether);
		s.setForceTaintSubFields(forceTaintSubFields);
		
		if(useTaintWrapper)
			try {
				s.setTaintWrapper(new EasyTaintWrapper("EasyTaintWrapperSourceComplet.txt"));
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		
		return s;
	}
	
}
