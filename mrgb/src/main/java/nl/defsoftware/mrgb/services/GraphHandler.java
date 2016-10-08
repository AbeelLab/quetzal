/**
 * 
 */
package nl.defsoftware.mrgb.view.controllers;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.shorts.Short2ObjectOpenHashMap;
import nl.defsoftware.mrgb.models.Rib;
import nl.defsoftware.mrgb.view.models.GraphModel;
import nl.defsoftware.mrgb.view.models.Sequence;

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

    private Int2ObjectLinkedOpenHashMap<Rib> graphMap = new Int2ObjectLinkedOpenHashMap<>();
    private Short2ObjectOpenHashMap<String> genomeNamesMap = new Short2ObjectOpenHashMap<>();
    
    private Int2ObjectOpenHashMap<int[]> edgeQueue = new Int2ObjectOpenHashMap<>();
    /* Keeps track of the X,Y coordinates that have a node already in place. */

    public GraphHandler(Int2ObjectLinkedOpenHashMap<Rib> graphMap, Short2ObjectOpenHashMap<String> genomeNamesMap) {
        this.graphMap = graphMap;
        this.genomeNamesMap = genomeNamesMap;
    }
    
    
    /**
     * This will fill the graph based on the graphmap.
     * 
     * @param model
     * @param graphMap
     * @param genomeNamesMap
     */
    public void setAlternateGraphViewModel(GraphModel model) {

        int sourceNode = graphMap.firstIntKey();
//        int sourceNode = 344;
        Rib firstRib = graphMap.get(sourceNode);
        
        
        GraphHandlerUtil.addEdgesToQueue(edgeQueue, firstRib.getNodeId(), firstRib.getConnectedEdges());
        drawSequence(model, firstRib, BACKBONE_X_BASELINE, BACKBONE_Y_BASELINE, 0);

//        for (int i = (sourceNode + 1); i < 500; i++) { //testing purposes
        for (int i = (sourceNode + 1); i < graphMap.size(); i++) {
            Rib aRib = graphMap.get(i);
            GraphHandlerUtil.addEdgesToQueue(edgeQueue, aRib.getNodeId(), aRib.getConnectedEdges());
            drawEdgesAndNodesToParents(model, aRib);
        }
    }

    /**
     * This method will determine any parent nodes previously stored in the
     * 'addEdgesToQueue' in a previous iteration and draw these edges with
     * respect to the location of the parent node and current node
     * 
     * insertion:
     * 5-6 = 4
     * 5-7 = 5
     * 6-7 = 4
     * 
     * @param model
     *            the data model to be filled
     * @param graphMap
     *            map containing all the nodes in the graph
     * @param aRib
     *            current node
     */
    private void drawEdgesAndNodesToParents(GraphModel model, Rib aRib) {
        if (edgeQueue.containsKey(aRib.getNodeId())) {
            int[] parentNodes = edgeQueue.get(aRib.getNodeId());
            List<MatchingScoreEntry> matchedGenomeRanking = GraphHandlerUtil.determineSortedNodeRanking(aRib, parentNodes, graphMap);

            if (matchedGenomeRanking.size() == 1) {
                int rank = 0;
                Rib parentRib = matchedGenomeRanking.get(rank).getParentRib();
                drawSequence(model, aRib, parentRib.getXCoordinate(), parentRib.getYCoordinate(), rank);
//                drawEdge(model, aRib, parentRib.getXCoordinate(), parentRib.getYCoordinate(), rank);
                drawEdge(model, aRib.getNodeId(), parentRib.getNodeId(), rank);
            } else {
                //if the matching produces an equal score, they will end up on a different rank. We must determine the true rank based on:
                //1. is a score equal to a score in a higher rank
                //2. find the one with the highest node id closest to the aRib.nodeId and draw from those coordinates
                
                int highestYCoord = 0;
                int xCoord = 0;
                int nodeRank = 0;
                for (int rank = 0; rank < matchedGenomeRanking.size(); rank++) {
                    MatchingScoreEntry entry = matchedGenomeRanking.get(rank);
                    //Find the first occurrence of aRib on its main axis by aligning it up with the parent that has the highest rank with this aRib.
                    if (entry.getChildNodeId() == aRib.getNodeId() && xCoord == 0) {
                        xCoord = entry.getParentRib().getXCoordinate();
                        nodeRank = rank;
                    }
                    
                    //Find highest y coordinate from this child's parents so its drawn at an adequate Y distance.
                    if (highestYCoord < entry.getParentRib().getYCoordinate()) {
                        highestYCoord = entry.getParentRib().getYCoordinate();
                    }
                }
                drawSequence(model, aRib, xCoord, highestYCoord, nodeRank);
                
                //draw all the edges to this aRib
                matchedGenomeRanking = GraphHandlerUtil.determineSortedEdgeRanking(aRib, parentNodes, graphMap);
                for (int rank = 0; rank < matchedGenomeRanking.size(); rank++) {
                    MatchingScoreEntry entry = matchedGenomeRanking.get(rank);
                    if (entry.getChildNodeId() == aRib.getNodeId()) {
                        drawEdge(model, aRib.getNodeId(), entry.getParentRib().getNodeId(), rank);
//                        drawEdge(model, 
//                                aRib, 
//                                entry.getParentRib().getXCoordinate(), 
//                                entry.getParentRib().getYCoordinate(), 
//                                rank);
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

    private void drawSequence(GraphModel model, Rib aRib, int parentXCoordinate, int parentYCoordinate, int rank) {
        int xCoordinate = parentXCoordinate + (HOR_NODE_SPACING * rank);
        int yCoordinate = parentYCoordinate + VER_NODE_SPACING;
        
        aRib.setCoordinates(xCoordinate, yCoordinate);
        model.addSequence(aRib.getNodeId(), xCoordinate, yCoordinate);
//        model.addLabel(Integer.toString(aRib.getNodeId()), xCoordinate + 10, yCoordinate, 0);
    }

    private void drawEdge(GraphModel model, Rib aRib, int parentXCoordinate, int parentYCoordinate, int rank) {
        int startX = parentXCoordinate;
        int startY = parentYCoordinate + Y_CORRECTION;

        int endX = aRib.getXCoordinate();
        int endY = aRib.getYCoordinate() + Y_CORRECTION;

        model.addEdge(aRib.getNodeId(), startX, startY, endX, endY, rank);
        model.addLabel(Integer.toString(aRib.getNodeId()), endX, endY, 2);
    }
    
    private void drawEdge(GraphModel model, int childRibId, int parentRibId, int rank) {
        model.addEdge(childRibId, parentRibId, rank);
    }

    /**
     * Layout algorithm for the graphMap data to be set on the model.
     * 
     * @param model
     * @param graphMap
     */
    public void setGraphViewModel(GraphModel model, Map<Integer, int[]> graphMap) {
        int HORIZONTAL_NODE_CURSOR = 1;
        for (Integer fromKey : graphMap.keySet()) {
            int VERTICAL_NODE_CURSOR = 1;// reset after each new key
            int xCoord = HORIZONTAL_NODE_CURSOR * HOR_NODE_SPACING;
            int yCoord = VER_NODE_BASELINE;

            // @TODO do check if node is already placed and which coords
            // Sequence prevPlacedSequence = model.findSequenceById(fromKey);
            // if (prevPlacedSequence != null) {
            // // set new y baseline
            // yCoord = (int) prevPlacedSequence.getCenterY();
            // } else {
            // model.addSequence(fromKey, xCoord, yCoord);
            // }
            model.addSequence(fromKey, xCoord, yCoord);
            HORIZONTAL_NODE_CURSOR++;

            int prevFollowUpNodeId = fromKey.intValue();
            // @TODO do topological sort first
            int[] connectedSequences = graphMap.get(fromKey);
            for (int i = 0; i < connectedSequences.length; i++) {
                // Sequence prevPlacedSequence =
                // model.findSequenceById(connectedSequences[i]);
                // if (prevPlacedSequence != null) {
                // int avgYCoord =
                // calculateAverageYCoordinate(prevPlacedSequence.getSequenceParents());
                // prevPlacedSequence.setCenterY(avgYCoord);
                //
                // } else {
                if (prevFollowUpNodeId + 1 == connectedSequences[i]) {
                    if (i == 0) { // backbone vertical position
                        xCoord = HORIZONTAL_NODE_CURSOR * HOR_NODE_SPACING;
                        HORIZONTAL_NODE_CURSOR++;
                    }
                    yCoord = VER_NODE_BASELINE + (VERTICAL_NODE_CURSOR * VER_NODE_SPACING);
                    model.addSequence(connectedSequences[i], xCoord, yCoord);
                    VERTICAL_NODE_CURSOR++;
                    prevFollowUpNodeId = connectedSequences[i];
                } else {
                    xCoord = (HORIZONTAL_NODE_CURSOR * HOR_NODE_SPACING);
                    yCoord = VER_NODE_BASELINE;
                    model.addSequence(connectedSequences[i], xCoord, yCoord);
                    HORIZONTAL_NODE_CURSOR++;
                }
                // add edge @TODO add weight and know if we need to go around
                // other
                // nodes when drawing
                model.addEdge(fromKey, connectedSequences[i]);
            }
            // }
        }
    }

    /**
     * @param sequenceParents
     * @return int
     */
    private int calculateAverageYCoordinate(List<Sequence> sequenceParents) {
        BigDecimal avgYCoord = new BigDecimal(0);
        for (Sequence sequence : sequenceParents) {
            avgYCoord.add(new BigDecimal(sequence.getCenterY()));
        }
        return avgYCoord.divideToIntegralValue(BigDecimal.valueOf(sequenceParents.size())).intValue();
    }

    private void addEdges(int[] toNodes, GraphModel model, Integer from) {
        for (int i = 0; i < toNodes.length; i++) {
            model.addEdge(from, toNodes[i]);
        }
    }

}
