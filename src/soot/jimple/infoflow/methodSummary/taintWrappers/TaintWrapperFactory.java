package soot.jimple.infoflow.methodSummary.taintWrappers;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.List;

import javax.xml.stream.XMLStreamException;

import soot.jimple.infoflow.methodSummary.data.LazySummary;
import soot.jimple.infoflow.taintWrappers.ITaintPropagationWrapper;

public class TaintWrapperFactory {
	
	public static ITaintPropagationWrapper createTaintWrapper(List<String> files) throws FileNotFoundException, XMLStreamException {
		List<File> fs = new LinkedList<File>();
		for(String s : files)
			fs.add(new File(s));
		return new SummaryTaintWrapper(new LazySummary(fs));
	}

	public static ITaintPropagationWrapper createTaintWrapper(String f) throws FileNotFoundException, XMLStreamException {
		return createTaintWrapper(java.util.Collections.singletonList(f));
	}
	
	public static ITaintPropagationWrapper createTaintWrapper(File f){
		return new SummaryTaintWrapper(new LazySummary(f));
	}

}
