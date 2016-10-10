package nl.defsoftware.mrgb.models;

import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import nl.defsoftware.mrgb.models.graph.AbstractNode;
import nl.defsoftware.mrgb.models.graph.Node;
import nl.defsoftware.mrgb.models.graph.NodeType;

/**
 * @author D.L. Ettema
 *
 */
public class Rib extends AbstractNode implements Node {

    private char[] sequence;
    private short[] genomeIds; // ORI attribute in the GFA file
    private short referenceGenomeId; // CRD attribute in the GFA file
    private Integer referenceGenomeCoordinates; // START attribute in the GFA
                                                // file
    private int[] connectedEdges = new int[0];
    private Int2IntOpenHashMap edgeWeights = new Int2IntOpenHashMap();
    private int xCoordinate;
    private int yCoordinate;

    // private List<String> crdctg; //unknown what to use for. Not yet
    // implemented
    // private List<String> ctg; //unknown what to use for. Not yet implemented

    public Rib(int nodeId, char[] sequence, short[] genomeIds, short referenceGenomeId,
            Integer referenceGenomeCoordinates) {
        super.setNodeId(nodeId);
        this.sequence = sequence;
        this.genomeIds = genomeIds;
        this.referenceGenomeId = referenceGenomeId;
        this.referenceGenomeCoordinates = referenceGenomeCoordinates;
    }

    public Rib(int nodeId) {
        super.setNodeId(nodeId);
    }

    public void setConnectedEdges(int[] connectedEdges) {
        this.connectedEdges = connectedEdges;
    }

    public int[] getConnectedEdges() {
        return connectedEdges;
    }

    public int amountOfGenomes() {
        return genomeIds.length;
    }

    public void setCoordinates(int xCoord, int yCoord) {
        xCoordinate = xCoord;
        yCoordinate = yCoord;
    }

    public short getRankedWeightOfEdge(int nodeId) {
        return (short) Math.ceil((Math.random() * nodeId));
    }

    public char[] getSequence() {
        return sequence;
    }

    public void setSequence(char[] sequence) {
        this.sequence = sequence;
    }

    public short[] getGenomeIds() {
        return genomeIds;
    }

    public void setGenomeIds(short[] genomeIds) {
        this.genomeIds = genomeIds;
    }

    public short getReferenceGenomeId() {
        return referenceGenomeId;
    }

    public void setReferenceGenomeId(short referenceGenomeId) {
        this.referenceGenomeId = referenceGenomeId;
    }

    public Integer getReferenceGenomeCoordinates() {
        return referenceGenomeCoordinates;
    }

    public void setReferenceGenomeCoordinates(Integer referenceGenomeCoordinates) {
        this.referenceGenomeCoordinates = referenceGenomeCoordinates;
    }

    public int getXCoordinate() {
        return xCoordinate;
    }

    public void setXCoordinate(int xCoordinate) {
        this.xCoordinate = xCoordinate;
    }

    public int getYCoordinate() {
        return yCoordinate;
    }

    public void setYCoordinate(int yCoordinate) {
        this.yCoordinate = yCoordinate;
    }

    @Override
    public boolean isComposite() {
        return false;
    }
    
    @Override
    public boolean isNotComposite() {
        return !isComposite();
    }

    @Override
    public NodeType getNodeType() {
        return NodeType.SINGLE_NODE;
    }
}
