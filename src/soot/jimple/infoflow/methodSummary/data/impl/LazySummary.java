package soot.jimple.infoflow.methodSummary.data.impl;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import soot.SootMethod;
import soot.jimple.infoflow.methodSummary.data.AbstractMethodFlow;
import soot.jimple.infoflow.methodSummary.util.MergeSummaries;
import soot.jimple.infoflow.methodSummary.xml.ISummaryReader;
import soot.jimple.infoflow.methodSummary.xml.XMLReader;

public class LazySummary {

	private ISummaryReader reader;
	private Map<String, Set<AbstractMethodFlow>> flows = new HashMap<String, Set<AbstractMethodFlow>>();
	private Set<String> supportedClasses = new HashSet<String>();
	private Set<String> loadableClasses = new HashSet<String>();
	private List<File> files;

	public LazySummary(File source) {
		if (source.isFile()) {
			files = java.util.Collections.singletonList(source);
		} else {
			files = Arrays.asList(source.listFiles());
		}
		init();
	}

	public LazySummary(List<File> files) {
		this.files = files;
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

	public Set<AbstractMethodFlow> getMethodFlows(SootMethod method) {
		String clazz = method.getDeclaringClass().getName();
		if (loadableClasses.contains(clazz)) {
			loadClass(clazz);
		}
		if (flows.containsKey(method.getSignature())) {
			return flows.get(method.getSignature());
		}
		return java.util.Collections.emptySet();
	}

	private void loadClass(String clazz) {
		for (File f : files) {
			if (fileToClass(f).equals(clazz)) {
				Map<String, Set<AbstractMethodFlow>> newFlows;
				try {
					newFlows = reader.processXMLFile(f);
					MergeSummaries.putAll(flows, newFlows);
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

}
