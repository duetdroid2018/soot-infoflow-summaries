package soot.jimple.infoflow.test.methodSummary.junit;

import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

import soot.jimple.infoflow.methodSummary.SummaryGenerator;



public class ApiTestNotWorking extends ApiTestHelper{

	@Override
	Class<?> getClazz() {
		return null;
	}
	
	@Test
	public void doNothing(){
		
	}
	@Override
	SummaryGenerator getSummary() {
		SummaryGenerator sg = new SummaryGenerator();
		List<String> sub = new LinkedList<String>();
		sub.add("java.util.ArrayList");
		sg.setSubstitutedWith(sub);
		sg.setUseRecursiveAccessPaths(false);
		sg.setAnalyseMethodsTogether(false);
		sg.setAccessPathLength(3);
		sg.setIgnoreFlowsInSystemPackages(false);
		return sg;
	}

}
