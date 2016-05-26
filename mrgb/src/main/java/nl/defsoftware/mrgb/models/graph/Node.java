/**
 * 
 */
package nl.defsoftware.mrgb.models.graph;

import java.util.Arrays;

/**
 *
 *
 * @author D.L. Ettema
 * @date: 22 May 2016
 */
public class Node {

	/**
	 * toEdges is an array of identifiers from this node. Accessory methods
	 * treat each short as an unsigned short, allowing for 2*short.MIN_VALUE
	 * node identifiers.
	 */
	private short[] edgesIdentifiers;

	/**
	 * To allow for a high number of node identifiers with the least amount of
	 * memory we are treating toEdges as an unsigned variable, thus we store the
	 * identifiers starting from short_MIN_VALUE. Basically mimicking a (signed)
	 * int primitive data type.
	 */
	private short edgesIdentifiersUnsignedOffset;

	/**
	 * This parameter introduced to stay memory efficient. Using the short for
	 * storing edge and node identifiers an(in which we treat each short as a
	 * unsigned short), gives us a limit of 2*32768 = 65536 node identifiers.
	 * Using an offset for the array, we can increase the nodes by 32768 * 65536
	 * = 2.147.483.648 nodes. Basically mimicking a (signed) int primitive data
	 * type.
	 */
	private short edgesArrayOffset = 0;

	/**
	 * Treating the identifier as a unsigned short.
	 */
	private short nodeIdentifier;
	
	private short nodeIdentifierUnsignedOffset;
	
	private short nodeArrayOffset = 0;

	/**
	 * Pretty label used for displaying an easy going name, can be identifer
	 * number
	 */
	private char[] prettyLabel;

	/**
	 * The DNA sequence of this node
	 */
	private char[] sequence;

	/* Properties in the GFA file about this node */
	private char[] ori;
	private char[] crd;
	private char[] crdctg;
	private char[] ctg;
	
	/**
	 * @TODO finish short to integer implementation
	 * @return
	 * 
	 * int[]
	 *
	 */
	public int[] getEdgesIdentifiers() {
		int [] identifiers = new int[edgesIdentifiers.length];
		for (int i = 0; i < edgesIdentifiers.length; i++) {
			//add case for when it is 0 with the unsighed offset, putting it to 0, cause then 0 * edgesArrayOffset, does nothing. 
//			identifiers[i] = (int) (edgesIdentifiers[i] + edgesIdentifiersUnsignedOffset) * edgesArrayOffset;
			identifiers[i] = (int) edgesIdentifiers[i];
		}
		
		return new int[edgesIdentifiers.length];
	}
	public void setEdgesIdentifiers(short[] edgesIdentifiers) {
		this.edgesIdentifiers = edgesIdentifiers;
	}
	public void setIdentifiersUnsignedOffset(short identifiersUnsignedOffset) {
		this.edgesIdentifiersUnsignedOffset = identifiersUnsignedOffset;
	}
	public void setEdgesArrayOffset(short edgesArrayOffset) {
		this.edgesArrayOffset = edgesArrayOffset;
	}
	public int getNodeIdentifier() {
		return nodeIdentifier;
	}
	public void setNodeIdentifier(short nodeIdentifier) {
		this.nodeIdentifier = nodeIdentifier;
	}
	public char[] getPrettyLabel() {
		return prettyLabel;
	}
	public void setPrettyLabel(char[] prettyLabel) {
		this.prettyLabel = prettyLabel;
	}
	public char[] getSequence() {
		return sequence;
	}
	public void setSequence(char[] sequence) {
		this.sequence = sequence;
	}
	public char[] getOri() {
		return ori;
	}
	public void setOri(char[] ori) {
		this.ori = ori;
	}
	public char[] getCrd() {
		return crd;
	}
	public void setCrd(char[] crd) {
		this.crd = crd;
	}
	public char[] getCrdctg() {
		return crdctg;
	}
	public void setCrdctg(char[] crdctg) {
		this.crdctg = crdctg;
	}
	public char[] getCtg() {
		return ctg;
	}
	public void setCtg(char[] ctg) {
		this.ctg = ctg;
	}
	
	@Override
	public String toString() {
		return "Node [edgesIdentifiers=" + Arrays.toString(edgesIdentifiers) + ", identifiersUnsignedOffset="
				+ edgesIdentifiersUnsignedOffset + ", edgesArrayOffset=" + edgesArrayOffset + ", nodeIdentifier="
				+ nodeIdentifier + ", prettyLabel=" + Arrays.toString(prettyLabel) + ", sequence="
				+ Arrays.toString(sequence) + ", ori=" + Arrays.toString(ori) + ", crd=" + Arrays.toString(crd)
				+ ", crdctg=" + Arrays.toString(crdctg) + ", ctg=" + Arrays.toString(ctg) + "]";
	}
}
