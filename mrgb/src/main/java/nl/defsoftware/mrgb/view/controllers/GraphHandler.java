/**
 * 
 */
package nl.defsoftware.mrgb.view.controllers;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.defsoftware.mrgb.view.models.GraphModel;
import nl.defsoftware.mrgb.view.models.Sequence;

/**
 * This GraphHandler object is responsible for filling the graph model with the
 * nodes and their coordinates.
 * 
 * @author D.L. Ettema
 *
 */
public class GraphHandler {
    
    private static final Logger log = LoggerFactory.getLogger(GraphHandler.class);
    
    private static final int HOR_NODE_SPACING = 20;
    private static final int VER_NODE_SPACING = 20;

    private static final int VER_NODE_BASELINE = 200;

    
    public void setAlternateGraphViewModel(GraphModel model) {
        
    }
    
    /**
     * Layout algorithm for the graphMap data to be set on the model.
     * 
     * @param model
     * @param graphMap
     */
    public void setGraphViewModel(GraphModel model, HashMap<Short, short[]> graphMap) {
        int HORIZONTAL_NODE_CURSOR = 1;
        for (Short fromKey : graphMap.keySet()) {
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
            short[] connectedSequences = graphMap.get(fromKey);
            for (int i = 0; i < connectedSequences.length; i++) {
                // Sequence prevPlacedSequence =
                // model.findSequenceById(connectedSequences[i]);
                // if (prevPlacedSequence != null) {
                // int avgYCoord =
                // calculateAverageYCoordinate(prevPlacedSequence.getSequenceParents());
                // prevPlacedSequence.setCenterY(avgYCoord);
                //
                // } else {
                if (prevFollowUpNodeId + 1 == Short.toUnsignedInt(connectedSequences[i])) {
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
     * @return
     * 
     *         int
     *
     */
    private int calculateAverageYCoordinate(List<Sequence> sequenceParents) {
        BigDecimal avgYCoord = new BigDecimal(0);
        for (Sequence sequence : sequenceParents) {
            avgYCoord.add(new BigDecimal(sequence.getCenterY()));
        }
        return avgYCoord.divideToIntegralValue(BigDecimal.valueOf(sequenceParents.size())).intValue();
    }

    private void addEdges(short[] toNodes, GraphModel model, Short from) {
        for (int i = 0; i < toNodes.length; i++) {
            model.addEdge(from, toNodes[i]);
        }
    }

}