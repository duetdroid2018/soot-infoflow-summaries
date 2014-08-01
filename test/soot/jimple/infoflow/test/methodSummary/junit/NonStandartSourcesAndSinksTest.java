package soot.jimple.infoflow.test.methodSummary.junit;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import soot.jimple.infoflow.Infoflow;
import soot.jimple.infoflow.config.ConfigForTest;
import soot.jimple.infoflow.entryPointCreators.DefaultEntryPointCreator;
import soot.jimple.infoflow.methodSummary.handler.SummaryTaintPropagationHandler;
import soot.jimple.infoflow.methodSummary.source.SummarySourceSinkManager;
import soot.jimple.infoflow.source.ISourceSinkManager;

public class NonStandartSourcesAndSinksTest {
	private static String appPath, libPath;
	public static final String NON_STANDART_SOURCES_AND_SINKS ="soot.jimple.infoflow.test.NonStandartSourcesAndSinks";
	public static final String APICLASS ="soot.jimple.infoflow.test.methodSummary.ApiClass";
	
	private ISourceSinkManager createSourceSinkManger(String methodSig,String clz){
		return new SummarySourceSinkManager(methodSig,5,false);
	}
	
	@Test
	public void intMultiTest3(){
		Infoflow infoflow = initInfoflow();
		String mSig = "<soot.jimple.infoflow.test.NonStandartSourcesAndSinks: int intMultiTest3(int,int)>";
    	List<String> epoints = new ArrayList<String>();
    	epoints.add(mSig);
    	DefaultEntryPointCreator dEntryPointCreater = new DefaultEntryPointCreator(epoints);
		infoflow.computeInfoflow(appPath, libPath, dEntryPointCreater, createSourceSinkManger(mSig,NON_STANDART_SOURCES_AND_SINKS));
		System.out.println(infoflow.getResults().toString());
		System.out.println(infoflow.getResults().size());
		
	}
	
	@Test
	public void listParameter5(){
		Infoflow infoflow = initInfoflow();
		
		String mSig = "<soot.jimple.infoflow.test.NonStandartSourcesAndSinks: void listParameter5(java.util.List)>";
		infoflow.addTaintPropagationHandler(new SummaryTaintPropagationHandler(mSig));
    	List<String> epoints = new ArrayList<String>();
    	epoints.add(mSig);
    	DefaultEntryPointCreator dEntryPointCreater = new DefaultEntryPointCreator(epoints);
    	
		infoflow.computeInfoflow(appPath, libPath, dEntryPointCreater, createSourceSinkManger(mSig,NON_STANDART_SOURCES_AND_SINKS));
		System.out.println(infoflow.getResults().toString());
		System.out.println(infoflow.getResults().size());
	}

	@Test
	public void twoParaToField(){
		Infoflow infoflow = initInfoflow();
		
		String mSig = "<soot.jimple.infoflow.test.methodSummary.ApiClass: int paraToVar(int,int)>";
		infoflow.addTaintPropagationHandler(new SummaryTaintPropagationHandler(mSig));
    	List<String> epoints = new ArrayList<String>();
    	epoints.add(mSig);
    	DefaultEntryPointCreator dEntryPointCreater = new DefaultEntryPointCreator(epoints);
    	
		infoflow.computeInfoflow(appPath, libPath, dEntryPointCreater, createSourceSinkManger(mSig,APICLASS));
		System.out.println(infoflow.getResults().toString());
		System.out.println(infoflow.getResults().size());
	}
	
	protected Infoflow initInfoflow(){
    	Infoflow result = new Infoflow();
    	ConfigForTest testConfig = new ConfigForTest();
    	result.setSootConfig(testConfig);
    	return result;
    }
	
	@BeforeClass
    public static void setUp() throws IOException
    {
        final String sep = System.getProperty("path.separator");
    	File f = new File(".");
        File testSrc1 = new File(f,"bin");
        File testSrc2 = new File(f,"build" + File.separator + "classes");

        if (! (testSrc1.exists() || testSrc2.exists())){
            fail("Test aborted - none of the test sources are available");
        }

    	libPath = System.getProperty("java.home") + File.separator + "lib" + File.separator + "rt.jar";
    	appPath = testSrc1.getCanonicalPath() + sep + testSrc2.getCanonicalPath();
    }

}
