package nl.defsoftware.mrgb.graphs.models;
import java.util.Collection;
import java.util.Set;

import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap;

/**
 * A graph with a collection of nodes. The nodes should be linked with each other to create the graph. This class is
 * 
 * @author D.L. Ettema
 * 
 */
public class Graph {

    private Collection<Node> nodes;
    private Int2ObjectLinkedOpenHashMap<Node> map;
    private Node source;
    private Node sink;
    private Set<Integer> genomeIds;

    public Graph(Collection<Node> nodes, Int2ObjectLinkedOpenHashMap<Node> map, Node source, Node sink) {
        this.nodes = nodes;
        this.map = map;
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
    
    public Int2ObjectLinkedOpenHashMap<Node> getMap() {
        return map;
    }
}
