/**
 * 
 */
package nl.defsoftware.mrgb.view.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.beans.property.DoubleProperty;
import javafx.scene.control.Label;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Shape;
import nl.defsoftware.mrgb.models.graph.Node;

/**
 *
 *
 * @author D.L. Ettema
 * @date 8 Jun 2016
 */
public class GraphModel {

	Sequence graphParent;

	List<Shape> allSequences;
	List<Shape> addedSequences;
	List<Shape> removedSequences;

	List<Edge> allEdges;
	List<Edge> addedEdges;
	List<Edge> removedEdges;
	
	List<Label> allLabels;

	Map<Integer, Sequence> sequenceMap; // <id,Sequence>
	Map<Integer, List<Integer>> xCoordinateMap; // <xCoordinate, ids[]>

	public GraphModel() {
		graphParent = new Sequence(new Integer(0));
		// clear model, create lists
		clear();
	}

	public void clear() {
		allSequences = new ArrayList<>();
		addedSequences = new ArrayList<>();
		removedSequences = new ArrayList<>();

		allEdges = new ArrayList<>();
		addedEdges = new ArrayList<>();
		removedEdges = new ArrayList<>();
		
		allLabels = new ArrayList<>();

		sequenceMap = new HashMap<Integer, Sequence>(); // <id,Sequence>
		xCoordinateMap = new HashMap<Integer, List<Integer>>();
	}

	public void addLabel(String text, int x, int y, int colour) {
	    Label label = new Label(text);
	    if (colour > 0) {
	        label.setTextFill(Paint.valueOf("red"));
	    }
	    label.setStyle("font-size: 8px;");
	    label.relocate(x, y);
	    allLabels.add(label);
	}
	
	public void addEdge(Integer sourceId, Integer targetId) {
        Sequence sourceSequence = sequenceMap.get(sourceId);
        Sequence targetSequence = sequenceMap.get(targetId);
        Edge edge = new Edge(sourceSequence, targetSequence);

        addedEdges.add(edge);
    }
	
	public void addSequence(Integer id, int x, int y) {
        Sequence sequence = new Sequence(id);
        sequence.relocate(x, y);
        addSequence(sequence, x);
    }
	
	public void addSequence(Node aNode, int rank, DoubleProperty scaleYProperty) {}
    
    private void addSequence(Sequence sequence, int xCoord) {
        addedSequences.add(sequence);
        sequenceMap.put(sequence.getSequenceId(), sequence);
        List<Integer> ids = xCoordinateMap.getOrDefault(xCoord, new ArrayList<Integer>());
        ids.add(sequence.getSequenceId());
        xCoordinateMap.put(xCoord, ids);
    }
    
    public Sequence findSequenceById(Short id) {
        return sequenceMap.get(id);
    }

    public List<Integer> findIdsAtXCoordinate(int xCoordinate) {
        return xCoordinateMap.getOrDefault(xCoordinateMap, new ArrayList<Integer>());
    }
    
    /**
     * Attach all Sequences which don't have a parent to graphParent
     * 
     * @param sequenceList
     */
    public void attachOrphansToGraphParent(List<Sequence> sequenceList) {
        for (Sequence sequence : sequenceList) {
            if (sequence.getSequenceParents().size() == 0) {
                graphParent.addSequenceChild(sequence);
            }
        }
    }

    /**
     * Remove the graphParent reference if it is set
     * 
     * @param sequenceList
     */
    public void disconnectFromGraphParent(List<Sequence> sequenceList) {
        for (Sequence sequence : sequenceList) {
            graphParent.removeSequenceChild(sequence);
        }
    }

    public void merge() {
        // Sequences
        allSequences.addAll(addedSequences);
        allSequences.removeAll(removedSequences);

        addedSequences.clear();
        removedSequences.clear();

        // edges
        allEdges.addAll(addedEdges);
        allEdges.removeAll(removedEdges);

        addedEdges.clear();
        removedEdges.clear();
    }

    /**
     * To be overwritten by subclasses
     */
    public void addEdge(int id, int startX, int startY, int endX, int endY, int rank) {}
    public void addEdge(int childId, int parentId, int rank, DoubleProperty scaleYProperty) {}
    public void addEdge(int childId, int parentId, int rank) {}
    
	public void clearAddedLists() {
		addedSequences.clear();
		addedEdges.clear();
	}

	public List<Shape> getAddedSequences() {
		return addedSequences;
	}

	public List<Shape> getRemovedSequences() {
		return removedSequences;
	}

	public List<Shape> getAllSequences() {
		return allSequences;
	}

	public List<Edge> getAddedEdges() {
		return addedEdges;
	}

	public List<Edge> getRemovedEdges() {
		return removedEdges;
	}

//	public List<Edge> getAllEdges() {
//		return allEdges;
//	}

	public List<Label> getAllLabels() {
	    return allLabels;
	}
}
