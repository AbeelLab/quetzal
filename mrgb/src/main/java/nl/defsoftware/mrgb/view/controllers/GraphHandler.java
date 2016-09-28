/**
 * 
 */
package nl.defsoftware.mrgb.view.controllers;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
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
    private static final int VER_NODE_SPACING = 20;
    private static final int VER_NODE_BASELINE = 200;
    
    private static final int BACKBONE_X_BASELINE = 200;
    private static final int BACKBONE_Y_BASELINE = 200;
    
    
    private Int2ObjectOpenHashMap<Rib> graphMap = new Int2ObjectOpenHashMap<>();
    
    private Int2ObjectOpenHashMap<int[]> edgeQueue = new Int2ObjectOpenHashMap<>();
    /* Keeps track of the X,Y  coordinates that have a node already in place. */
    private int FIRST_NODE = 0;
    
    
    public void setAlternateGraphViewModel(GraphModel model, Int2ObjectOpenHashMap<Rib> graphMap,
            Short2ObjectOpenHashMap<String> genomeNamesMap) {
        
        Rib firstRib = graphMap.get(FIRST_NODE);
        log.info("Rib({}) has {} edges", firstRib.getNodeId(), firstRib.getConnectedEdges().length);
        addEdgesToQueue(FIRST_NODE, firstRib.getConnectedEdges());
        firstRib.setCoordinates(BACKBONE_X_BASELINE, BACKBONE_Y_BASELINE);
        model.addSequence(FIRST_NODE, BACKBONE_X_BASELINE, BACKBONE_Y_BASELINE);
        int XCursor = BACKBONE_X_BASELINE;
        int YCursor = BACKBONE_Y_BASELINE;
        
        //this algorithm goes thru the nodes in the graphmap in topological order.
        //stores the edges in map to draw in a backward order so we know how to draw in respect of the many parallel paths.
       
        for (int i = 1; i < graphMap.size(); i++) {
            Rib aRib = graphMap.get(i);
            log.info("Rib({}) has {} edges", aRib.getNodeId(), aRib.getConnectedEdges().length);
            addEdgesToQueue(aRib.getNodeId(), aRib.getConnectedEdges());
            if (edgeQueue.containsKey(aRib.getNodeId())) {
                int[] parentNodes = edgeQueue.get(aRib.getNodeId());
                for (int j = 0; j < parentNodes.length; j++) {
                    Rib parentRib = graphMap.get(parentNodes[j]);
                    short rank = parentRib.getRankedWeightOfEdge(aRib.getNodeId());//the ranked weight
                    //find parent x and y coordinates
                    int startX = parentRib.getXCoordinate();
                    int startY = parentRib.getYCoordinate();
                    int endX = rank * HOR_NODE_SPACING;
                    int endY = VER_NODE_SPACING;
                    
                    //TODO: postpone: probably add length of the edge, since we dont know how much sequence or nodes are in between
                    
                    //draw edge
                    //TODO: start here by provinding means to the model to draw curves and lines
//                    model.addEdge(sourceId, targetId);
                    
                    //draw node
                    
                }
                //find weight of each edge.
                //find the parent node x and y coords
                //draw edges
                //draw node on the axis of the heaviest edge
            } else {
                //contains a node with no parent which means it is a strain that starts at this position.
                //TODO: not yet implemented
            }
        }

    }

    private void addEdgesToQueue(int fromId, int[] to) {
        for (int i = 0; i < to.length; i++) {
            if (edgeQueue.containsKey(to[i])) {
                int[] fromEdges = edgeQueue.get(to[i]);
                int[] newFromEdges = Arrays.copyOf(fromEdges, fromEdges.length + 1);
                newFromEdges[newFromEdges.length - 1] = fromId;
                edgeQueue.put(to[i], newFromEdges);
            } else {
                edgeQueue.put(to[i], new int[]{fromId});
            }
        }
    }

    private void detectBubble(Rib aRib) {
        int[] connectedEdgeIds = aRib.getConnectedEdges();
        for (int i = 0; i < connectedEdgeIds.length; i++) {
            detectBubble(graphMap.get(connectedEdgeIds[i]));
        }
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
