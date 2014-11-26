package soot.jimple.infoflow.methodSummary.postProcessor;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import soot.jimple.Stmt;
import soot.jimple.infoflow.data.Abstraction;
import soot.jimple.infoflow.data.AccessPath;
import soot.jimple.infoflow.data.SourceContextAndPath;
import soot.jimple.infoflow.data.pathBuilders.ContextSensitivePathBuilder;
import soot.jimple.infoflow.results.ResultSourceInfo;
import soot.jimple.infoflow.solver.IInfoflowCFG;
import soot.jimple.infoflow.source.SourceInfo;

/**
 * Extended path reconstruction algorithm for StubDroid
 * 
 * @author Steven Arzt
 */
public class SummaryPathBuilder extends ContextSensitivePathBuilder {
	
	private Set<SummarySourceInfo> sourceInfos = new HashSet<SummarySourceInfo>();
	
	/**
	 * Extended version of the {@link SourceInfo} class that also allows to
	 * store the abstractions along the path.
	 * 
	 * @author Steven Arzt
	 */
	public class SummarySourceInfo extends ResultSourceInfo {
		
		private final List<Abstraction> abstractionPath;
		
		public SummarySourceInfo(AccessPath source, Stmt context, Object userData,
				List<Stmt> path, List<Abstraction> abstractionPath) {
			super(source, context, userData, path);
			this.abstractionPath = abstractionPath;
		}
		
		/**
		 * Gets the sequence of abstractions along the propagation path
		 * @return The sequence of abstractions along the propagation path
		 */
		public List<Abstraction> getAbstractionPath() {
			return this.abstractionPath;
		}
		
	}
	
	/**
	 * Creates a new instance of the SummaryPathBuilder class 
	 * @param icfg The interprocedural control-flow graph to use
	 * @param maxThreadNum The maximum number of threads to use
	 */
	public SummaryPathBuilder(IInfoflowCFG icfg, int maxThreadNum) {
		super(icfg, maxThreadNum, true);
	}
	
	@Override
	protected boolean checkForSource(Abstraction abs, SourceContextAndPath scap) {
		if (!super.checkForSource(abs, scap))
			return false;
		
		// Save the abstraction path
		SummarySourceInfo ssi = new SummarySourceInfo(
				abs.getSourceContext().getAccessPath(),
				abs.getSourceContext().getStmt(),
				abs.getSourceContext().getUserData(),
				scap.getPath(),
				scap.getAbstractionPath());
		this.sourceInfos.add(ssi);
		return true;
	}
	
	/**
	 * Clears all results computed by this path reconstruction algorithm so far
	 */
	public void clear() {
		getResults().clear();
		sourceInfos.clear();
	}
	
	/**
	 * Gets the source information and the reconstructed paths
	 * @return The found sources and the respective propagation paths
	 */
	public Set<SummarySourceInfo> getSourceInfos() {
		return this.sourceInfos;
	}

}
