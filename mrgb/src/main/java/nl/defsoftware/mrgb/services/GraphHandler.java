/**
 * 
 */
package nl.defsoftware.mrgb.services;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.shorts.Short2ObjectOpenHashMap;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import nl.defsoftware.mrgb.models.graph.Bubble;
import nl.defsoftware.mrgb.models.graph.Node;
import nl.defsoftware.mrgb.models.graph.NodeType;
import nl.defsoftware.mrgb.view.controllers.MatchingScoreEntry;
import nl.defsoftware.mrgb.view.models.IGraphViewModel;
import nl.defsoftware.mrgb.view.models.NodeDrawingData;

/**
 * This GraphHandler object is responsible for filling the graph model from
 * parsed data with the nodes and placing them in a local coordinate system.
 * 
 * @author D.L. Ettema
 *
 */
public class GraphHandler {

    private static final Logger log = LoggerFactory.getLogger(GraphHandler.class);

    private static final int HOR_NODE_SPACING = 20;
    private static final int VER_NODE_SPACING = 25;
    private static final int VER_NODE_BASELINE = 200;

    private static final int BACKBONE_X_BASELINE = 100;
    private static final int BACKBONE_Y_BASELINE = 25;

    private static final int Y_CORRECTION = 5;

    private Int2ObjectLinkedOpenHashMap<Node> graphData;
    private Short2ObjectOpenHashMap<String> genomeNamesMap;
    private Int2ObjectLinkedOpenHashMap<Bubble> bubbles;
    private Set<Integer> drawnSubGraphNodeIds;

    private Int2ObjectOpenHashMap<int[]> edgeMapping = new Int2ObjectOpenHashMap<>();

    private DoubleProperty zoomFactor = new SimpleDoubleProperty(1.0);
    private double drawingStartCoordinate = 0;
    private double drawingRange = 0;

    /**
     * Given the two data-structures this Handler will input these into the
     * model and the model will determine the layout
     * 
     * @param graphMap
     * @param genomeNamesMap
     */
    public GraphHandler(Int2ObjectLinkedOpenHashMap<Node> graphData, Short2ObjectOpenHashMap<String> genomeNamesMap, Int2ObjectLinkedOpenHashMap<Bubble> bubbles) {
        this.graphData = graphData;
        this.genomeNamesMap = genomeNamesMap;
        this.bubbles = bubbles;
        this.drawnSubGraphNodeIds = new HashSet<>();
    }

    /**
     * 
     * 
     * @param edgeCanvas
     * @param model
     * @param drawingStartCoordinate: where to start drawing.
     * @param drawingRange: the range of drawing in total which can be bigger then the actual window view
     * @param zoomFactor
     */
    public void loadGraphViewModel(IGraphViewModel model, double drawingStartCoordinate, double drawingRange, DoubleProperty zoomFactor) {
        log.info("Zoomfactor: " + zoomFactor.get());
        this.zoomFactor = zoomFactor;
        this.drawingStartCoordinate = drawingStartCoordinate;
        this.drawingRange = drawingRange;

        int sourceNodeId = drawSourceNode(model, zoomFactor);

        // for (int i = (sourceNode + 1); i < 500; i++) { //testing purposes
        for (int i = (sourceNodeId  + 1); i < graphData.size(); i++) {
            if (graphData.containsKey(i)) {
            
                Node aNode = graphData.get(i);
//                Node aNode = getNodeOrBubble(i);
//                if (!drawnSubGraphNodeIds.contains(aNode.getNodeId())) {
                    GraphHandlerUtil.mapEdges(edgeMapping, aNode.getNodeId(), getConnectedEdges(aNode));
                    drawGreedy(model, aNode);
//                }
            }
        }
    }

    /**
     * @param model
     * @param zoomFactor
     * @return
     */
    private int drawSourceNode(IGraphViewModel model, DoubleProperty zoomFactor) {
        Node firstNode = getNodeOrBubble(graphData.firstIntKey());

        GraphHandlerUtil.mapEdges(edgeMapping, firstNode.getNodeId(), getConnectedEdges(firstNode));
        NodeDrawingData drawingData = new NodeDrawingData();
        drawingData.xCoordinate = BACKBONE_X_BASELINE;
        drawingData.yCoordinate = BACKBONE_Y_BASELINE;
        drawingData.width = 0;
        drawingData.height = 0;
        drawingData.scale = Math.max(zoomFactor.get(), 1.0);
        drawSequence(model, firstNode, drawingData, 0);
        return firstNode.getNodeId();
    }
    
    private int[] getConnectedEdges(Node node) {
        if (NodeType.SINGLE_NODE.equals(node.getNodeType())) {
            return node.getConnectedEdges();
        } else {
            Node stop = ((Bubble)node).getStop();
            return getConnectedEdges(stop);
        }
    }

    private Node getNodeOrBubble(int sourceNode) {
        if (bubbles.containsKey(sourceNode)) {
//            log.info("bubbleID({})", sourceNode);
            Bubble bubble = bubbles.get(sourceNode);
            drawnSubGraphNodeIds.add(bubble.getStop().getNodeId());
            for (Node node : bubble.getNestedNodes()) {
                drawnSubGraphNodeIds.add(node.getNodeId());
            }
            return bubble;
        } else {
            return graphData.get(sourceNode);
        }
    }

    /**
     * <p>
     * This method will determine any parent nodes previously stored in the
     * 'addEdgesToQueue' in a previous iteration and draw these edges with
     * respect to the location of the parent node and current node
     * 
     * insertion: 5-6 = 4; 5-7 = 5 ; 6-7 = 4;
     * </p>
     * 
     * @param model
     *            the data model to be filled
     * @param graphMap
     *            map containing all the nodes in the graph
     * @param aNode
     *            current node
     */
    private void drawGreedy(IGraphViewModel model, Node aNode) {
        if (edgeMapping.containsKey(aNode.getNodeId())) {
            int[] parentNodes = edgeMapping.get(aNode.getNodeId());
            List<MatchingScoreEntry> matchedGenomeRanking = GraphHandlerUtil.determineSortedNodeRanking(aNode, parentNodes, graphData);

            NodeDrawingData drawingData = new NodeDrawingData();
            drawingData.scale = Math.max(zoomFactor.get(), 1.0);
            if (matchedGenomeRanking.size() == 1) {
                int rank = 0;
                Node parentNode = matchedGenomeRanking.get(rank).getParentNode();
                drawingData.parentXCoordinate = parentNode.getXCoordinate();
                drawingData.parentYCoordinate = parentNode.getYCoordinate();
                drawingData.parentHeight = parentNode.getHeight();
                drawingData.parentWidth = parentNode.getWidth();
                drawingData.parentRadius = parentNode.getRadius();
                
                drawSequence(model, aNode, drawingData, rank);
                model.addEdge(aNode.getNodeId(), parentNode.getNodeId(), rank);
            } else {
                // if the matching produces an equal score, they will end up on
                // a different rank. We must determine the true rank based on:
                // 1. is a score equal to a score in a higher rank
                // 2. find the one with the highest node id closest to the
                // aRib.nodeId and draw from those coordinates

                double highestYCoord = 0;
                double xCoord = 0;
                int nodeRank = 0;
                for (int rank = 0; rank < matchedGenomeRanking.size(); rank++) {
                    MatchingScoreEntry entry = matchedGenomeRanking.get(rank);
                    // Find the first occurrence of aRib on its main axis by
                    // aligning it up with the parent that has the highest rank
                    // with this aRib.
                    //TODO: Since we using a greedy algo, we have a drawing bug where the 
                    //graph gently moves towards the right, due to the structural complexity of the graph.
                    if (entry.getChildNodeId() == aNode.getNodeId() && xCoord == 0) {
                        xCoord = entry.getParentNode().getXCoordinate();
                        nodeRank = rank;
                        drawingData.parentWidth = entry.getParentNode().getWidth();
                    }

                    // Find highest y coordinate from this child's parents so
                    // its drawn at an adequate Y distance. (to alleviate the problem of InDel nodes)
                    if (highestYCoord < entry.getParentNode().getYCoordinate()) {
                        highestYCoord = entry.getParentNode().getYCoordinate();
                        drawingData.parentHeight = entry.getParentNode().getHeight();
                    }
                }
                
                drawingData.parentXCoordinate = xCoord;
                drawingData.parentYCoordinate = highestYCoord;
                drawSequence(model, aNode, drawingData, nodeRank);

                // draw all the edges to this aRib
                matchedGenomeRanking = GraphHandlerUtil.determineSortedEdgeRanking(aNode, parentNodes, graphData);
                for (int rank = 0; rank < matchedGenomeRanking.size(); rank++) {
                    MatchingScoreEntry entry = matchedGenomeRanking.get(rank);
                    if (entry.getChildNodeId() == aNode.getNodeId()) {
                        model.addEdge(aNode.getNodeId(), entry.getParentNode().getNodeId(), rank);
                    }
                }
                matchedGenomeRanking = null;
            }

        } else {
            log.info("Rib({}) has no parent edges, new parent", aNode.getNodeId());
            // contains a node with no parent which means it is a strain that
            // starts at this position.
            // TODO: not yet implemented
        }
    }

    private static final double MIN_VISIBILITY_WIDTH = 2.0;
    private static final double DEFAULT_SINGLE_NODE_WIDTH = 15.0;
    private static final double DEFAULT_SINGLE_NODE_HEIGHT = 9.0;

    private void drawSequence(IGraphViewModel model, Node aNode, NodeDrawingData drawingData, int rank) {
        model.addSequence(aNode, rank, drawingData);
    }

    private boolean isInView(Node aRib, double startLevel, double endLevel) {
        return true;
        // int nodeStart = aRib.getLevel() - aRib.getSequence().length;
        // int nodeEnd = node.getLevel();
        // return nodeStart < endLevel && nodeEnd > startLevel;
    }

    private void drawEdge(IGraphViewModel model, Node aRib, int parentXCoordinate, int parentYCoordinate, int rank) {
        int startX = parentXCoordinate;
        int startY = parentYCoordinate + Y_CORRECTION;

        int endX = (int)Math.round(aRib.getXCoordinate());
        int endY = (int)Math.round(aRib.getYCoordinate()) + Y_CORRECTION;

        model.addEdge(aRib.getNodeId(), startX, startY, endX, endY, rank);
        model.addLabel(Integer.toString(aRib.getNodeId()), endX, endY, 2);
    }
}
