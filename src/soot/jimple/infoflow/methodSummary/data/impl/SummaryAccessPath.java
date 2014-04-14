package soot.jimple.infoflow.methodSummary.data.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import soot.SootField;


public class SummaryAccessPath {
	List<String> ap = new ArrayList<String>();
	
	public SummaryAccessPath(){
		
	}
	public SummaryAccessPath(SootField s) {
		if(s != null)
			ap.add(s.toString());
	}
	public SummaryAccessPath(List<SootField> s) {
		if(s != null){
			for(SootField f : s)
				ap.add(f.toString());
		}
			
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
		SummaryAccessPath ap = new SummaryAccessPath();
		ap.setAP(a);
		return ap;
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
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((ap == null) ? 0 : ap.hashCode());
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
		SummaryAccessPath other = (SummaryAccessPath) obj;
		if (ap == null) {
			if (other.ap != null)
				return false;
		} else if (!ap.equals(other.ap))
			return false;
		return true;
	}
	private void setAP(List<String> ap){
		this.ap = ap;
	}
	
}
