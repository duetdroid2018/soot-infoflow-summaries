package soot.jimple.infoflow.methodSummary.data.impl;

import java.util.List;

import soot.jimple.infoflow.methodSummary.data.AbstractFlowSink;
import soot.jimple.infoflow.methodSummary.data.AbstractFlowSource;
import soot.jimple.infoflow.methodSummary.data.AbstractMethodFlow;

public class DefaultMethodFlow extends AbstractMethodFlow {
	public String methodSig() {
		return methodSig;
	}

	private final String methodSig;
	private final AbstractFlowSource from;
	private final AbstractFlowSink to;
//	private final List<String> path;

	public DefaultMethodFlow(String methodSig, AbstractFlowSource from, AbstractFlowSink to) {
		this.methodSig = methodSig;
		this.from = from;
		this.to = to;
	//	this.path = null;
	}

	public DefaultMethodFlow(String methodSig, AbstractFlowSource from, AbstractFlowSink to, List<String> path) {
		this.methodSig = methodSig;
		this.from = from;
		this.to = to;
		//this.path = path;
	}

	@Override
	public AbstractFlowSource source() {
		return from;
	}

	@Override
	public AbstractFlowSink sink() {
		return to;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || !(obj instanceof DefaultMethodFlow))
			return false;
		
		DefaultMethodFlow other = (DefaultMethodFlow) obj;
		if (!other.methodSig.equals(this.methodSig))
			return false;
		if (!other.from.equals(this.from))
			return false;
		if (!other.to.equals(this.to))
			return false;
		return true;
	}
	
	@Override
	public int hashCode() {
		int prime = 31;
		return prime * methodSig.hashCode()
				+ prime * from.hashCode()
				+ prime * to.hashCode();
	}
	
	@Override
	public String toString(){
		return "{" + methodSig +" Source: [" + from.toString() + "] Sink: [" + to.toString() + "]" + "}";		
	}

	@Override
	public String classSig() {
		return methodSig.substring(1, methodSig.indexOf(":"));
	}

}
