package nl.defsoftware.mrgb.view.models;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.beans.property.DoubleProperty;
import javafx.scene.shape.Shape;
import nl.defsoftware.mrgb.Constants;
import nl.defsoftware.mrgb.models.graph.Bubble;
import nl.defsoftware.mrgb.models.graph.Node;

/**
 * 
 * This model draws the nodes on the actual coordinates
 * 
 * @author D.L. Ettema
 * @date: 27 September 2016
 *
 */
public class RibbonGraphModel extends AbstractGraphViewModel<Shape> {

    private static final Logger log = LoggerFactory.getLogger(RibbonGraphModel.class);
    
    private static final int LINE_ENDING_LENGTH = 8;
    private static final int HOR_NODE_SPACING = 20;
    private static final int VER_NODE_SPACING = 20;
    private static final double DEFAULT_SINGLE_NODE_WIDTH = 15.0;
    private static final double DEFAULT_SINGLE_NODE_HEIGHT = 10.0;

    private Set<Shape> allEdges;
    private Set<Shape> addedEdges;
    private Set<Shape> removedEdges;
    private Map<Integer, Shape> drawnSequencesMap;

    public RibbonGraphModel() {
        super();
        this.clear();
    }

    @Override
    public void addSequence(Node node, int rank, NodeDrawingData drawingData) {
        switch (node.getNodeType()) {
        case SINGLE_NODE:
            createSingleSequence(node, rank, drawingData);
            break;
        case SNP_BUBBLE:
            createSNPBubble(node, rank, drawingData);
            break;
        case INDEL_BUBBLE:
            break;
        case ALLELE_BUBBLE:
            createAlleleBubble(node, rank, drawingData);
            break;
        default:
            break;
        }
    }
    
    /**
     * Sets the location of the <code>aNode</code> and takes into account the following:</br>
     * - size between the previous node </br>
     * - adds some spacing between nodes </br> 
     * - scales the default node size </br>
     * - scales the vertical node spacing </br> 
     * 
     *  
     * @param aNode
     * @param rank
     * @param drawingData
     */
    private void createSingleSequence(Node aNode, int rank, NodeDrawingData drawingData) {
        DrawableSequence seq;
        drawingData.width = DEFAULT_SINGLE_NODE_WIDTH / drawingData.scale;
        drawingData.height = DEFAULT_SINGLE_NODE_HEIGHT / drawingData.scale;
        double verNodeSpacing = VER_NODE_SPACING / drawingData.scale;
        double horNodeSpacing = HOR_NODE_SPACING;// / drawingData.scale;
        drawingData.id = aNode.getNodeId();
        if (drawnSequencesMap.containsKey(aNode.getNodeId())) {
            seq = (DrawableSequence) drawnSequencesMap.get(aNode.getNodeId());
            // determine initial offset
            if (Double.compare(drawingData.parentXCoordinate, 0.0) == 0) {
                drawingData.xCoordinate = seq.getLayoutX() + (((drawingData.parentWidth / 2) + horNodeSpacing + (drawingData.width / 2)) * rank);
            } else {
                drawingData.xCoordinate = drawingData.parentXCoordinate + (((drawingData.parentWidth / 2) + horNodeSpacing + (drawingData.width / 2)) * rank);
            }
            // determine location based on node spacing, width or radius of
            // parent node and of itself multiplied by the ranking of the node
            drawingData.yCoordinate = drawingData.yCoordinate + drawingData.parentYCoordinate + (drawingData.parentHeight / 2) + verNodeSpacing + (drawingData.height / 2);

            seq.setWidth(drawingData.width);
            seq.setHeight(drawingData.height);
            seq.setLayoutX(drawingData.xCoordinate);
            seq.setLayoutY(drawingData.yCoordinate);

        } else { //if it is the first node
            drawingData.xCoordinate += drawingData.parentXCoordinate + ((horNodeSpacing + (drawingData.width/2) + (drawingData.parentWidth/2)) * rank);
            drawingData.yCoordinate += drawingData.parentYCoordinate + (drawingData.parentHeight / 2) + verNodeSpacing + (drawingData.height / 2) ;
            seq = new DrawableSequence(drawingData);
            drawnSequencesMap.put(aNode.getNodeId(), seq);
        }
        setDrawingDataToNode(aNode, drawingData);
        super.addedSequences.add(seq);
    }

    private void createAlleleBubble(Node aBubbleNode, int rank, NodeDrawingData drawingData) {
        if ((drawingData.scale * drawingData.scale) / (Constants.MAX_ZOOM_VALUE / 2) > 0.2) {
            Bubble bubble = (Bubble) aBubbleNode;
            //Setting Width and XCoordinate
            double minX = Double.MAX_VALUE; 
            double maxX = Double.MIN_VALUE;
            for (Node node : bubble.getNestedNodes()) {
                if (drawnSequencesMap.containsKey(node.getNodeId())) {
                    Shape shape = drawnSequencesMap.get(node.getNodeId());
                    minX = Math.min(minX, shape.getBoundsInParent().getMinX());
                    maxX = Math.max(maxX, shape.getBoundsInParent().getMaxX());
                }
            }
            drawingData.width = (maxX - minX);
            drawingData.xCoordinate = minX;
            //Setting height and yCoordinate
            if (drawnSequencesMap.containsKey(bubble.getStart().getNodeId()) && drawnSequencesMap.containsKey(bubble.getStop().getNodeId())) {
                DrawableSequence startSequence = (DrawableSequence) drawnSequencesMap.get(bubble.getStart().getNodeId());
                DrawableSequence stopSequence = (DrawableSequence) drawnSequencesMap.get(bubble.getStop().getNodeId());
                drawingData.height = stopSequence.getBoundsInParent().getMaxY() - startSequence.getBoundsInParent().getMinY();        
                drawingData.yCoordinate = startSequence.getBoundsInParent().getMinY();
            }
            DrawableAlleleBubble drawableBubble = new DrawableAlleleBubble(drawingData);
            setDrawingDataToNode(aBubbleNode, drawingData);
            super.addedSequences.add(drawableBubble);
            
            for (Node nestedNode : bubble.getNestedNodes()) {
                super.removedSequences.add(drawnSequencesMap.get(nestedNode.getNodeId()));
            }
            super.removedSequences.add(drawnSequencesMap.get(bubble.getStart().getNodeId()));
            super.removedSequences.add(drawnSequencesMap.get(bubble.getStop().getNodeId()));
        }
    }

    private void setDrawingDataToNode(Node aNode, NodeDrawingData data) {
        aNode.setCoordinates(data.xCoordinate, data.yCoordinate);
        aNode.setWidth(data.width);
        aNode.setHeight(data.height);
        aNode.setRadius(data.radius);
    }

    private void createSNPBubble(Node aNode, int rank, NodeDrawingData drawingData) {
        DrawableSNPBubble snp;
        if (drawnSequencesMap.containsKey(aNode.getNodeId())) {
            snp = (DrawableSNPBubble) drawnSequencesMap.get(aNode.getNodeId());
            // determine initial offset
            if (drawingData.parentXCoordinate == 0.0) {
                drawingData.xCoordinate = snp.getLayoutX();
            } else {
                drawingData.xCoordinate = drawingData.parentXCoordinate;
            }
            // determine location based on node spacing, width or radius of
            // parent node and of itself multiplied by the ranking of the node
            drawingData.xCoordinate += (((drawingData.parentWidth / 2) + HOR_NODE_SPACING + (drawingData.width / 2)) * rank);
            drawingData.yCoordinate = drawingData.yCoordinate + drawingData.parentYCoordinate + Math.max(drawingData.parentHeight / 2, drawingData.radius) + (VER_NODE_SPACING);

            snp.setLayoutX(drawingData.xCoordinate);
            snp.setLayoutY(drawingData.yCoordinate);

        } else {
            drawingData.xCoordinate = drawingData.xCoordinate + drawingData.parentXCoordinate + (((drawingData.parentWidth / 2) + HOR_NODE_SPACING + (drawingData.width / 2)) * rank);
            drawingData.yCoordinate = drawingData.yCoordinate + drawingData.parentYCoordinate + VER_NODE_SPACING;
            snp = new DrawableSNPBubble();
            snp.setLayoutX(drawingData.xCoordinate);
            snp.setLayoutY(drawingData.yCoordinate);

            drawnSequencesMap.put(aNode.getNodeId(), snp);
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
            DrawableEdgeCurve ribbon = new DrawableEdgeCurve(rank, startX, startY, endX, endY,
                    isOpeningCurve(startX, endX));
            addedEdges.add(ribbon);
        }
    }

    @Override
    public void addEdge(int childId, int parentId, int rank) {
        Shape from = drawnSequencesMap.get(parentId);
        Shape to = drawnSequencesMap.get(childId);
        if (from == null ) {
            System.out.println("Trying to add edge from null parentID: " + parentId + ". With childId: " + childId);
        } else if (to == null) {
            System.out.println("Trying to add edge from null childID: " + childId);
        } else {
            addedEdges.add(new DrawableEdge(from, to));
        }
    }

    @Override
    public void addEdge(int childId, int parentId, int rank, DoubleProperty scaleYProperty) {
        throw new UnsupportedOperationException("Method not implemented for " + this.getClass().getName());
    }

    @Override
    public void clear() {
        super.clear();
        drawnSequencesMap = new HashMap<>();
        allEdges = new HashSet<>();
        removedEdges = new HashSet<>();
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
    public Set<Shape> getRemovedEdges() {
        return removedEdges;
    }
}
