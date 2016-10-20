/**
 * 
 */
package nl.defsoftware.mrgb.view.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.beans.property.DoubleProperty;
import javafx.scene.shape.Shape;
import nl.defsoftware.mrgb.models.graph.Node;

/**
 *
 *
 * @author D.L. Ettema
 * @date 8 Jun 2016
 */
public class GraphModel extends AbstractGraphViewModel implements IGraphViewModel {

    List<Edge> allEdges;
    List<Edge> addedEdges;
    List<Edge> removedEdges;
    
    Map<Integer, Sequence> sequenceMap; // <id,Sequence>
    Map<Integer, List<Integer>> xCoordinateMap; // <xCoordinate, ids[]>

    public GraphModel() {
        super();
        this.clear();
    }

    @Override
    public void clear() {
        super.clear();
        allEdges = new ArrayList<>();
        addedEdges = new ArrayList<>();
        removedEdges = new ArrayList<>();
        
        sequenceMap = new HashMap<Integer, Sequence>(); // <id,Sequence>
        xCoordinateMap = new HashMap<Integer, List<Integer>>();
    }
    
    @Override
    public void merge() {
        super.merge();
        // edges
        allEdges.addAll(addedEdges);
        allEdges.removeAll(removedEdges);
    
        addedEdges.clear();
        removedEdges.clear();
    }
    
    @Override
    public List<Shape> getAllEdges() {
       throw new UnsupportedOperationException("Method not implemented for " + this.getClass().getName());
    }
    
//    @Override
//    public List<Edge> getAllEdges() {
//       return allEdges;
//    }

    private void addEdge(Integer childId, Integer parentId) {
        Sequence sourceSequence = sequenceMap.get(parentId);
        Sequence targetSequence = sequenceMap.get(childId);
        addedEdges.add(new Edge(sourceSequence, targetSequence));
    }
    
    @Override
    public void addEdge(int id, int startX, int startY, int endX, int endY, int rank) {
        throw new UnsupportedOperationException("Method not implemented for " + this.getClass().getName());
    }

    @Override
    public void addEdge(int childId, int parentId, int rank, DoubleProperty scaleYProperty) {
        addEdge(childId, parentId);
    }

    @Override
    public void addEdge(int childId, int parentId, int rank) {
        addEdge(childId, parentId);
    }

    @Override
    public void addSequence(Integer id, int x, int y) {
        Sequence sequence = new Sequence(id);
        sequence.relocate(x, y);
        addSequence(sequence, x);
    }

    @Override
    public void addSequence(Node aNode, int rank, DoubleProperty scaleYProperty) {
        throw new UnsupportedOperationException("Method not implemented for " + this.getClass().getName());
    }

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

    public void clearAddedLists() {
        addedSequences.clear();
        addedEdges.clear();
    }

    public List<Edge> getAddedEdges() {
        return addedEdges;
    }

    public List<Edge> getRemovedEdges() {
        return removedEdges;
    }
}
