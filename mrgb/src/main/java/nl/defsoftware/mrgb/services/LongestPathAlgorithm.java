package nl.defsoftware.mrgb.services;

import java.util.ArrayList;
import java.util.List;

import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap;
import nl.defsoftware.mrgb.models.graph.Node;

/**
 * @author D.L. Ettema
 *
 */
public class LongestPathAlgorithm {

    public int [] findLongestPath(final Int2ObjectLinkedOpenHashMap<Node> sequencesDataMap) {
//        ensure datamap has a Source node and a Target node
//        int [] longestPath = calculateLongestPath();
        return new int[] {};
    }

   private Integer calculateAmountOfStepsOfLongestPath(Node nodeU, Node targetNode) {
        if (nodeU.getNodeId() == targetNode.getNodeId()) {
            return 1;
        }
        
        List <Integer> scoresU = new ArrayList<>();
        for (Node outNode : nodeU.getOutEdges()) {
            scoresU.add(calculateAmountOfStepsOfLongestPath(outNode, targetNode));
        }
        return findMaxScore(scoresU) + 1;
    }
    
   /**
    * Find the maximum score in the given list of integers.
    * 
    * @param scores
    * @return maximum number
    */
    private Integer findMaxScore(List<Integer> scores) {
        int highestIndex = 0;
        for (int i = 0; i < scores.size(); i++) {
            if (scores.get(highestIndex) < scores.get(i)) {
                highestIndex = i;
            }
        }
        return scores.get(highestIndex);
    }
}
