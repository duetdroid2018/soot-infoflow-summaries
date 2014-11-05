package soot.jimple.infoflow.methodSummary.data;

import java.util.ArrayList;
import java.util.List;

import soot.SootField;

public class SummaryAccessPath {
	List<String> taintedField = new ArrayList<String>();
	
	public SummaryAccessPath(){
		
	}
	public SummaryAccessPath(String field) {
		if(field != null)
			taintedField.add(field.toString());
	}
	public SummaryAccessPath(List<String> fields) {
		if(fields != null){
			for(String f : fields)
				taintedField.add(f.toString());
		}
	}
	
	public int getAPLength(){
		return taintedField.size();
	}
	
	public String getField(int index){
		if(taintedField.size() > index)
			return taintedField.get(index);
		return null;
	}
	
	public SummaryAccessPath extend(SootField s){
		List<String> a = new ArrayList<String>(taintedField);
		a.add(s.toString());
		SummaryAccessPath ap = new SummaryAccessPath();
		ap.setAP(a);
		return ap;
	}
	
	public boolean notEmpty(){
		return taintedField.size() > 0;
	}
	
	public List<String> getFields(){
		return taintedField;
	}
	
	public String toString(){
		StringBuffer buf = new StringBuffer();
		for(String s : taintedField){
			buf.append(s);
		}
		return buf.toString().trim();
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((taintedField == null) ? 0 : taintedField.hashCode());
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
		if (taintedField == null) {
			if (other.taintedField != null)
				return false;
		} else if (!taintedField.equals(other.taintedField))
			return false;
		return true;
	}
	
	private void setAP(List<String> ap){
		this.taintedField = ap;
	}
	
}
