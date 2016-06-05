package nl.defsoftware.mrgb.view.models;
import java.util.Collection;

import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;

import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

/**
 * A graph with a collection of nodes. The nodes should be linked with each other to create the graph. This class is
 * only a container class for the nodes.
 * 
 */
public class Graph extends DefaultDirectedWeightedGraph<NodeLabel, DefaultWeightedEdge> {

    protected Collection<aNode> nodes;
    protected NodeLabel source;
    protected NodeLabel sink;

    public Graph(Collection<aNode> nodes, NodeLabel source, NodeLabel sink) {
    	super(DefaultWeightedEdge.class);
        this.nodes = nodes;
        this.source = source;
        this.sink = sink;
        
    }

    public NodeLabel getSink() {
        return sink;
    }

    public NodeLabel getSource() {
        return source;
    }

    public Collection<aNode> getNodeList() {
    	return nodes;
    }
    
    public class aNode extends Circle {
    	
    }
    
    public class anEdge extends Line {
    	
    }

}
