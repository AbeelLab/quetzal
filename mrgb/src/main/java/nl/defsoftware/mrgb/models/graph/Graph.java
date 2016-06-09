package nl.defsoftware.mrgb.models.graph;
import java.util.Collection;

import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;

import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import nl.defsoftware.mrgb.view.models.Sequence;

/**
 * A graph with a collection of nodes. The nodes should be linked with each other to create the graph. This class is
 * only a container class for the nodes.
 * 
 */
public class Graph extends DefaultDirectedWeightedGraph<Sequence, DefaultWeightedEdge> {

    protected Collection<aNode> nodes;
    protected Sequence source;
    protected Sequence sink;

    public Graph(Collection<aNode> nodes, Sequence source, Sequence sink) {
    	super(DefaultWeightedEdge.class);
        this.nodes = nodes;
        this.source = source;
        this.sink = sink;
    }

    public Sequence getSink() {
        return sink;
    }

    public Sequence getSource() {
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
