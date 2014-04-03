package soot.jimple.infoflow.methodSummary.data.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import soot.SootField;


public class SummaryAccessPath {
	List<String> ap = new ArrayList<String>();
	
	
	public SummaryAccessPath(SootField s) {
		if(s != null)
			ap.add(s.toString());
	}
	private SummaryAccessPath(List<String> a){
		this.ap = a;
	}
	public int getAPLength(){
		return ap.size();
	}
	public String fieldIdx(int index){
		if(ap.size() > index)
			return ap.get(index);
		return null;
	}
	
	public SummaryAccessPath extend(SootField s){
		List<String> a = new ArrayList<String>(ap);
		a.add(s.toString());
		return new SummaryAccessPath(a);
	}
	
	//creates an accesspath from xml file
	public SummaryAccessPath(String s){
		ap.addAll(Arrays.asList(s.split(" ")));
	}
	
	
	public boolean hasAP(){
		return ap.size() > 0;
	}
	public List<String> getAP(){
		return ap;
	}
	public String toString(){
		StringBuffer buf = new StringBuffer();
		for(String s : ap){
			buf.append(s);
		}
		return buf.toString().trim();
	}
}
