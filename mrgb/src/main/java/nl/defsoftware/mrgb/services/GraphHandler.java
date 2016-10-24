/**
 * 
 */
package nl.defsoftware.mrgb.services;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.shorts.Short2ObjectOpenHashMap;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.Pane;
import nl.defsoftware.mrgb.models.Rib;
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

    private static final int BACKBONE_X_BASELINE = 200;
    private static final int BACKBONE_Y_BASELINE = 200;

    private static final int Y_CORRECTION = 5;

    private Int2ObjectLinkedOpenHashMap<Rib> graphData = new Int2ObjectLinkedOpenHashMap<>();
    private Short2ObjectOpenHashMap<String> genomeNamesMap = new Short2ObjectOpenHashMap<>();

    private Int2ObjectOpenHashMap<int[]> edgeQueue = new Int2ObjectOpenHashMap<>();

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
    public GraphHandler(Int2ObjectLinkedOpenHashMap<Rib> graphData, Short2ObjectOpenHashMap<String> genomeNamesMap) {
        this.graphData = graphData;
        this.genomeNamesMap = genomeNamesMap;

    }

    /**
     * 
     * 
     * @param nodePane
     * @param edgeCanvas
     * @param model
     * @param drawingStartCoordinate: where to start drawing.
     * @param drawingRange: the range of drawing in total which can be bigger then the actual window view
     * @param zoomFactor
     */
    public void loadAlternateGraphViewModel(Pane nodePane, Canvas edgeCanvas, IGraphViewModel model,
            double drawingStartCoordinate, double drawingRange, DoubleProperty zoomFactor) {
        log.info("Zoomfactor: " + zoomFactor.get());
        this.zoomFactor = zoomFactor;
        this.drawingStartCoordinate = drawingStartCoordinate;
        this.drawingRange = drawingRange;

        int sourceNode = graphData.firstIntKey();// TODO should be viewing range dependent
        Rib firstRib = graphData.get(sourceNode);

        // double paneHeight = nodePane.getPrefHeight();
        // int rows = Math.floorDiv((int)Math.round(paneHeight), VER_NODE_SPACING);

        GraphHandlerUtil.addEdgesToQueue(edgeQueue, firstRib.getNodeId(), firstRib.getConnectedEdges());
        drawSequence(model, firstRib, BACKBONE_X_BASELINE, BACKBONE_Y_BASELINE, 0);

        // for (int i = (sourceNode + 1); i < 500; i++) { //testing purposes
        for (int i = (sourceNode + 1); i < graphData.size(); i++) {
            if (graphData.containsKey(i)) {
                Rib aRib = graphData.get(i);
                GraphHandlerUtil.addEdgesToQueue(edgeQueue, aRib.getNodeId(), aRib.getConnectedEdges());
                drawEdgesAndNodesToParents(model, aRib);
            }
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
     * @param aRib
     *            current node
     */
    private void drawEdgesAndNodesToParents(IGraphViewModel model, Rib aRib) {
        if (edgeQueue.containsKey(aRib.getNodeId())) {
            int[] parentNodes = edgeQueue.get(aRib.getNodeId());
            List<MatchingScoreEntry> matchedGenomeRanking = GraphHandlerUtil.determineSortedNodeRanking(aRib,
                    parentNodes, graphData);

            if (matchedGenomeRanking.size() == 1) {
                int rank = 0;
                Rib parentRib = matchedGenomeRanking.get(rank).getParentRib();
                drawSequence(model, aRib, parentRib.getXCoordinate(), parentRib.getYCoordinate(), rank);
                model.addEdge(aRib.getNodeId(), parentRib.getNodeId(), rank);
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
                    if (entry.getChildNodeId() == aRib.getNodeId() && xCoord == 0) {
                        xCoord = entry.getParentRib().getXCoordinate();
                        nodeRank = rank;
                    }

                    // Find highest y coordinate from this child's parents so
                    // its drawn at an adequate Y distance.
                    if (highestYCoord < entry.getParentRib().getYCoordinate()) {
                        highestYCoord = entry.getParentRib().getYCoordinate();
                    }
                }
                drawSequence(model, aRib, xCoord, highestYCoord, nodeRank);

                // draw all the edges to this aRib
                matchedGenomeRanking = GraphHandlerUtil.determineSortedEdgeRanking(aRib, parentNodes, graphData);
                for (int rank = 0; rank < matchedGenomeRanking.size(); rank++) {
                    MatchingScoreEntry entry = matchedGenomeRanking.get(rank);
                    if (entry.getChildNodeId() == aRib.getNodeId()) {
                        model.addEdge(aRib.getNodeId(), entry.getParentRib().getNodeId(), rank);
                    }
                }
                matchedGenomeRanking = null;
            }

        } else {
            log.info("Rib({}) has no parent edges, new parent", aRib.getNodeId());
            // contains a node with no parent which means it is a strain that
            // starts at this position.
            // TODO: not yet implemented
        }
    }

    private static final double MIN_VISIBILITY_WIDTH = 2.0;
    private static final double DEFAULT_SINGLE_NODE_WIDTH = 15.0;
    private static final double DEFAULT_SINGLE_NODE_HEIGHT = 9.0;

    private void drawSequence(IGraphViewModel model, Rib aRib, double parentXCoordinate, double parentYCoordinate, int rank) {
//        double height = GraphHandlerUtil.calculateNodeHeight(aRib, zoomFactor.get());
        double height = DEFAULT_SINGLE_NODE_HEIGHT + Math.exp(zoomFactor.get());
        double width = DEFAULT_SINGLE_NODE_WIDTH + Math.exp(zoomFactor.get());
        
        if (!isInView(aRib, drawingStartCoordinate, drawingRange)) {
            return;
        } else if (height < MIN_VISIBILITY_WIDTH) {
            // aRib.unpop();
            // heatmapColorer.drawHeatmap(node, startLevel);
//            return;
        }

        //Here we assume the node is in the drawable region
        NodeDrawingData drawingData = new NodeDrawingData();
        drawingData.height = height;
        drawingData.width = width;
        
        drawingData.xCoordinate = parentXCoordinate + (HOR_NODE_SPACING * rank);
        drawingData.yCoordinate = parentYCoordinate + VER_NODE_SPACING;
        
        drawingData.scale = Math.max(zoomFactor.get(), 1.0);
        
        // happens in the model
        // IViewGraphNode viewNode = ViewNodeBuilder.buildNode(node, width, height);
        // double fade = calculateBubbleFadeFactor(node, width);
        
        aRib.setCoordinates(drawingData.xCoordinate, drawingData.yCoordinate);
        
        model.addSequence(aRib, rank, drawingData);
    }

    private boolean isInView(Rib aRib, double startLevel, double endLevel) {
        return true;
        // int nodeStart = aRib.getLevel() - aRib.getSequence().length;
        // int nodeEnd = node.getLevel();
        // return nodeStart < endLevel && nodeEnd > startLevel;
    }

    private void drawEdge(IGraphViewModel model, Rib aRib, int parentXCoordinate, int parentYCoordinate, int rank) {
        int startX = parentXCoordinate;
        int startY = parentYCoordinate + Y_CORRECTION;

        int endX = (int)Math.round(aRib.getXCoordinate());
        int endY = (int)Math.round(aRib.getYCoordinate()) + Y_CORRECTION;

        model.addEdge(aRib.getNodeId(), startX, startY, endX, endY, rank);
        model.addLabel(Integer.toString(aRib.getNodeId()), endX, endY, 2);
    }
}
