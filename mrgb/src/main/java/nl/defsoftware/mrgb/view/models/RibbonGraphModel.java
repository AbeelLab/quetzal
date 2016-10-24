package nl.defsoftware.mrgb.view.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javafx.beans.property.DoubleProperty;
import javafx.scene.shape.Shape;
import nl.defsoftware.mrgb.models.graph.Node;

/**
 * @author D.L. Ettema
 * @date: 27 September 2016
 *
 */
public class RibbonGraphModel extends AbstractGraphViewModel<Shape> {

    private static final int LINE_ENDING_LENGTH = 8;

    private Set<Shape> allEdges;
    private Set<Shape> addedEdges;
    private List<Shape> removedEdges;
    private Map<Integer, DrawableSequence> sequenceMap;

    public RibbonGraphModel() {
        super();
        this.clear();
    }

    /**
     * @param aNode
     * @param parentNode
     * @param rank
     */
    @Override
    public void addSequence(Node aNode, int rank, NodeDrawingData drawingData) {
        Set<Node> nodesToDraw = new HashSet<>();
        addSequence(aNode.getNodeId(), drawingData.xCoordinate, drawingData.yCoordinate, drawingData);
        switch (aNode.getNodeType()) {
        case SINGLE_NODE:
            break;
        case SNP_BUBBLE:
            break;
        case INDEL_BUBBLE:
            break;
        case ALLELE_BUBBLE:
            break;
        default:
            break;
        }
    }

    private void addSequence(Integer id, double x, double y, NodeDrawingData drawingData) {
        DrawableSequence seq;
        if (sequenceMap.containsKey(id)) {
            seq = sequenceMap.get(id);
            seq.setHeight(drawingData.height);
            seq.setWidth(drawingData.width);
            seq.setLayoutX(drawingData.xCoordinate);
            seq.setLayoutY(drawingData.yCoordinate);
//            seq.setLayoutX(drawingData.xCoordinate - seq.getBoundsInLocal().getMinX());
//            seq.setLayoutY(drawingData.yCoordinate - seq.getBoundsInLocal().getMinY());
//            seq.relocate(drawingData.xCoordinate, drawingData.yCoordinate);
            seq.setScaleX(drawingData.scale);
            seq.setScaleY(drawingData.scale);
        } else {
            seq = new DrawableSequence(drawingData);
//            seq.relocate(drawingData.xCoordinate, drawingData.yCoordinate);
            sequenceMap.put(id, seq);
        }
        super.addedSequences.add(seq);
    }
    
    private int determineLength(int start, int end) {
        return end - start - LINE_ENDING_LENGTH;
    }

    private boolean isOpeningCurve(int startX, int endX) {
        return endX - startX > 0;
    }
    
    @Override
    public void addSequence(Integer id, int x, int y) {
        addSequence(id, x, y, null);
    }

    @Override
    public void addEdge(int id, int startX, int startY, int endX, int endY, int rank) {
        if (rank == 0) { // backbone part
            DrawableEdge ribbonLine = new DrawableEdge(0, determineLength(startY, endY));
            ribbonLine.relocate(startX, startY);
            addedEdges.add(ribbonLine);
        } else {
            DrawableEdgeCurve ribbon = new DrawableEdgeCurve(rank, startX, startY, endX, endY,
                    isOpeningCurve(startX, endX));
            addedEdges.add(ribbon);
        }
    }

    @Override
    public void addEdge(int childId, int parentId, int rank) {
        DrawableSequence from = sequenceMap.get(parentId);
        DrawableSequence to = sequenceMap.get(childId);
        addedEdges.add(new DrawableEdge(from, to));
    }

    @Override
    public void addEdge(int childId, int parentId, int rank, DoubleProperty scaleYProperty) {
        throw new UnsupportedOperationException("Method not implemented for " + this.getClass().getName());
    }

    @Override
    public void clear() {
        super.clear();
        sequenceMap = new HashMap<>();
        allEdges = new HashSet<>();
        removedEdges = new ArrayList<>();
        addedEdges = new HashSet<>();
    }

    @Override
    public Set<Shape> getAllEdges() {
        return this.allEdges;
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
    public Set<Shape> getAddedEdges() {
        return addedEdges;
    }

    @Override
    public List<Shape> getRemovedEdges() {
        return removedEdges;
    }
}
