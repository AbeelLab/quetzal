package nl.defsoftware.mrgb.services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap;
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

    /* Topological sorted nodes */
    private List<Node> ordD;
    /* Arrays that are shadowing the index of Ribs from ordD */
    private Node[] previousEntrance;
    private Node[] alternativeEntrance;

    /* Result of detectedBubbles */
    private Int2ObjectLinkedOpenHashMap<Bubble> detectedBubbles = new Int2ObjectLinkedOpenHashMap<>();

    /* Maintaining candidate bubble nodes during the algorithm */
    private List<Node> candidates = new ArrayList<>();

    /* ID for the bubbles, has no topological meaning */
    private int bubbleId = 0;

    /* OutParent[ordD[v]] = min({ordD[u_i] | (u_i , v) \elem E}) */
    private int[] outParent;
    /* OutChild[ordD[v]] = max({ordD[u_i] | (v, u_i) \elem E}) */
    private int[] outChild;

    /**
     * Main entry point into algorithm.
     * 
     * @param orderedNodes
     */
    public void detectSuperBubbles(Node[] orderedNodes) {
        // TODO make sure that all starting nodes are connected to one source
        // and all leaf nodes are connected to 1 sink node
        this.alternativeEntrance = new Node[orderedNodes.length];
        this.previousEntrance = new Node[orderedNodes.length];
        this.outParent = new int[orderedNodes.length];
        this.outChild = new int[orderedNodes.length];

        log.info("Detecting bubbles for {} nodes.", orderedNodes.length);

        /* Pre-computation */
        ordD = SuperBubbleDetectionHelper.topologicalSort(orderedNodes);
        orderedNodes = null;
        SuperBubbleDetectionHelper.preComputeRMQ(ordD, outParent, outChild);

        /* Main algorithm */
        superBubble();
        log.info("Bubble detection ... DONE");
    }

    /**
     * Retrieve the detected bubbles.
     * 
     * @return List of <code>Bubble</code>'s
     */
    public Int2ObjectLinkedOpenHashMap<Bubble> getDetectedBubbles() {
        return detectedBubbles;
    }

    private void superBubble() {
        log.debug("Ordering size: {}", ordD.size());
        Node prevEnt = null;
        for (int i = 0; i < ordD.size(); i++) {
            Node v = ordD.get(i);
            log.debug("Order({}): node({})", i, v.getNodeId());
            alternativeEntrance[i] = null;
            previousEntrance[i] = prevEnt;// filled according to the ordD
                                          // ordering
            if (exit(v)) {
                log.debug("New exit node({})", v.getNodeId());
                insertExit(v);
            }
            if (entrance(v)) {
                log.debug("New entrance node({})", v.getNodeId());
                insertEntrance(v);
                prevEnt = v;
            }
        }

        log.debug("\nCandidates size: {}", candidates.size());
        while (!candidates.isEmpty()) {
            if (entrance(tail())) {
                deleteTail();
            } else {
                reportSuperBubble(head(), tail());
            }
        }
    }

    private void reportSuperBubble(Node start, Node exit) {
        log.debug("start({}) on order: {}\nexit({}) on order: {}", start.getNodeId(), ord(start), exit.getNodeId(),
                ord(exit));

        if (start == null || exit == null || ord(start) >= ord(exit)) {
            deleteTail();
            return;
        }

        Node valid = null;
        Node s = previousEntrance[ord(exit)];

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
            report(s, exit); // yea we found a bubble!
            while (tail() != null && !tail().equals(s)) {
                if (exit(tail())) {
                    reportSuperBubble(next(s), tail());// see if there are any
                                                       // inner superbubbles
                } else {
                    deleteTail();
                }
            }
        }
    }

    private Node validateSuperBubble(Node startVertex, Node endVertex) {
        int start = ord(startVertex);
        int end = ord(endVertex);
        int outChildId = rangeMax(start, end - 1);
        int outParentId = rangeMin(start + 1, end);

        log.debug("rangeMax(childID:{}) & end({}), rangeMin(parentID:{}) & start({}) ", outChildId, end, outParentId,
                start);
        if (outChildId != end) {
            return null; // -1 according to Brankovic paper, p.380
        }

        if (outParentId == start) {
            return startVertex;
        } else if (entrance(vertex(outParentId))) {
            return vertex(outParentId);
        } else {
            return previousEntrance[ord(vertex(outParentId))];
        }
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

    private void report(Node start, Node exit) {
        Set<Node> innerNodes = new HashSet<>();
        findInnerNodes(innerNodes, start.getOutEdges(), exit);
        Bubble b = new Bubble(start.getNodeId(), NodeType.ALLELE_BUBBLE, start, exit);
        b.setNestedNodes(innerNodes);
//        log.debug("Bubble, startId({}) and stopId({})", start.getNodeId(), exit.getNodeId());
        detectedBubbles.put(start.getNodeId(), b);
    }

    private void findInnerNodes(Set<Node> innerNodes, Collection<Node> startNodes, Node exit) {
        for (Node out : startNodes) {
            if (out.getNodeId() != exit.getNodeId()) {
                innerNodes.add(out);
                findInnerNodes(innerNodes, out.getOutEdges(), exit);
            }
        }
    }

    private Node vertex(int i) {
        return ordD.get(i);
    }

    /**
     * Lemma-2: For any superbubble <s, t> in G, there must exist some parent p
     * of t such that p has exactly one child t.
     * 
     * @param v
     * @return
     */
    private boolean exit(Node t) {
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
    private boolean entrance(Node s) {
        for (Node c : s.getOutEdges()) {
            if (c.getInEdges().size() == 1) {
                return true;
            }
        }
        return false;
    }

    private void insertExit(Node v) {
        v.setAsBubbleExitNode();
        candidates.add(v);
    }

    private void insertEntrance(Node v) {
        v.setAsBubbleEntranceNode();
        candidates.add(v);
    }

    private Node head() {
        return candidates.get(0);
    }

    private Node tail() {
        if (candidates.size() > 0) {
            return candidates.get(candidates.size() - 1);
        } else {
            return null;
        }
    }

    private void deleteTail() {
        if (!candidates.isEmpty()) {
            candidates.remove(candidates.size() - 1);
        }
    }

    private Node next(Node v) {
        for (int i = 0; i < candidates.size(); i++) {
            if (candidates.get(i).equals(v) && i + 1 < candidates.size()) {
                return candidates.get(i + 1);
            }
        }
        return null;
    }
}
