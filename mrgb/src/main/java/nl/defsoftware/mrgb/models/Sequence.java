package nl.defsoftware.mrgb.models;

import java.util.List;

/**
 * 
 * @author D.L. Ettema
 *
 */
public class Sequence {

	private Integer nodeNumber;
	private String sequence;
	private List<String> ori;
	private List<String> crd;
	private List<String> crdctg;
	private List<String> ctg;
	
	public Sequence(Integer nodeNumber, String sequence, List<String> ori, List<String> crd, List<String> crdctg,
			List<String> ctg) {
		super();
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
	public String getSequence() {
		return sequence;
	}
	public void setSequence(String sequence) {
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
