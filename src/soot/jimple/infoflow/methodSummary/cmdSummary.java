package soot.jimple.infoflow.methodSummary;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.stream.XMLStreamException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import soot.jimple.infoflow.methodSummary.data.AbstractMethodFlow;
import soot.jimple.infoflow.methodSummary.util.HandleException;
import soot.jimple.infoflow.methodSummary.util.MergeSummaries;
import soot.jimple.infoflow.methodSummary.xml.ISummaryWriter;
import soot.jimple.infoflow.methodSummary.xml.WriterFactory;

public class cmdSummary {

	private static final Logger logger = LoggerFactory.getLogger(Summary.class);
	//TODO add a possibility to provide SubstituteClasses 
	public static final String METHODS = "-m";
	public static final String CLASS_SIG_KEY = "-c";
	public static final String RUN_OPTION_KEY = "-o";
	public static final String METHOD_FILTER_KEY = "-mf";
	public static final String OUTPUT_FOLDER_KEY = "-f";
	public static final String RUN_UNSAFE = "-unsafe";
	private static final String DEFAULT_OUTPUT_FOLDER = "";
	private static final int DEFAULT_RUN_OPTION = 0;
	private static final int TWO_PHASE_RUN = 1;
	private static final int TWO_PHASE_RUN_ONYL_PARA = 2;
	private static final int TWO_PHASE_RUN_ONLY_FIELD = 3;
	private static final boolean DEBUG = false;

	public static void main(String... args) throws FileNotFoundException, XMLStreamException {

		if (args.length == 0) {
			System.out.println("Missing parameters");
			System.out.println(programMAN());
		} else {
			System.out.print("\nSummaryMain");
			for (String s : args) {
				System.out.println(" " + s);
			}
			System.out.println();
		}
		if (containsKey(METHODS, args)) {
			boolean run_unsafe = false;
			List<String> filterMethods;
			String outputFolder = DEFAULT_OUTPUT_FOLDER;
			String[] mSigs = classSignitures(getValue(METHODS, args));
			if (checkMethodSigs(mSigs)) {
				if (containsKey(OUTPUT_FOLDER_KEY, args)) {
					outputFolder = getValue(OUTPUT_FOLDER_KEY, args);
					if (!(new File(outputFolder)).exists()) {
						System.err.println("The output folder: " + outputFolder + " doesn't exist");
						return;
					}
				}
				if(containsKey(RUN_UNSAFE, args)){
					run_unsafe = true;
				}
				
				if (containsKey(METHOD_FILTER_KEY, args)) {
					filterMethods = Arrays.asList(getValue(METHOD_FILTER_KEY, args).split(";"));
				} else {
					filterMethods = java.util.Collections.emptyList();
				}

				int runOp = getRunOption(getValue(RUN_OPTION_KEY, args));
				if (runOp == DEFAULT_RUN_OPTION) {
					
					createSummary(mSigs, outputFolder, filterMethods,run_unsafe);
				} else if (runOp >= TWO_PHASE_RUN && runOp <= TWO_PHASE_RUN_ONLY_FIELD) {
					
					createSummaryTwoPhases( mSigs, outputFolder, filterMethods, runOp,run_unsafe);
				} else {
					System.err.println(runOp + " dosn't match one of the possible run options");
				}
			} else {
				System.out.println(programMAN());
			}
		} else {
			System.out.println("it's necessary to provide at least one method signature");
		}

	}

	private static String programMAN() {
		return "SummaryMain "
				+ METHODS
				+ " Soot method signatures (use ';' if multiple signatures)\n"
				+ "\t["
				+ RUN_OPTION_KEY
				+ " 0|1]"
				+ "\t\t# 0: default - 1: two phase Analysis (first run only method parameter are considered as sources, second run only class fields are considered as sources\n"
				+ "\t[" + METHOD_FILTER_KEY + " method filter]\n" + "\t[" + OUTPUT_FOLDER_KEY
				+ " output folder (directory must exist)]\n";
	}

	private static boolean checkMethodSigs(String[] mSigs) {
		for (String sig : mSigs) {
			if (!sig.contains("<") || !sig.contains(">") || !sig.contains(":")) {
				System.out.println("Error in method signature: " + sig
						+ " (you need to provide soot methods signatures)");
				return false;
			}
		}
		return true;
	}

	private static void createSummaryTwoPhases(String[] mSigs, String folder, List<String> filter, int runOp, boolean run_unsafe2)
			throws XMLStreamException {

		for (String clz : getAllClasses(mSigs)) {
			String file = classToFile(clz);
			System.err.println("create methods summaries (two phases) for: " + clz + " output to: " + folder);
			Map<String, Set<AbstractMethodFlow>> flows = new HashMap<String, Set<AbstractMethodFlow>>();
			Summary s = new Summary();
			s.setIgnoreExceptions(!run_unsafe2);
			if (runOp != TWO_PHASE_RUN_ONLY_FIELD) {
				System.err.println("phase one (parameter is source)");

				for (String m : mSigs) {
					if (getClassNameFromMethodSig(m).equals(clz)) {
						if (filterInclude(m, filter)) {
							printStartSummary(m);
							try {
								Map<String, Set<AbstractMethodFlow>> tmp = s.createMethodSummary(m,getSourceSinkManagerNoFieldSources(m));
								flows = putAll(flows, tmp);
							} catch (RuntimeException e) {
								HandleException.handleException(flows, file, folder, e,
										"createSummaryTwoPhases phase 1 of 2 in class: " + clz + " method: " + m);
								if(run_unsafe2)
									throw e;
							}
							printEndSummary(m);
						} else {
							System.out.println("Skipped: " + m.toString());
						}
					}
				}

				if (DEBUG) {
					System.out.println("##################### Print Flows #########################");
					for (String key : flows.keySet()) {
						for (AbstractMethodFlow flow : flows.get(key)) {
							System.out.println(flow.toString());
						}
					}
					System.out.println("##################### End Print Flows #########################");
				}
			}

			if (runOp != TWO_PHASE_RUN_ONYL_PARA) {
				System.out.println("phase two (field is source)");
				for (String m : mSigs) {
					if (getClassNameFromMethodSig(m).equals(clz)) {
						if (filterInclude(m, filter)) {
							printStartSummary(m);
							try {
								SummarySourceSinkManager manager = new SummarySourceSinkManager(m, flows);
								manager.setParamterAsSource(false);
								manager.setAllFieldsAsSource(true);
								Map<String, Set<AbstractMethodFlow>> tmp = s.createMethodSummary(m, manager);
								flows = putAll(flows, tmp);
							} catch (RuntimeException e) {
								HandleException.handleException(flows, file, folder, e,
										"createSummaryTwoPhases phase 2 of 2 in class: " + clz + " method: " + m);
								if(run_unsafe2)
									throw e;
							}
							printEndSummary(m);
						} else {
							System.out.println("Skipped: " + m.toString());
						}
					}
				}
				if (DEBUG) {
					System.out.println("##################### Print Flows #########################");
					for (String key : flows.keySet()) {
						for (AbstractMethodFlow flow : flows.get(key)) {
							System.out.println(flow.toString());
						}
					}
					System.out.println("##################### End Print Flows #########################");
				}
			}
			write(flows, file, folder);
		}
	}

	private static void createSummary(String[] mSigs, String folder, List<String> filter, boolean run_unsafe2)
			throws FileNotFoundException, XMLStreamException {
		for (String clz : getAllClasses(mSigs)) {
			String file = classToFile(clz);
			System.out.println("create methods summaries for: " + clz + " output to: " + folder);
			Summary s = new Summary();
			s.setIgnoreExceptions(!run_unsafe2);
			Map<String, Set<AbstractMethodFlow>> flows = new HashMap<String, Set<AbstractMethodFlow>>();
			for (String m : mSigs) {
				if (getClassNameFromMethodSig(m).equals(clz)) {
					if (filterInclude(m, filter)) {
						printStartSummary(m);
						try {
							Map<String, Set<AbstractMethodFlow>> tmp = s.createMethodSummary(m);
							flows = putAll(flows, tmp);
						} catch (RuntimeException e) {
							HandleException.handleException(flows, file, folder, e, "createSummary in class: " + clz
									+ " method: " + m);
							if(run_unsafe2)
								throw e;
						}
						printEndSummary(m);
					} else {
						System.out.println("Skipped: " + m.toString());
					}
				}
			}
			write(flows, classToFile(clz), folder);
		}
	}

	private static Map<String, Set<AbstractMethodFlow>> putAll(Map<String, Set<AbstractMethodFlow>> flows,
			Map<String, Set<AbstractMethodFlow>> newFlows) {
		return MergeSummaries.putAll(flows, newFlows);
//		for (String key : newFlows.keySet()) {
//			if (newFlows.get(key) != null && newFlows.get(key).size() > 0) {
//				if (flows.containsKey(key)) {
//					if (flows.get(key) != null)
//						flows.get(key).addAll(newFlows.get(key));
//					else
//						flows.put(key, newFlows.get(key));
//				} else {
//					flows.put(key, newFlows.get(key));
//				}
//			}
//		}
//		return flows;
	}

	private static String classToFile(String c) {
		return c + ".xml";
	}

	private static boolean containsKey(String key, String[] args) {
		return getValue(key, args) != null;
	}

	private static String getValue(String key, String[] args) {
		for (String s : args) {
			if (s.startsWith(key)) {
				return s.substring(key.length()).trim();
			}
		}
		return null;
	}

	private static String[] classSignitures(String s) {
		return s.split(";");
	}

	private static int getRunOption(String value) {
		if (value == null)
			return DEFAULT_RUN_OPTION;
		return Integer.parseInt(value);
	}

	private static boolean filterInclude(String m, List<String> filter) {
		if (filter == null || filter.size() == 0)
			return true;

		for (String s : filter) {
			if (m.contains(s))
				return true;
		}
		return false;
	}

	private static void printStartSummary(String m) {
		System.out.println("##############################################################");
		System.out.println("start summary for: " + m);
		System.out.println("##############################################################");
	}

	private static void printEndSummary(String m) {
		System.out.println("##############################################################");
		System.out.println("finish summary for: " + m);
		System.out.println("##############################################################");
	}

	private static void write(Map<String, Set<AbstractMethodFlow>> flows, String fileName, String folder) {
		ISummaryWriter writer = WriterFactory.createXMLWriter(fileName, folder);
		try {
			writer.write(flows);
		} catch (XMLStreamException e) {
			logger.error("failed to writer flows");
		}
	}

	private static SummarySourceSinkManager getSourceSinkManagerNoFieldSources(String m) {
		SummarySourceSinkManager manager = new SummarySourceSinkManager(m);
		manager.setAllFieldsAsSource(false);
		manager.setCertainFieldsAsSource(false);
		manager.setThisAsSource(false);
		return manager;
	}

	private static String getClassNameFromMethodSig(String mSig) {
		String tmp = mSig.trim().replace("<", "");
		return tmp.substring(0, tmp.indexOf(":"));
	}

	private static List<String> getAllClasses(String[] mSig) {
		List<String> res = new LinkedList<String>();
		for (String m : mSig) {
			String clz = getClassNameFromMethodSig(m);
			if (!res.contains(clz))
				res.add(clz);
		}
		return res;
	}
}
