package soot.jimple.infoflow.methodSummary.data.summary;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import soot.SootMethod;
import soot.jimple.infoflow.methodSummary.data.MethodFlow;
import soot.jimple.infoflow.methodSummary.xml.ISummaryReader;
import soot.jimple.infoflow.methodSummary.xml.XMLReader;

public class LazySummary {

	private ISummaryReader reader;
	private MethodSummaries flows = new MethodSummaries();
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
		this.files = new LinkedList<File>();
		for(File f : files){
			if(f.isFile()){
				this.files.add(f);
			}else{
				this.files.addAll(Arrays.asList(f.listFiles()));
			}
		}
		//this.files = files;
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
					flows.merge(reader.processXMLFile(f));
					loadableClasses.remove(clazz);
					supportedClasses.add(clazz);
					//System.out.println();
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
