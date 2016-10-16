package nl.defsoftware.mrgb.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jgrapht.DirectedGraph;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.traverse.TopologicalOrderIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.defsoftware.mrgb.models.Rib;
import nl.defsoftware.mrgb.models.graph.Bubble;
import nl.defsoftware.mrgb.models.graph.Node;
import nl.defsoftware.mrgb.models.graph.NodeType;

/**
 * Implementation of algorithm proposed by Brankovic, L et al. in 2016. Titled:
 * Linear-time superbubble identification algorithm for genome assembly.
 * doi:10.1016/j.tcs.2015.10.021
 * 
 * @author D.L. Ettema
 *
 */
public class SuperBubbleDetectionAlgorithm {

    private static final Logger log = LoggerFactory.getLogger(SuperBubbleDetectionAlgorithm.class);

    /* backing nodes data structure */
    private Rib[] nodeData;
    /* Topological sorted nodes */
    private List<Rib> ordD;
    /* nodeID, ordering */
    private Map<Integer, Integer> ordDM;

    /* Result of detectedBubbles */
    private List<Bubble> detectedBubbles = new ArrayList<>();

    /* Maintaining various states during the algorithm */
    private List<Rib> candidates = new ArrayList<>();
    private Rib[] previousEntrance;
    private Rib[] alternativeEntrance;

    /* ID for the bubbles, has no topological meaning */
    private int bubbleId = 0;

    /* OutParent[ordD[v]] = min({ordD[u_i] | (u_i , v) \elem E}) */
    private int[] outParent;
    /* OutChild[ordD[v]] = max({ordD[u_i] | (v, u_i) \elem E}) */
    private int[] outChild;

    /**
     * graphData.values().toArray(new Rib[graphData.size()])
     * 
     * @param orderedNodes
     */
    public void detectSuperBubbles(Rib[] orderedNodes) {
        // TODO make sure that all starting nodes are connected to one source
        // and all leaf nodes are connected to 1 sink node
        this.nodeData = orderedNodes;
        this.ordDM = new HashMap<>();
        this.alternativeEntrance = new Rib[orderedNodes.length];
        this.previousEntrance = new Rib[orderedNodes.length];

        log.info("Detecting bubbles for {} nodes.", orderedNodes.length);

        /* Pre-computation */
        ordD = SuperBubbleDetectionHelper.topologicalSort(orderedNodes);
        preComputeRMQ();

        /* Main algorithm */
        superBubble();
    }


    /**
     * This will compute the Range Minimum Query (RMQ) problem as described in
     * section 4 of the Brankovic paper.
     */
    private void preComputeRMQ() {
        outParent = new int[ordD.size()];
        outChild = new int[ordD.size()];
        for (int i = 0; i < ordD.size(); i++) {
            preComputeRMQParents(ordD.get(i));
            preComputeRMQChilds(ordD.get(i));
        }
    }

    /**
     * OutParent[ordD[v]] = min({ordD[u_i] | (u_i , v) \elem E}),
     * 
     * @param v
     */
    private void preComputeRMQParents(Rib v) {
        int lowestId = Integer.MAX_VALUE;
        for (Node parent : v.getInEdges()) {
            lowestId = Integer.min(ord(parent), lowestId);
        }
        if (lowestId != Integer.MAX_VALUE)
            outParent[ord(v)] = lowestId;
    }

    /**
     * OutChild[ordD[v]] = max({ordD[u_i] | (v, u_i) \elem E}).
     * 
     * @param v
     */
    private void preComputeRMQChilds(Rib v) {
        int highestId = Integer.MIN_VALUE;
        for (Node child : v.getOutEdges()) {
            if (ord(child) > highestId) {
                highestId = ord(child);
            }
        }
        if (highestId != Integer.MIN_VALUE)
            outChild[ord(v)] = highestId;
    }

    /**
     * Main entry point for the algorithm.
     * 
     * @param orderedNodes
     */
    private void superBubble() {
        log.info("Ordering size: {}", ordD.size()); 
        Rib prevEnt = null;
        for (int i = 0; i < ordD.size(); i++) {
            Rib v = ordD.get(i);
            log.info("Order({}): node({})", i, v.getNodeId());
            alternativeEntrance[i] = null;
            previousEntrance[i] = prevEnt;
            if (exit(v)) {
                log.info("New exit node({})", v.getNodeId());
                insertExit(v);
            }
            if (entrance(v)) {
                log.info("New entrance node({})", v.getNodeId());
                insertEntrance(v);
                prevEnt = v;
            }
        }

        log.info("\nCandidates size: {}", candidates.size());
        while (!candidates.isEmpty()) {
            Rib tail = tail(candidates);
            if (entrance(tail)) {
                log.info("Deleting tail node({}) since it is also entrance candidate", tail.getNodeId());
                deleteTail();
            } else {
                reportSuperBubble(head(), tail);
            }
        }
    }

    private void reportSuperBubble(Rib start, Rib exit) {
        log.info("start({}) on order: {}\nexit({}) on order: {}", start.getNodeId(), ord(start), exit.getNodeId(), ord(exit));
        
        if (start == null || exit == null || ord(start) >= ord(exit)) {
            deleteTail();
            return;
        }

        Rib valid = null;
        Rib s = previousEntrance[ord(exit)];

        while (ord(s) >= ord(start)) {
            valid = validateSuperBubble(s, exit);
            if (valid == null || valid.equals(s) || valid.equals(alternativeEntrance[ord(s)])) {
                break;
            }
            alternativeEntrance[ord(s)] = valid;
            s = valid;
        }

        deleteTail();

        if (valid != null && valid.equals(s)) {
            report(s, exit);
            while (tail(candidates) != null && !tail(candidates).equals(s)) {
                if (exit(tail(candidates))) {
                    reportSuperBubble(next(s), tail(candidates));
                } else {
                    deleteTail();
                }
            }
        }
    }

    private Rib validateSuperBubble(Rib startVertex, Rib endVertex) {
        int start = ord(startVertex);
        int end = ord(endVertex);
        int outChildId = rangeMax(start, end - 1);
        int outParentId = rangeMin(start + 1, end);
        
        log.info("rangeMax(childID:{}) & end({}), rangeMin(parentID:{}) & start({}) ", outChildId, end, outParentId, start);
        if (outChildId != end)
            return null; // -1 according to Brankovic paper, p.380

        if (outParentId == start)
            return startVertex;
        else if (entrance(vertex(outParentId)))
            return vertex(outParentId);
        else
            return previousEntrance[ord(vertex(outParentId))];
    }

    private int rangeMax(int start, int end) {
        int highestId = Integer.MIN_VALUE;
        for (int i = start; i <= end; i++) {
            highestId = Integer.max(outChild[i], highestId);
        }
        return highestId;
    }

    private int rangeMin(int start, int end) {
        int lowestId = Integer.MAX_VALUE;
        for (int i = start; i <= end; i++) {
            lowestId = Integer.min(outParent[i], lowestId);
        }
        return lowestId;
    }

    private int ord(Node v) {
        return ordD.indexOf(v);
    }

    private void report(Rib start, Rib exit) {
        detectedBubbles.add(new Bubble(bubbleId, NodeType.ALLELE_BUBBLE, start, exit));
        bubbleId++;
    }

    private Rib vertex(int i) {
        return ordD.get(i);
    }

    /**
     * Lemma-2: For any superbubble <s, t> in G, there must exist some parent p
     * of t such that p has exactly one child t.
     * 
     * @param v
     * @return
     */
    private boolean exit(Rib t) {
        for (Node p : t.getInEdges()) {
            if (p.getOutEdges().size() == 1) {
                return true;
            }
        }
        return false;
    }

    /**
     * lemma 3: for any superbubble <s,t> in a DAG G, there must exist some
     * child c of s such that c has exactly one parent s.
     * 
     * @param s
     *            is a parent node which childs are checked if they satisfy
     *            lemma-3.
     * 
     * @return true if there is such a child c from s that has only 1 parent s.
     */
    private boolean entrance(Rib s) {
        for (Node c : s.getOutEdges()) {
            if (c.getInEdges().size() == 1)
                return true;
        }
        return false;
    }

    private void insertExit(Rib v) {
        v.setAsBubbleExitNode();
        candidates.add(v);
    }

    private void insertEntrance(Rib v) {
        v.setAsBubbleEntranceNode();
        candidates.add(v);
    }

    private Rib head() {
        return candidates.get(0);
    }

    private Rib tail(List<Rib> list) {
        if (list != null && list.size() > 0) return list.get(list.size() - 1);
        else return null;
    }

    private void deleteTail() {
        candidates.remove(candidates.size() - 1);
    }

    private Rib next(Rib v) {
        if (v.getNodeId() + 1 < candidates.size()) {
            return candidates.get(v.getNodeId() + 1);
        }
        return null;
    }

    public List<Bubble> getDetectedBubbles() {
        return detectedBubbles;
    }

}
