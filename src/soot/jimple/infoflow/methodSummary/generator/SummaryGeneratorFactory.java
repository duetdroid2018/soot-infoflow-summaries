package soot.jimple.infoflow.methodSummary.generator;

import java.io.IOException;

import soot.jimple.infoflow.InfoflowConfiguration;
import soot.jimple.infoflow.taintWrappers.EasyTaintWrapper;

public class SummaryGeneratorFactory {

	/**
	 * summary generator settings
	 */
	private final int accessPathLength = 4;
	private final boolean enableImplicitFlows = false;
	private final boolean enableExceptionTracking = true;
	private final boolean flowSensitiveAliasing = true;
	private final boolean useRecursiveAccessPaths = true;
	private final boolean loadFullJAR = true;
	private final boolean useTaintWrapper = false;
	
	/**
	 * Initializes the summary generator object
	 * @return The initialized summary generator object
	 */
	public SummaryGenerator initSummaryGenerator() {
		SummaryGenerator s = new SummaryGenerator();
		InfoflowConfiguration.setAccessPathLength(accessPathLength);
		InfoflowConfiguration.setUseRecursiveAccessPaths(useRecursiveAccessPaths);
		s.getConfig().setEnableExceptionTracking(enableExceptionTracking);
		s.getConfig().setEnableImplicitFlows(enableImplicitFlows);
		s.getConfig().setFlowSensitiveAliasing(flowSensitiveAliasing);
		s.getConfig().setLoadFullJAR(loadFullJAR);
		
		if(useTaintWrapper)
			try {
				s.setTaintWrapper(new EasyTaintWrapper("EasyTaintWrapperSourceComplet.txt"));
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		
		return s;
	}
	
}
