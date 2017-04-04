package nl.defsoftware.mrgb.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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
     * This <code>Map</code> contains the cumulative distances. Key value is the node ID to which the distance applies.
     * Each value contains a <code>DistanceNodeTupel</code> with the parent node ID of the key node ID with its
     * cumulative distance. In essence, this is a reverse lookup distance map: given a node ID, you can lookup the
     * distances <strong>TO</strong> this node <strong>FROM</strong> all its parents in the list of
     * <code>DistanceNodeTupel</code>s.
     */
    private Map<Integer, DistanceNodeTupel> distanceToNodeMap = new HashMap<>();

    private int distances[];

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
    public List<Integer> findLongestPathBFS(List<Integer> path, final Int2ObjectLinkedOpenHashMap<Node> sequencesDataMap, final int sourceNodeId,
            final int targetNodeId) {
        log.info("Calculating longest path from {} to {}", sourceNodeId, targetNodeId);
        printMemoryUsage();
        distances = new int[sequencesDataMap.size() + 1];
        calcBFSLongestPath(sequencesDataMap, sourceNodeId, targetNodeId);
        printMemoryUsage();
        log.info("Distance to nodeID({}) is {} steps", distanceToNodeMap.get(targetNodeId).nodeId, distanceToNodeMap.get(targetNodeId).distance);
        log.info("Calculating longest path ... DONE");
        return findLongestPath(path, targetNodeId);
    }

    private List<Integer> findLongestPath(List<Integer> path, final int targetNodeId) {
        path.add(targetNodeId);
        DistanceNodeTupel t = distanceToNodeMap.get(targetNodeId);
        for (int i = 0; i < distanceToNodeMap.size(); i++) {
            path.add(t.nodeId);
            t = distanceToNodeMap.get(t.nodeId);
            if (t == null) {
                break;
            }
        }
        return path;
    }

    private void calcBFSLongestPath(final Int2ObjectLinkedOpenHashMap<Node> sequencesDataMap, final int sourceNodeId,
            final int targetNodeId) {
        List<Integer> sortedList = new ArrayList(sequencesDataMap.keySet());
        Collections.sort(sortedList);
        for (int key : sortedList) {
            Node parentNode = sequencesDataMap.get(key);
            if (parentNode.getNodeId() >= sourceNodeId && parentNode.getNodeId() <= targetNodeId) { //limiting scope
                for (Node childNode : parentNode.getOutEdges()) {
                    if (distances[childNode.getNodeId()] < distances[parentNode.getNodeId()] + 1) {
                        distances[childNode.getNodeId()] = distances[parentNode.getNodeId()] + 1;
                        distanceToNodeMap.put(childNode.getNodeId(), new DistanceNodeTupel(parentNode.getNodeId(), distances[parentNode.getNodeId()] + 1));
                    }
                }
            }
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

    private void printMemoryUsage() {
        Runtime runtime = Runtime.getRuntime();
        long bytes = (runtime.totalMemory() - runtime.freeMemory());
        double megabytes = bytes / 1024.0 / 1024.0;
        log.info("memory = " + String.format(Locale.US, "%.3f", megabytes) + " MB");
    }
}
