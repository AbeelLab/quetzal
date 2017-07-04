package nl.defsoftware.mrgb.view.controllers;

/**
 * @author D.L. Ettema
 *
 */
public class RankedEdges {

    private int parentNodeId;
    private int rank;
    
    public RankedEdges(int parentNodeId, int rank) {
        this.parentNodeId = parentNodeId;
        this.rank = rank;
    }

    public int getParentNodeId() {
        return parentNodeId;
    }

    public void setParentNodeId(int parentNodeId) {
        this.parentNodeId = parentNodeId;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }
}
