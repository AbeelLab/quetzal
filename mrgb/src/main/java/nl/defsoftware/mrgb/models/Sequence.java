package nl.defsoftware.mrgb.models;

import java.util.List;

/**
 * 
 * @author D.L. Ettema
 * 22 May 2016
 */
public class Sequence {

	private Integer nodeNumber;
	private char[] sequence;
	private List<String> ori;
	private List<String> crd;
	private List<String> crdctg;
	private List<String> ctg;
	
	public Sequence(Integer nodeNumber, char[] sequence, List<String> ori, List<String> crd, List<String> crdctg,
			List<String> ctg) {
		this.nodeNumber = nodeNumber;
		this.sequence = sequence;
		this.ori = ori;
		this.crd = crd;
		this.crdctg = crdctg;
		this.ctg = ctg;
	}
	
	public Integer getNodeNumber() {
		return nodeNumber;
	}
	public void setNodeNumber(Integer nodeNumber) {
		this.nodeNumber = nodeNumber;
	}
	public char[] getSequence() {
		return sequence;
	}
	public void setSequence(char[] sequence) {
		this.sequence = sequence;
	}
	public List<String> getOri() {
		return ori;
	}
	public void setOri(List<String> ori) {
		this.ori = ori;
	}
	public List<String> getCrd() {
		return crd;
	}
	public void setCrd(List<String> crd) {
		this.crd = crd;
	}
	public List<String> getCrdctg() {
		return crdctg;
	}
	public void setCrdctg(List<String> crdctg) {
		this.crdctg = crdctg;
	}
	public List<String> getCtg() {
		return ctg;
	}
	public void setCtg(List<String> ctg) {
		this.ctg = ctg;
	}
}
