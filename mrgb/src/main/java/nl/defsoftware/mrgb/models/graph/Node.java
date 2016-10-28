package nl.defsoftware.mrgb.models.graph;

import java.util.Collection;

/**
 * @author D.L. Ettema
 *
 */
public interface Node {

    public int getNodeId();
    
    public boolean isComposite();
    
    public boolean isNotComposite();
    
    public NodeType getNodeType();
    
    public void addOutEdge(Node node);

    public void addInEdge(Node node);

    public void addAllOutEdges(Collection<Node> nodes);
    
    public void addAllInEdges(Collection<Node> nodes);

    public void removeInEdge(Node node);

    public void removeOutEdge(Node node);
    
    public Collection<Node> getOutEdges();
    
    public Collection<Node> getInEdges();
    
    public void setAsBubbleExitNode();
    
    public void setAsBubbleEntranceNode() ;
    
    public boolean isEntranceNode();
    
    public double getXCoordinate();

    public double getYCoordinate();
    
    public void setWidth(double width);
    
    public double getWidth();
    
    public void setHeight(double height);
    
    public double getHeight();
    
    public void setRadius(double radius);
    
    public double getRadius();
    
    public void setCoordinates(double x, double y);
    
    
}
