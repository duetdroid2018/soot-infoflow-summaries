package soot.jimple.infoflow.test.methodSummary.junit;

import java.io.FileNotFoundException;
import java.util.ArrayList;

import javax.xml.stream.XMLStreamException;

import org.junit.Test;

import soot.jimple.infoflow.methodSummary.cmdSummary;

public class GetPathsTest {

	@Test(timeout = 250000)
	public void getPathTest() throws FileNotFoundException, XMLStreamException {
		String mSig = "<soot.jimple.infoflow.test.methodSummary.FieldToPara: void listParameter5(java.util.List)>;"
				+ "<soot.jimple.infoflow.test.methodSummary.FieldToPara: void listParameter5(java.util.List)>;"
				+ "<soot.jimple.infoflow.test.methodSummary.FieldToPara: void listParameter5(java.util.List)>;"
				+ "<soot.jimple.infoflow.test.methodSummary.FieldToPara: void listParameter5(java.util.List)>;"
				+ "<soot.jimple.infoflow.test.methodSummary.FieldToPara: void listParameter5(java.util.List)>;"
				+ "<soot.jimple.infoflow.test.methodSummary.FieldToPara: void listParameter5(java.util.List)>;"
				+ "<soot.jimple.infoflow.test.methodSummary.FieldToPara: void listParameter5(java.util.List)>;"
				+ "<soot.jimple.infoflow.test.methodSummary.FieldToPara: void listParameter5(java.util.List)>;"
				+ "<soot.jimple.infoflow.test.methodSummary.FieldToPara: void listParameter5(java.util.List)>;"
				+ "<soot.jimple.infoflow.test.methodSummary.FieldToPara: void listParameter5(java.util.List)>";
		ArrayList<String> runArgs = new ArrayList<String>();
		runArgs.add("-m " + mSig);
		runArgs.add("-unsafe");
		cmdSummary.main(runArgs.toArray(new String[runArgs.size()]));
	}

}
