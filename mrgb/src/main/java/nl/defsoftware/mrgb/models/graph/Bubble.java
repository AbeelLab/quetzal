package nl.defsoftware.mrgb.models.graph;

import java.util.HashSet;
import java.util.Set;

/**
 * The more nestedNodes this bubble will become already visible at a lower zoom level. 
 * The less nestedNodes, this bubble will become visible not untill a higher zoom level.
 * 
 * @author D.L. Ettema
 *
 */
public class Bubble extends AbstractNode implements Node {

    private Set<Node> nestedNodes = new HashSet<>();
    private NodeType nodeType = null; 
    private Node start = null;
    private Node stop = null;
    
    public Bubble(int nodeId, NodeType nodeType, Node start, Node stop) {
        super.setNodeId(nodeId);
        this.start = start;
        this.stop = stop;
        this.nodeType = nodeType;
    }
    
    public Set<Node> getNestedNodes() {
        return nestedNodes;
    }

    public void setNestedNodes(Set<Node> nestedNodes) {
        this.nestedNodes = nestedNodes;
    }

    @Override
    public boolean isComposite() {
        return true;
    }
    
    @Override
    public boolean isNotComposite() {
        return !isComposite();
    }

    @Override
    public NodeType getNodeType() {
      return nodeType;
    }

    public Node getStart() {
        return start;
    }

    public void setStart(Node start) {
        this.start = start;
    }

    public Node getStop() {
        return stop;
    }

    public void setStop(Node stop) {
        this.stop = stop;
    }
}
