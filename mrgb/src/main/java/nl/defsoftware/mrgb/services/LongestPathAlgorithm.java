package nl.defsoftware.mrgb.services;

import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap;
import nl.defsoftware.mrgb.models.graph.Node;

/**
 * This algorithm calculates the longest path in a dynamic programming fashion.
 * 
 * @author D.L. Ettema
 *
 */
public class LongestPathAlgorithm {
    
    private Deque<Integer> stack = new LinkedList<>();

    /**
     * Given a graph stored in a {@link Int2ObjectLinkedOpenHashMap} with
     * <code><ID,Node></code> we determine the longest possible path(s). We
     * return a array of int IDs that correspond to the IDs in the provided Map.
     * 
     * @param sequencesDataMap
     * @return
     */
    public int[] findLongestPath(final Int2ObjectLinkedOpenHashMap<Node> sequencesDataMap) {
        // ensure datamap has a Source node and a Target node
        // int [] longestPath = calculateLongestPath();
        return new int[] {};
    }

    
    /**
     * Dynamic programming fashion of determining the longest path by depth first search, without memoization.
     * 
     * @param nodeU
     * @param targetNodeId
     * @return The largest number of steps as part of the longest path given a {@link Node} to a targetnode
     */
    private Integer calculateDFSAmountOfStepsOfLongestPath(final Node nodeU, final Node targetNode) {
        if (nodeU.getNodeId() == targetNode.getNodeId()) {
            return 1;
        }

        //depth first search of calculating only the number of nodes, not which ones
        List<Integer> scoresOfOutNodes = new ArrayList<>();
        for (Node outNode : nodeU.getOutEdges()) {
            scoresOfOutNodes.add(calculateDFSAmountOfStepsOfLongestPath(outNode, targetNode));
        }
        return findMaxScore(scoresOfOutNodes) + 1;
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
    
    //---------------------------------------------------------
    
    private Map<Integer, List<DistanceNodeTupel>> connectionFromMap = new HashMap<>();
    //example: <1, <2,4>>
    
    /**
     * This uses an iterative algorithm to determine the longest path in a given graph map (sequencesDataMap) and uses a Breadth First Search
     * 
     * 
     * @param sequencesDataMap
     */
    public void findLongestPathBFS(final Int2ObjectLinkedOpenHashMap<Node> sequencesDataMap) {
        int lastFromId = 0;
        while(!stack.isEmpty()) {
            lastFromId = stack.pop();
            Node fromNode = sequencesDataMap.get(lastFromId);
            if (calcBFSLongestPathNumberOfSteps(fromNode, 1)) {
                stack.clear();
            }
        }
        System.out.println("max steps: " + getMaxDistanceToThisNode(lastFromId));
        
    }
    
    private boolean calcBFSLongestPathNumberOfSteps(final Node fromNode, int targetNodeId) {
       if (fromNode.getNodeId() == targetNodeId) {
           return true;
       } else {
           int dist = getMaxDistanceToThisNode(fromNode.getNodeId()) + 1;
           for (Node outNode : fromNode.getOutEdges()) {
               if (connectionFromMap.containsKey(outNode.getNodeId())) {
                   connectionFromMap.put(outNode.getNodeId(), new ArrayList<DistanceNodeTupel>());
               }
               connectionFromMap.get(outNode.getNodeId()).add(new DistanceNodeTupel(fromNode.getNodeId(), dist));
               stack.add(outNode.getNodeId());
           }
           return false;
       }
    }
    
    private int getMaxDistanceToThisNode(int fromId) {
        int max = 0;
        for (DistanceNodeTupel t : connectionFromMap.get(fromId)) {
            if (t.distance > max) max = t.distance;
        }
        return max;
    }
    
    private class DistanceNodeTupel {
        public int nodeId;
        public int distance;
        public DistanceNodeTupel(int nodeId, int distance) {
            this.nodeId = nodeId;
            this.distance = distance;
        }
    }
}
