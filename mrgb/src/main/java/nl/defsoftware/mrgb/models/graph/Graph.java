package nl.defsoftware.mrgb.models.graph;
import java.util.Collection;
import java.util.Set;

/**
 * A graph with a collection of nodes. The nodes should be linked with each other to create the graph. This class is
 * 
 * @author D.L. Ettema
 * 
 */
public class Graph {

    private Collection<Node> nodes;
    private Node source;
    private Node sink;
    private Set<Integer> genomeIds;

    public Graph(Collection<Node> nodes, Node source, Node sink) {
        this.nodes = nodes;
        this.source = source;
        this.sink = sink;
    }

    public Node getSink() {
        return sink;
    }

    public Node getSource() {
        return source;
    }

    public Collection<Node> getNodeList() {
    	return nodes;
    }
}
