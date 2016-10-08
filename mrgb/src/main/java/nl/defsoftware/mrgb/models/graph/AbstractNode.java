package nl.defsoftware.mrgb.models.graph;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * @author D.L. Ettema
 *
 */
public abstract class AbstractNode implements Node {

    private Set<Node> inEdges;
    private Set<Node> outEdges;
    
    protected AbstractNode() {
        this.inEdges = new HashSet<>();
        this.outEdges = new HashSet<>();
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
}
