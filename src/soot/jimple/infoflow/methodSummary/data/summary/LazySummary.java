package soot.jimple.infoflow.methodSummary.data.summary;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import soot.SootMethod;
import soot.jimple.infoflow.methodSummary.data.MethodFlow;
import soot.jimple.infoflow.methodSummary.xml.XMLReader;


/**
 * This class loads method summary xml files on demand.
 *
 */
public class LazySummary {

	private XMLReader reader;
	private MethodSummaries flows = new MethodSummaries();
	private Set<String> supportedClasses = new HashSet<String>();
	private Set<String> loadableClasses = new HashSet<String>();
	private Set<File> files;

	/**
	 * Loads a file or all files in a dir (not recursively)
	 * @param source
	 */
	public LazySummary(File source) {
		if (!source.exists())
			throw new RuntimeException("Source directory " + source + " does not exist");
		
		if (source.isFile())
			files = Collections.singleton(source);
		else if (source.isDirectory()) {
			File[] filesInDir = source.listFiles();
			if (filesInDir == null)
				throw new RuntimeException("Could not get files in directory " + source);
			files = new HashSet<File>(Arrays.asList(filesInDir));
		}
		else
			throw new RuntimeException("Invalid input file: " + source);
		
		init();
	}

	public LazySummary(List<File> files) {
		this.files = new HashSet<File>();
		for(File f : files) {
			if (f.isFile())
				this.files.add(f);
			else if (f.isDirectory()) {
				File[] filesInDir = f.listFiles();
				if (filesInDir == null)
					throw new RuntimeException("Could not get files in directory " + f);
				files.addAll(Arrays.asList(filesInDir));
			}
			else
				throw new RuntimeException("Invalid input file: " + f);
		}
		
		init();
	}

	private void init() {
		this.reader = new XMLReader();
		for (File f : files) {
			if (f.isFile() && f.getName().endsWith(".xml")) {
				loadableClasses.add(f.getName().replace(".xml", ""));
			}
		}
	}

	public boolean supportsClass(String clazz) {
		if (supportedClasses.contains(clazz))
			return true;
		if (loadableClasses.contains(clazz))
			return true;
		return false;
	}

	public Set<MethodFlow> getMethodFlows(SootMethod method) {
		String clazz = method.getDeclaringClass().getName();
		if (loadableClasses.contains(clazz)) {
			loadClass(clazz);
		}
		return flows.getFlowsForMethod(method.getSignature()); 
	}

	private void loadClass(String clazz) {
		for (File f : files) {
			if (fileToClass(f).equals(clazz)) {
				try {
					flows.merge(reader.read(f));
					loadableClasses.remove(clazz);
					supportedClasses.add(clazz);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	private String fileToClass(File f) {
		return f.getName().replace(".xml", "");
	}
	
	public Set<String> getSupportedClasses() {
		return this.supportedClasses;
	}
	
	public Set<String> getLoadableClasses() {
		return this.loadableClasses;
	}

}
