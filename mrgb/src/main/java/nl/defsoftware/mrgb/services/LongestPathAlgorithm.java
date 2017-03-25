package nl.defsoftware.mrgb.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap;
import nl.defsoftware.mrgb.models.graph.Node;

/**
 * This algorithm calculates the longest path in a dynamic programming fashion.
 * 
 * @author D.L. Ettema
 *
 */
public class LongestPathAlgorithm {

    private static final Logger log = LoggerFactory.getLogger(LongestPathAlgorithm.class);
    
    /**
     * Given a graph stored in a {@link Int2ObjectLinkedOpenHashMap} with <code><ID,Node></code> we determine the
     * longest possible path(s). We return a array of int IDs that correspond to the IDs in the provided Map.
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

        // depth first search of calculating only the number of nodes, not which
        // ones
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

    // --------------------------- BFS implementation --------------------------

    /**
     * This <code>Map</code> contains the cumulative distances. Key value is the node ID to which the distance applies.
     * Each value contains a <code>DistanceNodeTupel</code> with the parent node ID of the key node ID with its
     * cumulative distance. In essence, this is a reverse lookup distance map: given a node ID, you can lookup the
     * distances <strong>TO</strong> this node <strong>FROM</strong> all its parents in the list of
     * <code>DistanceNodeTupel</code>s.
     */
    private Map<Integer, List<DistanceNodeTupel>> distanceToMap = new HashMap<>();

    /* A queue that contains the IDs which it performs its BFS. */
    private Deque<Integer> stack = new LinkedList<>();

    /**
     * This uses an iterative algorithm to determine the longest path in a given graph map (sequencesDataMap) and uses a
     * Breadth First Search. It bootstraps a queue and from this it retrieves the node from the given
     * <code>Int2ObjectLinkedOpenHashMap</code> and finds all the children of this node and puts their ID's on the
     * queue. The algorithm maintains a <code>Map</code> with on each entry a <code>List</code> of
     * <code>DistanceNodeTupel</code>. Each entry in the list has node ID and the distance TO this node ID from the
     * source node. It finds for each node ID it encounters 5
     * 
     * 
     * @param sequencesDataMap
     */
    public void findLongestPathBFS(final Int2ObjectLinkedOpenHashMap<Node> sequencesDataMap, final int sourceNodeId, final int targetNodeId) {
        log.info("Calculating longest path from {} to {}", sourceNodeId, targetNodeId);
        int lastId = 0;
        initForSourceNode(sourceNodeId);
        while (!stack.isEmpty()) {
            lastId = stack.pop();
            if (calcBFSLongestPathNumberOfSteps(sequencesDataMap.get(lastId), targetNodeId)) {
                stack.clear();
            }
        }
        printOutDistancesToThisNode(lastId);
        log.info("Calculating longest path ... DONE");
    }

    private boolean calcBFSLongestPathNumberOfSteps(final Node fromNode, final int targetNodeId) {
        if (fromNode.getNodeId() == targetNodeId) {
            return true;
        } else {
            int dist = getMaxDistanceToThisNode(fromNode.getNodeId());
            for (Node outNode : fromNode.getOutEdges()) {
                if (!distanceToMap.containsKey(outNode.getNodeId())) {
                    distanceToMap.put(outNode.getNodeId(), new ArrayList<DistanceNodeTupel>()); // init list
                }
                int distanceToThisOutNode = dist + 1;
                distanceToMap.get(outNode.getNodeId()).add(new DistanceNodeTupel(fromNode.getNodeId(), distanceToThisOutNode));
                stack.add(outNode.getNodeId());
            }
            return false;
        }
    }

    private void initForSourceNode(final int sourceNodeId) {
        stack.add(sourceNodeId);// start with source node
        distanceToMap.put(sourceNodeId, Arrays.asList(new DistanceNodeTupel(sourceNodeId, 0))); // init map to zero for
                                                                                            // sourcenode
    }

    /**
     * Draft version of method.
     * 
     * Finding an entry ONLY based on the highest distance since we are seeking the largest number of steps of the
     * longest path and not the path itself.
     * 
     * @param toNodeId
     * @return number of steps to this given node id from a source node for which this map is maintained.
     */
    private int getMaxDistanceToThisNode(final int toNodeId) {
        int max = 0;
        for (DistanceNodeTupel t : distanceToMap.get(toNodeId)) {
            if (t.distance > max)
                max = t.distance;
        }
        return max;
    }

    private void printOutDistancesToThisNode(final int fromId) {
        log.info("To node ID: {} ", fromId);
        for (DistanceNodeTupel t : distanceToMap.get(fromId)) {
            log.info("From: {}, dist: {}", t.nodeId, t.distance);
        }
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
