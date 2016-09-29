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
import javafx.scene.shape.Shape;
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

    private Int2ObjectOpenHashMap<Rib> graphMap = new Int2ObjectOpenHashMap<>();

    private Int2ObjectOpenHashMap<int[]> edgeQueue = new Int2ObjectOpenHashMap<>();
    /* Keeps track of the X,Y coordinates that have a node already in place. */
    private int FIRST_NODE = 1;

    /**
     * This will fill the graph based on the graphmap.
     * 
     * 
     * @param model
     * @param graphMap
     * @param genomeNamesMap
     */
    public void setAlternateGraphViewModel(GraphModel model, Int2ObjectOpenHashMap<Rib> graphMap,
            Short2ObjectOpenHashMap<String> genomeNamesMap) {

        Rib firstRib = graphMap.get(FIRST_NODE);
//        log.info("Rib({}) has {} edges", firstRib.getNodeId(), firstRib.getConnectedEdges().length);
        addEdgesToQueue(firstRib.getNodeId(), firstRib.getConnectedEdges());
        drawSequence(model, firstRib, BACKBONE_X_BASELINE, BACKBONE_Y_BASELINE, 0);
        
        int XCursor = BACKBONE_X_BASELINE;
        int YCursor = BACKBONE_Y_BASELINE;

        // this algorithm goes thru the nodes in the graphmap in topological order.
        // stores the edges in map to draw in a backward order so we know how to
        // draw in respect of the many parallel paths.

        for (int i = (FIRST_NODE + 1); i < graphMap.size(); i++) {
            Rib aRib = graphMap.get(i);
            // log.info("Rib({}) has {} edges", aRib.getNodeId(),
            // aRib.getConnectedEdges().length);
            addEdgesToQueue(aRib.getNodeId(), aRib.getConnectedEdges());
            drawEdgesToParents(model, graphMap, aRib);
        }

    }

    private void drawSequence(GraphModel model, Rib aRib, int parentXCoordinate, int parentYCoordinate, int rank) {
        int xCoordinate = parentXCoordinate + (rank * HOR_NODE_SPACING);
        int yCoordinate = parentYCoordinate + VER_NODE_SPACING;
        aRib.setCoordinates(xCoordinate, yCoordinate);
        model.addSequence(aRib.getNodeId(), xCoordinate, yCoordinate);
        model.addLabel(Integer.toString(aRib.getNodeId()), xCoordinate+10, yCoordinate);
    }

    /**
     * This method will determine any parent nodes previously stored in the
     * 'addEdgesToQueue' in a previous iteration and draw these edges with
     * respect to the location of the parent node and current node
     * 
     * @param model
     *            the data model to be filled
     * @param graphMap
     *            map containing all the nodes in the graph
     * @param aRib
     *            current node
     */
    private void drawEdgesToParents(GraphModel model, Int2ObjectOpenHashMap<Rib> graphMap, Rib aRib) {
        if (edgeQueue.containsKey(aRib.getNodeId())) {
            int[] parentNodes = edgeQueue.get(aRib.getNodeId());
            for (int j = 0; j < parentNodes.length; j++) {
                Rib parentRib = graphMap.get(parentNodes[j]);
                log.info("Parent ID({}) X={} and Y={}", parentRib.getNodeId(), parentRib.getXCoordinate(), parentRib.getYCoordinate());

                short rank = parentRib.getRankedWeightOfEdge(aRib.getNodeId());// the ranked weight
                
                drawEdge(model, aRib, parentRib, rank);
                drawSequence(model, aRib, parentRib.getXCoordinate(), parentRib.getYCoordinate(), rank);
            }
            // find weight of each edge.
            // find the parent node x and y coords
            // draw edges
            // draw node on the axis of the heaviest edge
        } else {
            log.info("Rib({}) has no parent edges, new parent", aRib.getNodeId());
            // contains a node with no parent which means it is a strain that
            // starts at this position.
            // TODO: not yet implemented
        }
    }

    private void drawEdge(GraphModel model, Rib aRib, Rib parentRib, short rank) {
        // find parent x and y coordinates
        int startX = parentRib.getXCoordinate();
        int startY = parentRib.getYCoordinate();
        
        int endX = parentRib.getXCoordinate() + (rank * HOR_NODE_SPACING);
        int endY = parentRib.getYCoordinate() + VER_NODE_SPACING;

        // TODO: postpone: probably add length of the edge, since we
        // dont know how much sequence or nodes are in between

        // draw edge
        // TODO: start here by providing means to the ribbongraphmodel
        // to draw curves and lines
        model.addEdge(aRib.getNodeId(), startX, startY, endX, endY, rank);
    }

    private void addEdgesToQueue(int fromId, int[] to) {
        for (int i = 0; i < to.length; i++) {
            if (edgeQueue.containsKey(to[i])) {
                int[] fromEdges = edgeQueue.get(to[i]);
                int[] newFromEdges = Arrays.copyOf(fromEdges, fromEdges.length + 1);
                newFromEdges[newFromEdges.length - 1] = fromId;
                edgeQueue.put(to[i], newFromEdges);
            } else {
                edgeQueue.put(to[i], new int[] { fromId });
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
