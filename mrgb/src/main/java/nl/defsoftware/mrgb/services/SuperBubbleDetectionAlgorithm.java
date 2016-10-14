package nl.defsoftware.mrgb.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    
    /* Topological sort maintaining states */
    private List<Boolean> state;
    private int order;
    
    /* backing nodes data structure */
    private Rib[] nodeData;
    /* Topological sorted nodes */
    private List<Rib> ordD; 
    /*nodeID, ordering*/
    private Map<Integer, Integer> ordDM;
    
    /* Result of detectedBubbles */
    private List<Bubble> detectedBubbles = new ArrayList<>();
    
    /* Maintaining various states during the algorithm */
    private List<Rib> candidates = new ArrayList<>();
    private Rib[] previousEntrance;
    private Rib[] alternativeEntrance;
    
    /* ID for the bubbles, has no topological meaning */
    private int bubbleId = 0;
    
    /* OutParent[ordD[v]] = min({ordD[u_i] | (u_i , v) \elem  E}) */
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
        this.ordD = new ArrayList<>(orderedNodes.length);
        this.ordDM = new HashMap<>();
        this.alternativeEntrance = new Rib[orderedNodes.length];
        this.previousEntrance = new Rib[orderedNodes.length];
        
        log.info("Detecting bubbles for {} nodes.", orderedNodes.length);
        
        /* Pre-computation */
//        topologicalSort(orderedNodes);
        hardCodedTopologicalSort();
        preComputeRMQ();
        
        /* Main algorithm */
        superBubble();
    }

    private void hardCodedTopologicalSort() {
        ordD.add(nodeData[0]);
        ordD.add(nodeData[1]);
        ordD.add(nodeData[2]);
        ordD.add(nodeData[10]);
        ordD.add(nodeData[11]);
        ordD.add(nodeData[4]);
        ordD.add(nodeData[8]);
        ordD.add(nodeData[5]);
        ordD.add(nodeData[9]);
        ordD.add(nodeData[6]);
        ordD.add(nodeData[3]);
        ordD.add(nodeData[7]);
        ordD.add(nodeData[12]);
        ordD.add(nodeData[14]);
        ordD.add(nodeData[13]);
    }

    private void topologicalSort(Rib[] orderedNodes) {
        order = orderedNodes.length-1;
        state = new ArrayList<>();
        for (int i = 0; i < orderedNodes.length; i++) {
            state.add(i, Boolean.FALSE);
        }
        recursiveTopologicalSort(orderedNodes[0], 0);
        Collections.reverse(ordD);
    }

    private void recursiveTopologicalSort(Node rib, int orderingIndex) {
        state.set(orderingIndex, Boolean.TRUE);
        for (Node outNode : rib.getOutEdges()) {
            int index = findOrderingIndex(outNode);
            if (state.get(index) == Boolean.FALSE) {
                recursiveTopologicalSort(outNode, index);
            }
        }
        ordD.add((Rib)rib);
//        ordDM.put(rib.getNodeId(), order);
        order--;
    }
    
    private int findOrderingIndex(Node outNode) {
        for (int i = 0; i < nodeData.length; i++) {
            if (nodeData[i].getNodeId() == outNode.getNodeId()) return i;
        }
        return 0;
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
        outChild[ord(v)] = highestId;
    }
    
    /**
     * Main entry point for the algorithm.  
     * @param orderedNodes
     */
    private void superBubble() {
        Rib prevEnt = null;
        for (int vId = 0; vId < ordD.size(); vId++) {
            Rib v = ordD.get(vId);
            alternativeEntrance[vId] = null;
            previousEntrance[vId] = prevEnt;
            if (exit(v)) {
                insertExit(v);
            }
            if (entrance(v)) {
                insertEntrance(v);
                prevEnt = v;
            }
        }

        while (!candidates.isEmpty()) {
            if (entrance(tail())) {
                deleteTail();
            } else {
                reportSuperBubble(head(), tail());
            }
        }
    }

    private void reportSuperBubble(Rib start, Rib exit) {
        if (start == null || exit == null || ord(start) >= ord(exit)) {
            deleteTail();
            return;
        }

        Rib valid = null;
        Rib s = previousEntrance[ord(exit)];

        while (ord(s) >= ord(start)) {
            valid = validateSuperBubble(s, exit);
            if (valid == s || valid == alternativeEntrance[s.getNodeId()] || valid == null) {
                break;
            }
            alternativeEntrance[s.getNodeId()] = valid;
            s = valid;
        }

        deleteTail();

        if (valid == s) {
            report(s, exit);
            while (tail() != s) {
                if (exit(tail())) {
                    reportSuperBubble(next(s), tail());
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

        if (outChildId != end)
            return null; // -1 according to Brankovic paper, p.380

        if (outParentId == start)
            return startVertex;
        else if (entrance(vertex(outParentId)))
            return vertex(outParentId);
        else
            return previousEntrance[vertex(outParentId).getNodeId()];
    }

    

    private int rangeMax(int start, int end) {
        int highestId = Integer.MIN_VALUE;
        for (int i = start; i < end; i++) {
            highestId = Integer.max(outParent[i], highestId);
        }
        return highestId;
    }

    private int rangeMin(int start, int end) {
        int lowestId = Integer.MAX_VALUE;
        for (int i = start; i < end; i++) {
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
            if (p.getOutEdges().size() == 1 /* && p.getOutEdges().contains(t) */) {
                return true;
            }
        }
        return false;
    }

    /**
     * lemma 3: for any superbubble <s,t> in a DAG G, there must exist some
     * child c of s such that c has exactly one parent s.
     * 
     * 
     * @param s
     *            is a parent node which childs are checked if they satisfy
     *            lemma-3.
     * 
     * @return true if there is such a child c from s that has only 1 parent s.
     */
    private boolean entrance(Rib s) {
        for (Node c : s.getOutEdges()) {
            if (c.getInEdges().size() == 1 /* && c.getInEdges().contains(s) */) {
                return true;
            }
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

    private Rib tail() {
        return candidates.get(candidates.size() - 1);
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
