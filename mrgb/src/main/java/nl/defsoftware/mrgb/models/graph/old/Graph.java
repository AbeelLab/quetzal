package nl.defsoftware.mrgb.models.graph.old;
import java.util.Collection;

import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;

/**
 * A graph with a collection of nodes. The nodes should be linked with each other to create the graph. This class is
 * only a container class for the nodes.
 * 
 */
public class Graph {

    protected Collection<Node> nodes;
    protected Node source;
    protected Node sink;

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

}
