package soot.jimple.infoflow.methodSummary.data.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import soot.SootField;

//TODO check if the memory overhead is a problem
//when we create a new ap from an old ap all strings from the old ap are copied into the new one
public class AccessPath {
	List<String> ap = new ArrayList<String>();
	
	
	public AccessPath(SootField s) {
		if(s != null)
			ap.add(s.toString());
	}
	
	public int getAPLength(){
		return ap.size();
	}
	public String fieldIdx(int index){
		if(ap.size() > index)
			return ap.get(index);
		return null;
	}
	
	public AccessPath(SootField s, AccessPath ap){
		this.ap.addAll(ap.ap);
		if(ap != null)
			this.ap.add(s.toString());
	}
	
	//creates an accesspath from xml file
	public AccessPath(String s){
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
