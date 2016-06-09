package nl.defsoftware.mrgb.view.models;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

/**
 * This sequence holds amongst others the sequence of the DNA, plus it may present other nodes to inform the user of 
 * the nature of this sequence.
 *
 *
 * @author D.L. Ettema
 * @date 7 Jun 2016
 */
public class Sequence extends Circle {

	private Short id;
	private Collection<Edge> edges;
	private List<Sequence> childSequences = new ArrayList<Sequence>();
	private List<Sequence> parentSequences = new ArrayList<Sequence>();
	
	public Sequence(Short id) {
		this.id = id;
		this.edges = new ArrayList<Edge>();
		this.setRadius(10.0);
		this.setFill(Color.BLACK);
		this.setOpacity(0.6);
	}

	public void addEdge(Sequence to, int capacity) {
		Edge e = new Edge(capacity, this, to);
		edges.add(e);
	}

	public void addEdge(Sequence to, int lower, int capacity) {
		Edge e = new Edge(lower, capacity, this, to);
		edges.add(e);
	}

	public Collection<Edge> getEdges() {
		return edges;
	}

	public Short getSequenceId() {
		return id;
	}

	public void setSequenceId(Short id) {
		this.id = id;
	}

	public void addSequenceChild(Sequence to) {
		childSequences.add(to);		
	}

	public void addSequenceParent(Sequence from) {
		parentSequences.add(from);
	}
	
	public List<Sequence> getSequenceChildren() {
		return childSequences;
	}
	
	public List<Sequence> getSequenceParents() {
		return parentSequences;
	}
	
	public void removeSequenceChild(Sequence sequence) {
		childSequences.remove(sequence);
	}
}
