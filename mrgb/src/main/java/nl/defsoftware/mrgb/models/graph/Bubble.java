package nl.defsoftware.mrgb.models.graph;

import java.util.HashSet;
import java.util.Set;

/**
 * @author D.L. Ettema
 *
 */
public class Bubble extends AbstractNode implements Node {

    private Set<Node> nestedNodes = new HashSet<>();
    private NodeType nodeType = null; 
    
    public Bubble(int nodeId, NodeType nodeType) {
        super.setNodeId(nodeId);
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
}
