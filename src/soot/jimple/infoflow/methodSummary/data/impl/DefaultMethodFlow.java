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
		if (obj == null)
			return false;
		if (!(obj instanceof AbstractMethodFlow))
			return false;
		AbstractMethodFlow other = (AbstractMethodFlow) obj;
		if (other.sink().equals(this.sink()) && other.source().equals(this.source()) && other.methodSig().equals(this.methodSig()))
			return true;
		return false;
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
