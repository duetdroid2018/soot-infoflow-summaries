package soot.jimple.infoflow.methodSummary.postProcessor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Set;

import soot.jimple.Stmt;
import soot.jimple.infoflow.collect.ConcurrentHashSet;
import soot.jimple.infoflow.data.Abstraction;
import soot.jimple.infoflow.data.AccessPath;
import soot.jimple.infoflow.data.SourceContextAndPath;
import soot.jimple.infoflow.data.pathBuilders.ContextSensitivePathBuilder;
import soot.jimple.infoflow.results.InfoflowResults;
import soot.jimple.infoflow.results.ResultSinkInfo;
import soot.jimple.infoflow.results.ResultSourceInfo;
import soot.jimple.infoflow.solver.cfg.IInfoflowCFG;
import soot.jimple.infoflow.source.SourceInfo;

/**
 * Extended path reconstruction algorithm for StubDroid
 * 
 * @author Steven Arzt
 */
public class SummaryPathBuilder extends ContextSensitivePathBuilder {
	
	private Set<SummaryResultInfo> resultInfos = new ConcurrentHashSet<SummaryResultInfo>();
	private Set<Abstraction> visitedAbstractions = Collections.newSetFromMap(new IdentityHashMap<Abstraction,Boolean>());
	
	/**
	 * Extended version of the {@link SourceInfo} class that also allows to
	 * store the abstractions along the path.
	 * 
	 * @author Steven Arzt
	 */
	public class SummarySourceInfo extends ResultSourceInfo {
		
		private final List<Abstraction> abstractionPath;
		
		public SummarySourceInfo(AccessPath source, Stmt context, Object userData,
				List<Abstraction> abstractionPath) {
			super(source, context, userData, null, null);
			this.abstractionPath = abstractionPath;
		}
		
		/**
		 * Gets the sequence of abstractions along the propagation path
		 * @return The sequence of abstractions along the propagation path
		 */
		public List<Abstraction> getAbstractionPath() {
			return this.abstractionPath;
		}
		
		@Override
		public List<Stmt> getPath() {
			List<Stmt> stmts = new ArrayList<>(abstractionPath.size());
			for (Abstraction abs : abstractionPath)
				if (abs.getCurrentStmt() != null)
					stmts.add(abs.getCurrentStmt());
			return stmts;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = super.hashCode();
			result = prime * result + ((abstractionPath == null) ? 0 : abstractionPath.hashCode());
			return result;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (!super.equals(obj))
				return false;
			if (getClass() != obj.getClass())
				return false;
			SummarySourceInfo other = (SummarySourceInfo) obj;
			if (abstractionPath == null) {
				if (other.abstractionPath != null)
					return false;
			} else if (!abstractionPath.equals(other.abstractionPath))
				return false;
			return true;
		}
		
	}
	
	/**
	 * Data class containing a single source-to-sink connection produced by
	 * FlowDroid
	 * 
	 * @author Steven Arzt
	 */
	public class SummaryResultInfo {
		
		private final SummarySourceInfo sourceInfo;
		private final ResultSinkInfo sinkInfo;
		
		/**
		 * Creates a new instance of the {@link SummaryResultInfo} class
		 * @param sourceInfo The source information object
		 * @param sinkInfo The sink information object
		 */
		public SummaryResultInfo(SummarySourceInfo sourceInfo,
				ResultSinkInfo sinkInfo) {
			this.sourceInfo = sourceInfo;
			this.sinkInfo = sinkInfo;
		}
		
		/**
		 * Gets the source information for this source-to-sink connection
		 * @return The source information for this source-to-sink connection
		 */
		public SummarySourceInfo getSourceInfo() {
			return this.sourceInfo;
		}
		
		/**
		 * Gets the sink information for this source-to-sink connection
		 * @return The sink information for this source-to-sink connection
		 */
		public ResultSinkInfo getSinkInfo() {
			return this.sinkInfo;
		}
		
		@Override
		public String toString() {
			return "Source: " + sourceInfo + " -> Sink: " + sinkInfo;
		}
		
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((sinkInfo == null) ? 0 : sinkInfo.hashCode());
			result = prime * result + ((sourceInfo == null) ? 0 : sourceInfo.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			SummaryResultInfo other = (SummaryResultInfo) obj;
			if (sinkInfo == null) {
				if (other.sinkInfo != null)
					return false;
			} else if (!sinkInfo.equals(other.sinkInfo))
				return false;
			if (sourceInfo == null) {
				if (other.sourceInfo != null)
					return false;
			} else if (!sourceInfo.equals(other.sourceInfo))
				return false;
			return true;
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
		// Record the abstraction
		visitedAbstractions.add(abs);
		
		// Source abstractions do not have predecessors
		if (abs.getPredecessor() != null)
			return false;
		
		// Save the abstraction path
		SummarySourceInfo ssi = new SummarySourceInfo(
				abs.getSourceContext().getAccessPath(),
				abs.getSourceContext().getStmt(),
				abs.getSourceContext().getUserData(),
				scap.getAbstractionPath());
		ResultSinkInfo rsi = new ResultSinkInfo(
				scap.getAccessPath(),
				scap.getStmt());
		
		this.resultInfos.add(new SummaryResultInfo(ssi, rsi));
		return true;
	}
	
	/**
	 * Clears all results computed by this path reconstruction algorithm so far
	 */
	public void clear() {
		super.getResults().clear();
		resultInfos.clear();
		for (Abstraction abs : visitedAbstractions)
			abs.clearPathCache();
		visitedAbstractions.clear();
	}
	
	/**
	 * Gets the source information and the reconstructed paths
	 * @return The found source-to-sink connections and the respective
	 * propagation paths
	 */
	public Set<SummaryResultInfo> getResultInfos() {
		return this.resultInfos;
	}
	
	@Override
	public InfoflowResults getResults() {
		throw new RuntimeException("Not implemented, use getResultInfos() instead");
	}

}
