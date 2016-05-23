package nl.defsoftware.mrgb.models.graph;
import java.util.ArrayList;
import java.util.Collection;

public class Node {

    protected String label;
    protected Collection<Edge> edges;

    public Node(String label) {
        this.label = label;
        this.edges = new ArrayList<Edge>();
    }

    public void addEdge(Node to, int capacity) {
        Edge e = new Edge(capacity, this, to);
        edges.add(e);
    }

    /**
     * add edge with a lower bound and an upper bound for its capacity.
     * 
     * @param to
     * @param lower
     * @param capacity
     */
    public void addEdge(Node to, int lower, int capacity) {
        Edge e = new Edge(lower, capacity, this, to);
        edges.add(e);
    }

    public Collection<Edge> getEdges() {
        return edges;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

}
