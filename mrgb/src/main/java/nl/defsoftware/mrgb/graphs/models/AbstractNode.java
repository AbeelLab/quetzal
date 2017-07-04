package nl.defsoftware.mrgb.models.graph;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Abstract class with general variables for all nodes. Also overwrites hashCode
 * and equals based on nodeID only.
 * 
 * @author D.L. Ettema
 *
 */
public abstract class AbstractNode implements Node {

    private int nodeId;
    private Set<Node> inEdges;
    private Set<Node> outEdges;
    private short[] genomeIds; // ORI attribute in the GFA file
    
    private boolean isExitNode = false;
    private boolean isEntranceNode = false;
    private double xCoordinate;
    private double yCoordinate;
    private double width;
    private double height;
    private double radius;

    protected AbstractNode() {
        this.inEdges = new HashSet<>();
        this.outEdges = new HashSet<>();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + nodeId;
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
        AbstractNode other = (AbstractNode) obj;
        if (nodeId != other.nodeId)
            return false;
        return true;
    }

    public int getNodeId() {
        return nodeId;
    }

    protected void setNodeId(int nodeId) {
        this.nodeId = nodeId;
    }

    public void addOutEdge(Node node) {
        this.outEdges.add(node);
    }

    public void addInEdge(Node node) {
        this.inEdges.add(node);
    }

    public void addAllOutEdges(Collection<Node> nodes) {
        outEdges.addAll(nodes);
    }

    public void addAllInEdges(Collection<Node> nodes) {
        this.inEdges.addAll(nodes);
    }

    public void removeInEdge(Node node) {
        inEdges.remove(node);
    }

    public void removeOutEdge(Node node) {
        outEdges.remove(node);
    }

    public Collection<Node> getOutEdges() {
        return outEdges;
    }

    public Collection<Node> getInEdges() {
        return inEdges;
    }

    public void setAsBubbleExitNode() {
        isExitNode = true;
    }

    public boolean isExitNode() {
        return isExitNode;
    }

    public void setAsBubbleEntranceNode() {
        isEntranceNode = true;
    }

    public boolean isEntranceNode() {
        return isEntranceNode;
    }

    public void setCoordinates(double xCoord, double yCoord) {
        xCoordinate = xCoord;
        yCoordinate = yCoord;
    }

    public double getXCoordinate() {
        return xCoordinate;
    }

    public void setXCoordinate(double xCoordinate) {
        this.xCoordinate = xCoordinate;
    }

    public double getYCoordinate() {
        return yCoordinate;
    }

    public void setYCoordinate(double yCoordinate) {
        this.yCoordinate = yCoordinate;
    }

    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }
    
    public short[] getGenomeIds() {
        return genomeIds;
    }

    public void setGenomeIds(short[] genomeIds) {
        this.genomeIds = genomeIds;
    }
}
