package nl.defsoftware.mrgb.view.models;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javafx.beans.property.DoubleProperty;
import javafx.scene.shape.Shape;
import nl.defsoftware.mrgb.models.graph.Bubble;
import nl.defsoftware.mrgb.models.graph.Node;
import nl.defsoftware.mrgb.models.graph.NodeType;

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
    private Map<Integer, Shape> drawnSequencesMap;

    /*<node id first child in bubble, Collection of incoming edges from nodes of that bubble>*/
    private Map<Integer, Collection<Node>> drawableNodes = new HashMap<>();
    private Map<Integer, Collection<Node>> nonDrawableNodes = new HashMap<>();

    public RibbonGraphModel() {
        super();
        this.clear();
    }

    private void createSingleSequence(Node aNode, int rank, NodeDrawingData drawingData) {
        DrawableSequence seq;
        drawingData.width = DEFAULT_SINGLE_NODE_WIDTH;
        drawingData.height = DEFAULT_SINGLE_NODE_HEIGHT;
        if (drawnSequencesMap.containsKey(aNode.getNodeId())) {
            seq = (DrawableSequence) drawnSequencesMap.get(aNode.getNodeId());
            // determine initial offset
            if (drawingData.parentXCoordinate == 0.0) {
                drawingData.xCoordinate = seq.getLayoutX();
            } else {
                drawingData.xCoordinate = drawingData.parentXCoordinate;
            }
            // determine location based on node spacing, width or radius of
            // parent node and of itself multiplied by the ranking of the node
            drawingData.xCoordinate += ((Math.max(drawingData.parentWidth / 2, drawingData.radius) + HOR_NODE_SPACING + (drawingData.width / 2)) * rank);
            drawingData.yCoordinate = drawingData.parentYCoordinate + Math.max(drawingData.parentHeight / 2, drawingData.radius) + (VER_NODE_SPACING) + (drawingData.height / 2);

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
            drawnSequencesMap.put(aNode.getNodeId(), seq);
        }
        setDrawingDataToNode(aNode, drawingData);
        super.addedSequences.add(seq);
    }

    private void createAlleleBubble(Node aNode, int rank, NodeDrawingData drawingData) {
        drawingData.height = determineBubbleHeight((Bubble) aNode, drawingData.scale);
        drawingData.width = DEFAULT_SINGLE_NODE_WIDTH;
        addSubGraphSequences((Bubble) aNode, rank, drawingData);
        DrawableAlleleBubble bubble;
        if (drawnSequencesMap.containsKey(aNode.getNodeId())) {
            bubble = (DrawableAlleleBubble) drawnSequencesMap.get(aNode.getNodeId());
            // determine initial offset
            if (drawingData.parentXCoordinate == 0.0) {
                drawingData.xCoordinate = bubble.getLayoutX();
            } else {
                drawingData.xCoordinate = drawingData.parentXCoordinate;
            }
            // determine location based on node spacing, width or radius of
            // parent node and of itself multiplied by the ranking of the node
            drawingData.xCoordinate += ((Math.max(drawingData.parentWidth / 2, drawingData.radius) + HOR_NODE_SPACING + (drawingData.width / 2)) * rank);
            drawingData.yCoordinate = drawingData.parentYCoordinate + Math.max(drawingData.parentHeight / 2, drawingData.radius) + (VER_NODE_SPACING) + (drawingData.height/2);

            bubble.setWidth(drawingData.width);
            bubble.setHeight(drawingData.height);
            bubble.setLayoutX(drawingData.xCoordinate);
            bubble.setLayoutY(drawingData.yCoordinate);
        } else {
            drawingData.xCoordinate = drawingData.xCoordinate + drawingData.parentXCoordinate + (HOR_NODE_SPACING * rank);
            drawingData.yCoordinate = drawingData.yCoordinate + drawingData.parentYCoordinate + VER_NODE_SPACING + (drawingData.radius);
            bubble = new DrawableAlleleBubble();
            bubble.setWidth(drawingData.width);
            bubble.setHeight(drawingData.height);
            bubble.setLayoutX(drawingData.xCoordinate);
            bubble.setLayoutY(drawingData.yCoordinate);

            drawnSequencesMap.put(aNode.getNodeId(), bubble);
        }
        setDrawingDataToNode(aNode, drawingData);
        super.addedSequences.add(bubble);
    }

    private void addSubGraphSequences(Bubble bubble, int rank, NodeDrawingData drawingData) {
        if (drawingData.height > 100) {
            //TODO sort them on nodeID
            NodeDrawingData bubbleDrawingData = drawingData;
            for (Node node : bubble.getNestedNodes()) {
                addSequence(node, rank, bubbleDrawingData);
                bubbleDrawingData.parentXCoordinate = bubbleDrawingData.xCoordinate;
                bubbleDrawingData.parentYCoordinate = bubbleDrawingData.yCoordinate;
                bubbleDrawingData.parentWidth = bubbleDrawingData.width;
                bubbleDrawingData.parentHeight = bubbleDrawingData.height;
                bubbleDrawingData.parentRadius = bubbleDrawingData.radius;
            }
        }
    }

    /**
     * //#(scale, nestedsize) = radius 
     * //higher nestedSize, low scale = viewable radius 
     * //higher nestedSize, high scale = bubble vanishes 
     * //lower nestedSize, low scale = not viewable 
     * //lower nestedSize, high scale = viewable radius
     * We need some sort of staffel mechanism?
     * 
     * @param aNode
     * @param scale
     * @return
     */
    int MINUMUM_NODE_SIZE = 10;
    private double determineBubbleHeight(Bubble bubble, double scale) {
        double width = bubble.getNestedNodes().size() * scale;
        if (bubble.getNestedNodes().size() < MINUMUM_NODE_SIZE && width < DEFAULT_SINGLE_NODE_WIDTH) {
            width = MINUMUM_NODE_SIZE * scale;
            if (width > DEFAULT_SINGLE_NODE_WIDTH) {
                return DEFAULT_SINGLE_NODE_WIDTH;
            }
        }
        return width;
//        if (isBetween(scale, 1, 5)) {
//            if (bubble.getNestedNodes().size() > 100) return DEFAULT_SINGLE_NODE_RADIUS * scale;
//            else return 0;
//        }
//        else if (isBetween(scale, 5, 7)) {
//            if (bubble.getNestedNodes().size() > 100) return DEFAULT_SINGLE_NODE_RADIUS * Math.exp(scale);
//            else if (bubble.getNestedNodes().size() > 50) return DEFAULT_SINGLE_NODE_RADIUS * scale;
//            else if (bubble.getNestedNodes().size() > 0) return DEFAULT_SINGLE_NODE_RADIUS;
//        }
//        return 0;
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
            drawingData.xCoordinate += ((HOR_NODE_SPACING + (drawingData.radius) + drawingData.parentWidth / 2) * rank);
            drawingData.yCoordinate = drawingData.parentYCoordinate + Math.max(drawingData.parentHeight / 2, drawingData.radius) + (VER_NODE_SPACING);

            snp.setLayoutX(drawingData.xCoordinate);
            snp.setLayoutY(drawingData.yCoordinate);

        } else {
            drawingData.xCoordinate = drawingData.xCoordinate + drawingData.parentXCoordinate
                    + (HOR_NODE_SPACING * rank);
            drawingData.yCoordinate = drawingData.yCoordinate + drawingData.parentYCoordinate + VER_NODE_SPACING;
            snp = new DrawableSNPBubble();
            snp.setLayoutX(drawingData.xCoordinate);
            snp.setLayoutY(drawingData.yCoordinate);

            drawnSequencesMap.put(aNode.getNodeId(), snp);
        }
        setDrawingDataToNode(aNode, drawingData);
        super.addedSequences.add(snp);
    }

    @Override
    public void addSequence(Node node, int rank, NodeDrawingData drawingData) {
        switch (node.getNodeType()) {
        case SINGLE_NODE:
            if (!drawnSequencesMap.containsKey(node.getNodeId())) { 
                // createSingleSequence(node, rank, drawingData);
                createAlleleBubble(node, rank, drawingData);
            }
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
        addedEdges.add(new DrawableEdge(from, to));
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
