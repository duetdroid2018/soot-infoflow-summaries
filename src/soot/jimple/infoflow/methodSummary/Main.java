package soot.jimple.infoflow.methodSummary;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Stack;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.DelayQueue;

import javax.xml.stream.XMLStreamException;

import soot.jimple.infoflow.methodSummary.data.summary.MethodSummaries;
import soot.jimple.infoflow.methodSummary.generator.SummaryGenerator;
import soot.jimple.infoflow.methodSummary.util.ClassFileInformation;
import soot.jimple.infoflow.methodSummary.util.HandleException;
import soot.jimple.infoflow.methodSummary.xml.ISummaryWriter;
import soot.jimple.infoflow.methodSummary.xml.WriterFactory;
import soot.jimple.infoflow.test.methodSummary.ApiClass;

class Main {

	/**
	 * general summary settings
	 */
	private final Class<?>[] classesForSummary = {ApiClass.class}; 
		
		/*{ HashMap.class, TreeSet.class, ArrayList.class, Stack.class, Vector.class, LinkedList.class,
			LinkedHashMap.class, ConcurrentLinkedQueue.class, PriorityQueue.class, ArrayBlockingQueue.class, ArrayDeque.class,
			ConcurrentSkipListMap.class, DelayQueue.class, TreeMap.class, ConcurrentHashMap.class,StringBuilder.class, RuntimeException.class };
*/
	private final boolean overrideExistingFiles = true;
	//if filter is set => only methods that have a sig which matches a filter string are analyzed
	private final String[] filter = {};

	private final boolean continueOnError = true;

	private final String folder = "jdkSummaries";

	/**
	 * summary generator settings
	 */
	private final int accessPathLength = 5;
	private final int summaryAPLength = 4;
	private final boolean ignoreFlowsInSystemPackages = false;
	private final boolean enableImplicitFlows = false;
	private final boolean enableExceptionTracking = false;
	private final boolean flowSensitiveAliasing = false;
	private final boolean useRecursiveAccessPaths = false;
	private final boolean analyseMethodsTogether = true;

	public static void main(String[] args) throws FileNotFoundException, XMLStreamException {
		Main main = new Main();
		main.createSummaries();
	}

	public void createSummaries() {

		for (Class<?> c : classesForSummary) {
			createSummaryForClass(c);
		}

	}

	@SuppressWarnings("unused")
	private void createSummaryForClass(Class<?> clz) {
		long beforeSummary = System.nanoTime();
		System.out.println("create methods summaries for: " + clz + " output to: " + folder);
		List<String> sigs = ClassFileInformation.getMethodSignatures(clz, false);
		String file = classToFile(clz);
		SummaryGenerator s = init();
		MethodSummaries flows = new MethodSummaries();
		File f = new File(folder + File.separator + file);

		if (f.exists() && !overrideExistingFiles) {
			System.out.println("summary for " + clz + " exists => skipped");
			return;
		}
		for (String m : sigs) {
			if (filterInclude(m)) {
				printStartSummary(m);
				try {
					flows.merge(s.createMethodSummary(m, sigs));
				} catch (RuntimeException e) {
					HandleException.handleException(flows, file, folder, e, "createSummary in class: " + clz + " method: " + m);
					if (!continueOnError)
						throw e;
				}
				printEndSummary(m);
			} else {
				System.out.println("Skipped: " + m.toString());
			}
		}
		write(flows, file, folder);
		System.out.println("Methods summaries for: " + clz + " created in " + (System.nanoTime() - beforeSummary) / 1E9 + " seconds");
	}

	private SummaryGenerator init() {
		SummaryGenerator s = new SummaryGenerator();
		s.setAccessPathLength(accessPathLength);
		s.setSummaryAPLength(summaryAPLength);
		s.setIgnoreFlowsInSystemPackages(ignoreFlowsInSystemPackages);
		s.setEnableExceptionTracking(enableExceptionTracking);
		s.setEnableImplicitFlows(enableImplicitFlows);
		s.setFlowSensitiveAliasing(flowSensitiveAliasing);
		s.setUseRecursiveAccessPaths(useRecursiveAccessPaths);
		s.setAnalyseMethodsTogether(analyseMethodsTogether);
		return s;
	}

	private String classToFile(Class<?> c) {
		return c.getName() + ".xml";
	}

	private boolean filterInclude(String m) {
		if (filter == null || filter.length == 0)
			return true;

		for (String s : filter) {
			if (m.contains(s))
				return true;
		}
		return false;
	}

	private void printStartSummary(String m) {
		System.out.println("##############################################################");
		System.out.println("start summary for: " + m);
		System.out.println("##############################################################");
	}

	private void printEndSummary(String m) {
		System.out.println("##############################################################");
		System.out.println("finish summary for: " + m);
		System.out.println("##############################################################");
	}

	private void write(MethodSummaries flows, String fileName, String folder) {
		ISummaryWriter writer = WriterFactory.createXMLWriter(fileName, folder);
		try {
			writer.write(flows);
		} catch (XMLStreamException e) {
			e.printStackTrace();
			if(!continueOnError)
				throw new RuntimeException(e);
		}
	}

}
