/**
 * 
 */
package nl.defsoftware.mrgb.services;

import static nl.defsoftware.mrgb.models.graph.NodeType.isSame;

import java.util.HashSet;
import java.util.List;
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
 * This GraphHandler object is responsible for filling the graph model from parsed data with the nodes and placing them
 * in a local coordinate system.
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

    private Int2ObjectOpenHashMap<int[]> edgeMapping;
    private Int2ObjectOpenHashMap<int[]> bubbleMapping;

    private DoubleProperty zoomFactor = new SimpleDoubleProperty(1.0);
    private int drawingStartCoordinate = 0;
    private int drawingRange = 0;
    private List<Integer> longestPath;

    /**
     * Given the two data-structures this Handler will input these into the model and the model will determine the
     * layout
     * 
     * @param graphMap
     * @param genomeNamesMap
     */
    public GraphHandler(Int2ObjectLinkedOpenHashMap<Node> graphData, Short2ObjectOpenHashMap<String> genomeNamesMap,
            Int2ObjectLinkedOpenHashMap<Bubble> bubbles) {
        this.graphData = graphData;
        this.genomeNamesMap = genomeNamesMap;
        this.bubbles = bubbles;
        this.drawnSubGraphNodeIds = new HashSet<>();
        this.edgeMapping = new Int2ObjectOpenHashMap<>();
        this.bubbleMapping = new Int2ObjectOpenHashMap<>();
    }

    /**
     * 
     * 
     * @param edgeCanvas
     * @param model
     * @param drawingStartCoordinate:
     *            where to start drawing.
     * @param drawingRange:
     *            the range of drawing in total which can be bigger then the actual window view
     * @param zoomFactor
     */
    public void loadGraphViewModel(IGraphViewModel model, int drawingStartCoordinate, int drawingRange,
            DoubleProperty zoomFactor, List<Integer> longestPath) {
        log.info("Zoomfactor: " + zoomFactor.get());
        this.zoomFactor = zoomFactor;
        this.drawingStartCoordinate = drawingStartCoordinate;
        this.drawingRange = drawingRange;
        this.longestPath = longestPath;
        edgeMapping.clear();
        bubbleMapping.clear();
        int sourceNodeId = drawSourceNode(model, zoomFactor);

        for (int i = (sourceNodeId + 1); i < drawingRange; i++) { // testing purposes
            if (graphData.containsKey(i)) {
                Node aNode = getNodeOrBubble(i);
                // Node aNode = graphData.get(i);
                GraphHandlerUtil.catalogEdgesForDrawing(edgeMapping, aNode.getNodeId(), getConnectedEdges(aNode));
                drawGreedy(model, aNode);
                drawBubble(model, aNode);
            }
        }
    }

    private int drawSourceNode(IGraphViewModel model, DoubleProperty zoomFactor) {
        Node firstNode = getNodeOrBubble(drawingStartCoordinate);

        GraphHandlerUtil.catalogEdgesForDrawing(edgeMapping, firstNode.getNodeId(), getConnectedEdges(firstNode));
        NodeDrawingData drawingData = new NodeDrawingData();
        drawingData.xCoordinate = BACKBONE_X_BASELINE;
        drawingData.yCoordinate = BACKBONE_Y_BASELINE;
        drawingData.width = 0;
        drawingData.height = 0;
        drawingData.scale = zoomFactor.get();
        drawSequence(model, firstNode, drawingData, 0);
        return firstNode.getNodeId();
    }

    private Node getNodeOrBubble(int sourceNode) {
        if (bubbles.containsKey(sourceNode)) {
            Bubble bubble = bubbles.get(sourceNode);
            GraphHandlerUtil.catalogEdgesForDrawing(bubbleMapping, sourceNode,
                    new int[] { bubble.getStop().getNodeId() });
        }
        return graphData.get(sourceNode);
    }

    private int[] getConnectedEdges(Node node) {
        return isSame(NodeType.SINGLE_NODE, node.getNodeType()) ? node.getConnectedEdges()
                : getConnectedEdges(((Bubble) node).getStop());
    }

    /**
     * <p>
     * This method will determine any parent nodes previously stored in the 'addEdgesToQueue' in a previous iteration
     * and draw these edges with respect to the location of the parent node and current node
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
            List<MatchingScoreEntry> matchedGenomeRanking = GraphHandlerUtil.determineSortedNodeRanking(aNode,
                    parentNodes, graphData);

            NodeDrawingData drawingData = new NodeDrawingData();
            drawingData.scale = zoomFactor.get();
            if (matchedGenomeRanking.size() == 1) {
                int rank = 0;
                Node parentNode = matchedGenomeRanking.get(rank).getParentNode();
                drawingData.parentXCoordinate = parentNode.getXCoordinate();
                drawingData.parentYCoordinate = parentNode.getYCoordinate();
                drawingData.parentHeight = parentNode.getHeight();
                drawingData.parentWidth = parentNode.getWidth();
                drawingData.parentRadius = parentNode.getRadius();

                drawSequence(model, aNode, drawingData, rank);
                drawEdge(model, aNode, rank, parentNode);
            } else {
                // if the matching produces an equal score, they will end up on
                // a different rank. We must determine the true rank based on:
                // 1. is a score equal to a score in a higher rank
                // 2. find the one with the highest node id closest to the
                // aRib.nodeId and draw from those coordinates

                double highestYCoord = 0.0;
                double xCoord = 0.0;
                int nodeRank = 0;
                for (int rank = 0; rank < matchedGenomeRanking.size(); rank++) {
                    MatchingScoreEntry entry = matchedGenomeRanking.get(rank);
                    // Find the first occurrence of aRib on its main axis by
                    // aligning it up with the parent that has the highest rank
                    // with this aRib.
                    if (Double.compare(xCoord, 0.0) == 0) {
                        if (longestPath.contains(aNode.getNodeId())) {
                            xCoord = BACKBONE_X_BASELINE;
                        } else if (entry.getChildNodeId() == aNode.getNodeId()) {
                            xCoord = entry.getParentNode().getXCoordinate();
                            nodeRank = rank;
                            drawingData.parentWidth = entry.getParentNode().getWidth();
                        }
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
                        drawEdge(model, aNode, rank, entry.getParentNode());
                    }
                }
                matchedGenomeRanking = null;
            }

        } else {
            log.info("Rib({}) has no parent edges, new parent (DIDNT DRAW NODE, PLEASE IMPLEMENT)", aNode.getNodeId());
            // contains a node with no parent which means it is a strain that
            // starts at this position.
            // TODO: not yet implemented
        }
    }

    /**
     * @param model
     * @param aNode
     * @param rank
     * @param parentNode
     */
    private void drawEdge(IGraphViewModel model, Node aNode, int rank, Node parentNode) {
        model.addEdge(aNode.getNodeId(), parentNode.getNodeId(), rank);
    }

    private void drawSequence(IGraphViewModel model, Node aNode, NodeDrawingData drawingData, int rank) {
        model.addSequence(aNode, rank, drawingData);
    }

    private void drawBubble(IGraphViewModel model, Node bubbleEndNode) {
        if (bubbleMapping.containsKey(bubbleEndNode.getNodeId())) {
            int[] bubbleStartIds = bubbleMapping.get(bubbleEndNode.getNodeId());
            NodeDrawingData drawingData = new NodeDrawingData();
            drawingData.id = bubbleStartIds[0];

            int[] parentNodes = edgeMapping.get(drawingData.id);
            Node startNode = graphData.get(drawingData.id);
            List<MatchingScoreEntry> matchedGenomeRanking = GraphHandlerUtil.determineSortedNodeRanking(startNode,
                    parentNodes, graphData);
            int rank = 0;
            Node parentNode = matchedGenomeRanking.get(rank).getParentNode();

            drawingData.parentXCoordinate = parentNode.getXCoordinate();
            drawingData.parentYCoordinate = parentNode.getYCoordinate();
            drawingData.parentHeight = parentNode.getHeight();
            drawingData.parentWidth = parentNode.getWidth();
            drawingData.parentRadius = parentNode.getRadius();
            drawingData.scale = zoomFactor.get();
            drawSequence(model, bubbles.get(drawingData.id), drawingData, rank);
        }
    }

    /**
     * @deprecated
     * 
     * @param model
     * @param aRib
     * @param parentXCoordinate
     * @param parentYCoordinate
     * @param rank
     */
    private void drawEdge(IGraphViewModel model, Node aRib, int parentXCoordinate, int parentYCoordinate, int rank) {
        int startX = parentXCoordinate;
        int startY = parentYCoordinate + Y_CORRECTION;

        int endX = (int) Math.round(aRib.getXCoordinate());
        int endY = (int) Math.round(aRib.getYCoordinate()) + Y_CORRECTION;

        model.addEdge(aRib.getNodeId(), startX, startY, endX, endY, rank);
        model.addLabel(Integer.toString(aRib.getNodeId()), endX, endY, 2);
    }
}
