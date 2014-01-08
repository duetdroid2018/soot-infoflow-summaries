package soot.jimple.infoflow.test.methodSummary.junit;

import java.io.File;
import java.io.FileNotFoundException;

import javax.xml.stream.XMLStreamException;

import soot.jimple.infoflow.methodSummary.taintWrappers.TaintWrapperFactory;
import soot.jimple.infoflow.taintWrappers.ITaintPropagationWrapper;
import soot.jimple.infoflow.test.WrapperListTests;

public class SummaryWrapperListTest extends WrapperListTests{
	static File files = new File("testSummaries");//String[] files =  {"testSummaries\\LinkedList.xml","testSummaries\\ArrayList.xml","testSummaries\\Stack.xml"};
	public SummaryWrapperListTest() throws FileNotFoundException, XMLStreamException {
		super((ITaintPropagationWrapper) TaintWrapperFactory.createTaintWrapper(files));
	}
}
