package soot.jimple.infoflow.methodSummary;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.stream.XMLStreamException;

import soot.jimple.infoflow.methodSummary.data.FlowSink;
import soot.jimple.infoflow.methodSummary.data.FlowSource;
import soot.jimple.infoflow.methodSummary.data.MethodFlow;
import soot.jimple.infoflow.methodSummary.data.factory.SourceSinkFactory;
import soot.jimple.infoflow.methodSummary.data.impl.DefaultMethodFlow;
import soot.jimple.infoflow.methodSummary.data.summary.MethodSummaries;
import soot.jimple.infoflow.methodSummary.generator.SummaryGenerator;
import soot.jimple.infoflow.methodSummary.util.ClassFileInformation;
import soot.jimple.infoflow.methodSummary.util.HandleException;
import soot.jimple.infoflow.methodSummary.xml.ISummaryWriter;
import soot.jimple.infoflow.methodSummary.xml.WriterFactory;
import soot.jimple.infoflow.taintWrappers.EasyTaintWrapper;

class Main {

	/**
	 * general summary settings
	 */
	private final Class<?>[] classesForSummary = {Exception.class, Throwable.class/*Double.class,Integer.class/*String.class/*, ContextWrapper.class,Bundle.class,StringEntity.class ,Arrays.class,StringBuilder.class Integer.class*/};
	/*{ HashMap.class, TreeSet.class, ArrayList.class, Stack.class, Vector.class, LinkedList.class,
			LinkedHashMap.class, ConcurrentLinkedQueue.class, PriorityQueue.class, ArrayBlockingQueue.class, ArrayDeque.class,
			ConcurrentSkipListMap.class, DelayQueue.class, TreeMap.class, ConcurrentHashMap.class,StringBuilder.class, RuntimeException.class };*/

	private final boolean overrideExistingFiles = true;
	//if filter is set => only methods that have a sig which matches a filter string are analyzed
	private final String[] filter = {""};

	private final boolean continueOnError = true;

	private final String folder = "";
	
	final List<String> failedMethos = new LinkedList<>();

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
	
	private final List<String> subWith = java.util.Collections.singletonList("java.util.ArrayList");

	public static void main(String[] args) throws FileNotFoundException, XMLStreamException {
		Main main = new Main();	
		//Scene.v().getMethod("<android.app.Activity: android.app.Activity getParent()>");
		main.createSummaries();
		System.out.println("failed Methods:");
		for(String m : main.failedMethos)
			System.out.println(m);
		System.exit(0);
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
				if(m.contains("format"))
					continue;
				printStartSummary(m);
				try {
					flows.merge(s.createMethodSummary(m, sigs));
				} catch (RuntimeException e) {
					failedMethos.add(m);
					HandleException.handleException(flows, file, folder, e, "createSummary in class: " + clz + " method: " + m);
					if (!continueOnError)
						throw e;
					flows.merge(createDummyTaintAllFlow(m));
				}
				printEndSummary(m);
			} else {
				System.out.println("Skipped: " + m.toString());
			}
		}
		write(flows, file, folder);
		System.out.println("Methods summaries for: " + clz + " created in " + (System.nanoTime() - beforeSummary) / 1E9 + " seconds");
	}

	private Map<String, Set<MethodFlow>> createDummyTaintAllFlow(String m) {
		FlowSource source = SourceSinkFactory.createThisSource();
		FlowSink sink = SourceSinkFactory.createReturnSink(true);
		MethodFlow flow = new DefaultMethodFlow(m, source, sink);
		Map<String,Set<MethodFlow>> res = new HashMap<>();
		Set<MethodFlow> flows = new HashSet<>();
		flows.add(flow);
		res.put(m, flows);
		return res;
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
		s.setForceTaintSubFields(forceTaintSubFields);
		
		if(useTaintWrapper){
		try {
			s.setTaintWrapper(new EasyTaintWrapper("EasyTaintWrapperSourceComplet.txt"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
		s.setSubstitutedWith(subWith);
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
		File f = new File(folder);
		if(!f.exists())
			f.mkdir();
		try {
			writer.write(flows);
		} catch (XMLStreamException e) {
			e.printStackTrace();
			if(!continueOnError)
				throw new RuntimeException(e);
		}
	}

}
