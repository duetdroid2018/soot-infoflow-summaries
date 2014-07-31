package soot.jimple.infoflow.methodSummary.soot;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import soot.Scene;
import soot.SootClass;
import soot.jimple.infoflow.IInfoflow.AliasingAlgorithm;
import soot.jimple.infoflow.IInfoflow.CallgraphAlgorithm;
import soot.jimple.infoflow.config.IInfoflowConfig;
import soot.jimple.infoflow.data.pathBuilders.DefaultPathBuilderFactory;
import soot.options.Options;

public class SootStartup {
	protected boolean DEBUG = false;
	protected CallgraphAlgorithm callgraphAlgorithm = /*CallgraphAlgorithm.OnDemand;*/ CallgraphAlgorithm.AutomaticSelection;
	protected AliasingAlgorithm aliasingAlgorithm = AliasingAlgorithm.FlowSensitive;
	private final String androidPath;
	private final boolean forceAndroidJar;
	private IInfoflowConfig sootConfig;

	public SootStartup() {
		this.androidPath = "D:\\realSDK\\android.jar";
		this.forceAndroidJar = true;
	}

	public SootStartup(String androidPath, boolean forceAndroidJar) {
		this.androidPath = androidPath;
		this.forceAndroidJar = forceAndroidJar;
	}
	
	public void startSoot(String path){
		initializeSoot("", path, null);
		
	}
	
	
	private void initializeSoot(String appPath, String libPath, String extraSeed) {
		// reset Soot:
		soot.G.reset();
				
		Options.v().set_no_bodies_for_excluded(true);
		Options.v().set_allow_phantom_refs(true);
		if (DEBUG)
			Options.v().set_output_format(Options.output_format_jimple);
		else
			Options.v().set_output_format(Options.output_format_none);
		
		// We only need to distinguish between application and library classes
		// if we use the OnTheFly ICFG
		if (callgraphAlgorithm == CallgraphAlgorithm.OnDemand) {
			Options.v().set_soot_classpath(libPath);
			if (appPath != null) {
				List<String> processDirs = new LinkedList<String>();
				for (String ap : appPath.split(File.pathSeparator))
					processDirs.add(ap);
				Options.v().set_process_dir(processDirs);
			}
		}
		else
			Options.v().set_soot_classpath(appPath + File.pathSeparator + libPath);
		
		// Configure the callgraph algorithm
		switch (callgraphAlgorithm) {
			case AutomaticSelection:
				// If we analyze a distinct entry point which is not static,
				// SPARK fails due to the missing allocation site and we fall
				// back to CHA.
				if (extraSeed == null || extraSeed.isEmpty()) {
					Options.v().setPhaseOption("cg.spark", "on");
					Options.v().setPhaseOption("cg.spark", "string-constants:true");
				}
				else
					Options.v().setPhaseOption("cg.cha", "on");
				break;
			case CHA:
				Options.v().setPhaseOption("cg.cha", "on");
				break;
			case RTA:
				Options.v().setPhaseOption("cg.spark", "on");
				Options.v().setPhaseOption("cg.spark", "rta:true");
				Options.v().setPhaseOption("cg.spark", "string-constants:true");
				break;
			case VTA:
				Options.v().setPhaseOption("cg.spark", "on");
				Options.v().setPhaseOption("cg.spark", "vta:true");
				Options.v().setPhaseOption("cg.spark", "string-constants:true");
				break;
			case SPARK:
				Options.v().setPhaseOption("cg.spark", "on");
				Options.v().setPhaseOption("cg.spark", "string-constants:true");
				break;
			case OnDemand:
				// nothing to set here
				break;
			default:
				throw new RuntimeException("Invalid callgraph algorithm");
		}
		
		// Specify additional options required for the callgraph
		if (callgraphAlgorithm != CallgraphAlgorithm.OnDemand) {
			Options.v().set_whole_program(true);
			Options.v().setPhaseOption("cg", "trim-clinit:false");
		}

		// do not merge variables (causes problems with PointsToSets)
		Options.v().setPhaseOption("jb.ulp", "off");
		
		if (!this.androidPath.isEmpty()) {
			Options.v().set_src_prec(Options.src_prec_apk);
			if (this.forceAndroidJar)
				soot.options.Options.v().set_force_android_jar(this.androidPath);
			else
				soot.options.Options.v().set_android_jars(this.androidPath);
		} else
			Options.v().set_src_prec(Options.src_prec_java);
		
		//at the end of setting: load user settings:
		if (sootConfig != null)
			sootConfig.setSootOptions(Options.v());
		
		// load all entryPoint classes with their bodies
		Scene.v().loadNecessaryClasses();
		boolean hasClasses = false;
//		for (String className : set) {
//			SootClass c = Scene.v().forceResolve(className, SootClass.BODIES);
//			if (c != null){
//				c.setApplicationClass();
//				if(!c.isPhantomClass() && !c.isPhantom())
//					hasClasses = true;
//			}
//		}
		if (!hasClasses) {
			return;
		}
	}
	
	public void setSootConfig(IInfoflowConfig config){
		sootConfig = config;
	}

}
