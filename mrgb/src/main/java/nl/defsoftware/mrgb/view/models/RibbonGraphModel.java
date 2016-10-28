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
    private static final int HOR_NODE_SPACING = 20;
    private static final int VER_NODE_SPACING = 25;
    private static final int VER_NODE_BASELINE = 200;
    private static final double DEFAULT_SINGLE_NODE_WIDTH = 15.0;
    private static final double DEFAULT_SINGLE_NODE_HEIGHT = 10.0;
    private static final double DEFAULT_SINGLE_NODE_RADIUS = 10.0;
    
    private Set<Shape> allEdges;
    private Set<Shape> addedEdges;
    private List<Shape> removedEdges;
    private Map<Integer, Shape> sequenceMap;
    
    private List<Integer> subGraphNodesAllowedToDraw = new ArrayList<>();

    public RibbonGraphModel() {
        super();
        this.clear();
    }

    private void createSingleSequence(Node aNode, int rank, NodeDrawingData drawingData) {
        DrawableSequence seq;
        drawingData.width = DEFAULT_SINGLE_NODE_WIDTH;
        drawingData.height = DEFAULT_SINGLE_NODE_HEIGHT;
        if (sequenceMap.containsKey(aNode.getNodeId())) {
            seq = (DrawableSequence)sequenceMap.get(aNode.getNodeId());
            
            if (drawingData.parentXCoordinate == 0.0) {
                drawingData.xCoordinate = seq.getLayoutX() + ((HOR_NODE_SPACING + (drawingData.width/2) + (Math.max(drawingData.parentWidth, drawingData.radius)/2)) * rank);
            } else {
                drawingData.xCoordinate = drawingData.parentXCoordinate + ((HOR_NODE_SPACING + (drawingData.width/2) + (Math.max(drawingData.parentWidth, drawingData.radius)/2)) * rank);
            }
            drawingData.yCoordinate = drawingData.parentYCoordinate + (Math.max(drawingData.parentHeight, drawingData.radius)/2) + (VER_NODE_SPACING) + (drawingData.height/2);
            
            seq.setLayoutX(drawingData.xCoordinate);
            seq.setLayoutY(drawingData.yCoordinate);
            
        } else {
            drawingData.xCoordinate = drawingData.xCoordinate + drawingData.parentXCoordinate + (HOR_NODE_SPACING * rank);
            drawingData.yCoordinate = drawingData.yCoordinate + drawingData.parentYCoordinate + VER_NODE_SPACING + (drawingData.parentHeight / 2);
            
            seq = new DrawableSequence(drawingData);
            seq.setLayoutX(drawingData.xCoordinate);
            seq.setLayoutY(drawingData.yCoordinate);
            seq.setWidth(drawingData.width);
            seq.setHeight(drawingData.height);
            sequenceMap.put(aNode.getNodeId(), seq);
        }
        setDrawingDataToNode(aNode, drawingData);
        super.addedSequences.add(seq);
    }

    private void createAlleleBubble(Node aNode, int rank, NodeDrawingData drawingData) {
        DrawableAlleleBubble bubble;
        drawingData.radius = DEFAULT_SINGLE_NODE_RADIUS + Math.exp(drawingData.scale);
        if (sequenceMap.containsKey(aNode.getNodeId())) {
            bubble = (DrawableAlleleBubble)sequenceMap.get(aNode.getNodeId());
            if (drawingData.parentXCoordinate == 0.0) {
                drawingData.xCoordinate = bubble.getLayoutX() + ((HOR_NODE_SPACING + (drawingData.radius) + (Math.max(drawingData.parentWidth/2, drawingData.radius))) * rank);
            } else {
                drawingData.xCoordinate = drawingData.parentXCoordinate + ((HOR_NODE_SPACING + (drawingData.radius) + (Math.max(drawingData.parentWidth/2, drawingData.radius))) * rank);
            }
            drawingData.yCoordinate = drawingData.parentYCoordinate + Math.max(drawingData.parentHeight/2, drawingData.radius) + (VER_NODE_SPACING) + (drawingData.radius);
            
            bubble.setRadius(drawingData.radius);
            bubble.setLayoutX(drawingData.xCoordinate);
            bubble.setLayoutY(drawingData.yCoordinate);
            
        } else {
            drawingData.xCoordinate = drawingData.xCoordinate + drawingData.parentXCoordinate + (HOR_NODE_SPACING * rank);
            drawingData.yCoordinate = drawingData.yCoordinate + drawingData.parentYCoordinate + VER_NODE_SPACING + (drawingData.radius);
            bubble = new DrawableAlleleBubble();
            bubble.setRadius(drawingData.radius);
            bubble.setLayoutX(drawingData.xCoordinate);
            bubble.setLayoutY(drawingData.yCoordinate);
            
            sequenceMap.put(aNode.getNodeId(), bubble);
        }
        setDrawingDataToNode(aNode, drawingData);
        super.addedSequences.add(bubble);
    }
    
    
    private void setDrawingDataToNode(Node aNode, NodeDrawingData data) {
        aNode.setCoordinates(data.xCoordinate, data.yCoordinate);
        aNode.setWidth(data.width);
        aNode.setHeight(data.height);
        aNode.setRadius(data.radius);
    }
    
    @Override
    public void addSequence(Node node, int rank, NodeDrawingData drawingData) {
      Set<Node> nodesToDraw = new HashSet<>();
      switch (node.getNodeType()) {
      case SINGLE_NODE:
//          createSingleSequence(node, rank, drawingData);
          createAlleleBubble(node, rank, drawingData);
          break;
      case SNP_BUBBLE:
          createSNPBubble(node, rank, drawingData);
          break;
      case INDEL_BUBBLE:
          break;
      case ALLELE_BUBBLE:
          break;
      default:
          break;
      }
    }
    
    private void createSNPBubble(Node aNode, int rank, NodeDrawingData drawingData) {
        DrawableSNPBubble snp;
        if (sequenceMap.containsKey(aNode.getNodeId())) {
            snp = (DrawableSNPBubble) sequenceMap.get(aNode.getNodeId());
            if (drawingData.parentXCoordinate == 0.0) {
                drawingData.xCoordinate = snp.getLayoutX() + ((HOR_NODE_SPACING + (drawingData.radius) + drawingData.parentWidth/2) * rank);
            } else {
                drawingData.xCoordinate = drawingData.parentXCoordinate + ((HOR_NODE_SPACING + (drawingData.radius) + drawingData.parentWidth/2) * rank);
            }
            drawingData.yCoordinate = drawingData.parentYCoordinate + Math.max(drawingData.parentHeight/2, drawingData.radius) + (VER_NODE_SPACING);
            
            snp.setLayoutX(drawingData.xCoordinate);
            snp.setLayoutY(drawingData.yCoordinate);
            
        } else {
            drawingData.xCoordinate = drawingData.xCoordinate + drawingData.parentXCoordinate + (HOR_NODE_SPACING * rank);
            drawingData.yCoordinate = drawingData.yCoordinate + drawingData.parentYCoordinate + VER_NODE_SPACING;
            snp = new DrawableSNPBubble();
            snp.setLayoutX(drawingData.xCoordinate);
            snp.setLayoutY(drawingData.yCoordinate);
            
            sequenceMap.put(aNode.getNodeId(), snp);
        }
        setDrawingDataToNode(aNode, drawingData);
        super.addedSequences.add(snp);
    }

    private int determineLength(int start, int end) {
        return end - start - LINE_ENDING_LENGTH;
    }

    private boolean isOpeningCurve(int startX, int endX) {
        return endX - startX > 0;
    }
    
    @Override
    public void addSequence(Integer id, int x, int y) {
        throw new UnsupportedOperationException("addSequence method not implemented");
    }

    @Override
    public void addEdge(int id, int startX, int startY, int endX, int endY, int rank) {
        if (rank == 0) { // backbone part
            DrawableEdge ribbonLine = new DrawableEdge(0, determineLength(startY, endY));
            ribbonLine.relocate(startX, startY);
            addedEdges.add(ribbonLine);
        } else {
            DrawableEdgeCurve ribbon = new DrawableEdgeCurve(rank, startX, startY, endX, endY, isOpeningCurve(startX, endX));
            addedEdges.add(ribbon);
        }
    }

    @Override
    public void addEdge(int childId, int parentId, int rank) {
        Shape from = sequenceMap.get(parentId);
        Shape to = sequenceMap.get(childId);
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
