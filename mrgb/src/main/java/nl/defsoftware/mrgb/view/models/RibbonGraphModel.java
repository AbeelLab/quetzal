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
import nl.defsoftware.mrgb.models.Rib;

/**
 * @author D.L. Ettema
 * @date: 27 September 2016
 *
 */
public class RibbonGraphModel extends AbstractGraphViewModel<Shape> {

    private static final int LINE_ENDING_LENGTH = 8;
    private static final int HOR_NODE_SPACING = 20;
    private static final int VER_NODE_SPACING = 25;
    private static final int VER_NODE_BASELINE = 200;
    
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
        
        double DEFAULT_SINGLE_NODE_WIDTH = 15.0;
        double DEFAULT_SINGLE_NODE_HEIGHT = 9.0;
        
        DrawableSequence seq;
        double width = DEFAULT_SINGLE_NODE_WIDTH + Math.exp(drawingData.scale);
        double height = DEFAULT_SINGLE_NODE_HEIGHT + Math.exp(drawingData.scale);
        if (sequenceMap.containsKey(aNode.getNodeId())) {
            seq = sequenceMap.get(aNode.getNodeId());
            
            seq.setWidth(width);
            seq.setHeight(height);
            
//            position: width parentnode/2 + default edge length + width childnode/2
            double x = drawingData.parentXCoordinate + ((HOR_NODE_SPACING + (width/2) + (drawingData.parentWidth/2)) * rank);
            double y = drawingData.parentYCoordinate + (drawingData.parentHeight/2) + (VER_NODE_SPACING * 2) + (height/2);
            
            seq.setLayoutX(x);
            seq.setLayoutY(y);
            ((Rib)aNode).setCoordinates(x, y);
            ((Rib)aNode).setWidth(width);
            ((Rib)aNode).setHeight(height);
        } else {
            drawingData.width = width;
            drawingData.height = height;
            drawingData.xCoordinate = drawingData.xCoordinate + drawingData.parentXCoordinate + (HOR_NODE_SPACING * rank);
            drawingData.yCoordinate = drawingData.yCoordinate + drawingData.parentYCoordinate + DEFAULT_SINGLE_NODE_HEIGHT + (height / 2);
            ((Rib)aNode).setCoordinates(drawingData.xCoordinate, drawingData.yCoordinate);
            ((Rib)aNode).setWidth(width);
            ((Rib)aNode).setHeight(height);
            seq = new DrawableSequence(drawingData);
//            seq.relocate(x, y);
            sequenceMap.put(aNode.getNodeId(), seq);
        }
        super.addedSequences.add(seq);
        
        
        
//        Set<Node> nodesToDraw = new HashSet<>();
//        addSequence(aNode.getNodeId(), drawingData.xCoordinate, drawingData.yCoordinate, drawingData);
//        switch (aNode.getNodeType()) {
//        case SINGLE_NODE:
//            break;
//        case SNP_BUBBLE:
//            break;
//        case INDEL_BUBBLE:
//            break;
//        case ALLELE_BUBBLE:
//            break;
//        default:
//            break;
//        }
    }

    private void addSequence(Integer id, double x, double y, NodeDrawingData drawingData) {
        DrawableSequence seq;
        if (sequenceMap.containsKey(id)) {
            seq = sequenceMap.get(id);
            seq.setWidth(drawingData.width);
            seq.setHeight(drawingData.height);
            seq.setScaleX(drawingData.scale);
            seq.setScaleY(drawingData.scale);
            x = drawingData.xCoordinate - seq.getBoundsInParent().getMinX() + (drawingData.width);
            y = drawingData.yCoordinate - seq.getBoundsInParent().getMinY() + (drawingData.height);
            seq.setLayoutX(x);
            seq.setLayoutY(y);
        } else {
            seq = new DrawableSequence(drawingData);
            seq.relocate(drawingData.xCoordinate, drawingData.yCoordinate);
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
