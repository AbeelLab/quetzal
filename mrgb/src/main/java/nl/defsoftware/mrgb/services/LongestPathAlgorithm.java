package nl.defsoftware.mrgb.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.unimi.dsi.fastutil.ints.Int2ObjectAVLTreeMap;
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
    private Int2ObjectAVLTreeMap<List<DistanceNodeTupel>> distanceToMap = new Int2ObjectAVLTreeMap<>();

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
    public int findLongestPathBFS(final Int2ObjectLinkedOpenHashMap<Node> sequencesDataMap, final int sourceNodeId,
            final int targetNodeId) {
        log.info("Calculating longest path from {} to {}", sourceNodeId, targetNodeId);
        initForSourceNode(sourceNodeId);
        printMemoryUsage();
        while (!stack.isEmpty()) {
            int id = stack.pop();
            calcBFSLongestPathNumberOfSteps(sequencesDataMap.get(id), targetNodeId);
        }
        log.info("Size of distanceToMap: {}", distanceToMap.size());
        printMemoryUsage();
        printOutDistancesToThisNode(targetNodeId);
        log.info(printLongestPath(targetNodeId));
        log.info("Calculating longest path ... DONE");
        return getMaxDistanceToThisNode(targetNodeId);
    }

    private boolean calcBFSLongestPathNumberOfSteps(final Node fromNode, final int targetNodeId) {
        if (fromNode.getNodeId() == targetNodeId) {
            return true;
        } else {
            final int distFromNode = getMaxDistanceToThisNode(fromNode.getNodeId());
            int cumulativeDistanceToThisFromNode = distFromNode + 1;// cumulative distance
            for (Node childNode : fromNode.getOutEdges()) {
//                updateOrAddTupel(childNode, fromNode, cumulativeDistanceToThisFromNode);
                updateTupel(childNode, fromNode, cumulativeDistanceToThisFromNode);
                stack.add(childNode.getNodeId());
            }
            return false;
        }
    }

    /**
     * This method only provides one longest path and does not record multiple longest paths, but only the first one it encouters.
     * 
     * @param childNode
     * @param fromNode
     * @param distanceToThisChildNode
     */
    private void updateTupel(final Node childNode, final Node fromNode, final int distanceToThisChildNode) {
        if (distanceToMap.containsKey(childNode.getNodeId())) {
            for (DistanceNodeTupel t : distanceToMap.get(childNode.getNodeId())) {
                if (t.distance < distanceToThisChildNode) {
                    t.distance = distanceToThisChildNode;
                    t.nodeId = fromNode.getNodeId();
                }
            }
        } else {
            distanceToMap.put(childNode.getNodeId(), new ArrayList<>(Arrays.asList(new DistanceNodeTupel(fromNode.getNodeId(), distanceToThisChildNode)))); // init
        }
    }
    
    private void updateOrAddTupel(final Node childNode, final Node fromNode, final int distanceToThisChildNode) {
        if (distanceToMap.containsKey(childNode.getNodeId())) {
            boolean isTupelExists = false;
            for (DistanceNodeTupel t : distanceToMap.get(childNode.getNodeId())) {
                if (t.nodeId == fromNode.getNodeId()) {
                    isTupelExists = true;
                    if (t.distance < distanceToThisChildNode) {
                        t.distance = distanceToThisChildNode;
                    }
                }
            }
            if (!isTupelExists) {
                distanceToMap.get(childNode.getNodeId()).add(new DistanceNodeTupel(fromNode.getNodeId(), distanceToThisChildNode));
            }
        } else {
            distanceToMap.put(childNode.getNodeId(), new ArrayList<>(Arrays.asList(new DistanceNodeTupel(fromNode.getNodeId(), distanceToThisChildNode)))); // init
        }
    }

    private void initForSourceNode(final int sourceNodeId) {
        stack.add(sourceNodeId);// start with source node
        distanceToMap.put(sourceNodeId, Arrays.asList());// init map with empty list for sourcenode
    }

    /**
     * Finding an entry ONLY based on the highest distance since we are seeking the largest number of steps of the
     * longest path and not the path itself.
     * 
     * @param toNodeId
     * @return Number of steps to this given node id from a source node for which this map is maintained.
     */
    private int getMaxDistanceToThisNode(final int toNodeId) {
        int max = 0;
        for (DistanceNodeTupel t : distanceToMap.get(toNodeId)) {
            if (t.distance > max)
                max = t.distance;
        }
        return max;
    }

    private void printOutDistancesToThisNode(final int toId) {
        log.info("To node ID: {} ", toId);
        for (DistanceNodeTupel t : distanceToMap.get(toId)) {
            log.info("From: {}, dist: {}", t.nodeId, t.distance);
        }
    }

    private String printLongestPath(final int toId) {
        String pathString = toId + " ";
        int max = getMaxDistanceToThisNode(toId);
        for (DistanceNodeTupel t : distanceToMap.get(toId)) {
            if (t.distance == max) {
                pathString = pathString.concat(printLongestPath(t.nodeId));
            }
        }
        return pathString.concat("]");
    }

    private class DistanceNodeTupel {
        public int nodeId;
        public int distance;

        public DistanceNodeTupel(int nodeId, int distance) {
            this.nodeId = nodeId;
            this.distance = distance;
        }
    }

    private void printMemoryUsage() {
        Runtime runtime = Runtime.getRuntime();
        long bytes = (runtime.totalMemory() - runtime.freeMemory());
        double megabytes = bytes / 1024.0 / 1024.0;
        log.info("memory = " + String.format(Locale.US, "%.3f", megabytes) + " MB");
    }
}
