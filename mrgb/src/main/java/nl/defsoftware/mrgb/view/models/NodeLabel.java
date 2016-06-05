package nl.defsoftware.mrgb.view.models;
import java.util.ArrayList;
import java.util.Collection;

import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;

public class NodeLabel extends Circle {

	private String label;
	private short id;
	private Collection<Edge> edges;

	public NodeLabel(String label, double centerX, double centerY, double radius, Paint fill) {
    setCenterX(centerX);
    setCenterY(centerY);
    setRadius(radius);
    setFill(fill);
		this.label = label;
		this.edges = new ArrayList<Edge>();
	}

	public void addEdge(NodeLabel to, int capacity) {
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
	public void addEdge(NodeLabel to, int lower, int capacity) {
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

	public short getNodeLabelId() {
		return id;
	}

	public void setNodeLabelId(short id) {
		this.id = id;
	}

	public String toString() {
		return label;
	}
}
