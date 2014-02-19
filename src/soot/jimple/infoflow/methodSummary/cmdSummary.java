package soot.jimple.infoflow.methodSummary;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.stream.XMLStreamException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import soot.jimple.infoflow.methodSummary.data.MethodSummaries;
import soot.jimple.infoflow.methodSummary.util.HandleException;
import soot.jimple.infoflow.methodSummary.xml.ISummaryWriter;
import soot.jimple.infoflow.methodSummary.xml.WriterFactory;

public class cmdSummary {

	private static final Logger logger = LoggerFactory.getLogger(SummaryGenerator.class);
	//TODO add a possibility to provide SubstituteClasses 
	public static final String METHODS = "-m";
	public static final String CLASS_SIG_KEY = "-c";
	public static final String METHOD_FILTER_KEY = "-mf";
	public static final String OUTPUT_FOLDER_KEY = "-f";
	public static final String RUN_UNSAFE = "-unsafe";
	private static final String DEFAULT_OUTPUT_FOLDER = "";
	
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
					File outFolder = new File(outputFolder);
					if (!outFolder.exists())
						outFolder.mkdirs();
				}
				if(containsKey(RUN_UNSAFE, args)){
					run_unsafe = true;
				}
				
				if (containsKey(METHOD_FILTER_KEY, args)) {
					filterMethods = Arrays.asList(getValue(METHOD_FILTER_KEY, args).split(";"));
				} else {
					filterMethods = java.util.Collections.emptyList();
				}

				createSummary(mSigs, outputFolder, filterMethods,run_unsafe);
			} else {
				System.out.println(programMAN());
			}
		} else
			System.out.println("it's necessary to provide at least one method signature");
	}
	
	private static String programMAN() {
		return "SummaryMain "
				+ METHODS
				+ " Soot method signatures (use ';' if multiple signatures)\n"
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
	
	/**
	 * Creates library summaries for the given methods
	 * @param mSigs The signatures of the methods for which to create the
	 * summaries
	 * @param folder The folder in which to store the generated summaries
	 * @param filter A list of filter strings. Methods that do not contain any
	 * of the strings in this list are filtered out. If this list is null or
	 * empty, no filtering is applied
	 * @param run_unsafe2 True if exceptions shall NOT be ignored during
	 * execution
	 * @throws FileNotFoundException
	 * @throws XMLStreamException
	 */
	private static void createSummary(String[] mSigs, String folder, List<String> filter, boolean run_unsafe2)
			throws FileNotFoundException, XMLStreamException {
		for (String clz : getAllClasses(mSigs)) {
			long beforeSummary = System.nanoTime();
			String file = classToFile(clz);
			System.out.println("create methods summaries for: " + clz + " output to: " + folder);
			SummaryGenerator s = new SummaryGenerator();
			MethodSummaries flows = new MethodSummaries();
			
			// Do not overwrite existing summaries
			String xmlFile = classToFile(clz);
			File f = new File(folder + File.separator + xmlFile);
			if (f.exists())
				continue;
			
			for (String m : mSigs) {
				if (getClassNameFromMethodSig(m).equals(clz)) {
					if (filterInclude(m, filter)) {
						printStartSummary(m);
						try {
							flows.merge(s.createMethodSummary(m));
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
			write(flows, xmlFile, folder);
			System.out.println("Methods summaries for: " + clz + " created in "
					+ (System.nanoTime() - beforeSummary) / 1E9 + " seconds");
		}
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

	private static void write(MethodSummaries flows, String fileName, String folder) {
		ISummaryWriter writer = WriterFactory.createXMLWriter(fileName, folder);
		try {
			writer.write(flows);
		} catch (XMLStreamException e) {
			logger.error("failed to writer flows");
		}
	}
	
	private static String getClassNameFromMethodSig(String mSig) {
		String tmp = mSig.trim().replace("<", "");
		return tmp.substring(0, tmp.indexOf(":"));
	}

	private static Collection<String> getAllClasses(String[] mSig) {
		Set<String> res = new HashSet<String>();
		for (String m : mSig)
			res.add(getClassNameFromMethodSig(m));
		return res;
	}
}
