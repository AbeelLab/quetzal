package nl.defsoftware.mrgb.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import nl.defsoftware.mrgb.graphs.models.Node;
import nl.defsoftware.mrgb.graphs.models.Rib;

/**
 * @author D.L. Ettema
 *
 */
public class SuperBubbleDetectionHelper {

    /* Topological sort maintaining states */
    private static List<Boolean> state;

    public static List<Node> topologicalSort(Node[] orderedNodes) {
        List<Node> result = new ArrayList<>(orderedNodes.length);
        state = new ArrayList<>();
        for (int i = 0; i < orderedNodes.length; i++) {
            state.add(i, Boolean.FALSE);
        }
        recursiveTopologicalSort(result, orderedNodes, orderedNodes[0], 0);
        Collections.reverse(result);
        
        return result;
    }

    private static void recursiveTopologicalSort(List<Node> resultOrdering, Node[] orderedNodes, Node aNode, int orderingIndex) {
        state.set(orderingIndex, Boolean.TRUE);
        for (Node outNode : aNode.getOutEdges()) {
            int index = findOrderingIndex(orderedNodes, outNode);
            if (state.get(index) == Boolean.FALSE) {
                recursiveTopologicalSort(resultOrdering, orderedNodes, outNode, index);
            }
        }
        resultOrdering.add((Rib) aNode);
    }

    /**
     * TODO can be optimised to work with a depleting stack.
     * 
     * @param orderedNodes
     * @param outNode
     * @return
     */
    private static int findOrderingIndex(Node[] orderedNodes, Node outNode) {
        for (int i = 0; i < orderedNodes.length; i++) {
            if (orderedNodes[i].getNodeId() == outNode.getNodeId())
                return i;
        }
        return 0;
    }
    
    /**
     * This will compute the Range Minimum Query (RMQ) problem as described in
     * section 4 of the Brankovic paper.
     */
    public static void preComputeRMQ(List<Node> ordD, int[] outParent, int[] outChild) {
        for (int i = 0; i < ordD.size(); i++) {
            preComputeRMQParents(ordD, ordD.get(i), outParent);
            preComputeRMQChilds(ordD, ordD.get(i), outChild);
        }
    }

    /**
     * OutParent[ordD[v]] = min({ordD[u_i] | (u_i , v) \elem E}),
     * 
     * @param v
     */
    private static void preComputeRMQParents(List<Node> ordD, Node v, int[] outParent) {
        int lowestId = Integer.MAX_VALUE;
        for (Node parent : v.getInEdges()) {
            lowestId = Integer.min(ordD.indexOf(parent), lowestId);
        }
        if (lowestId != Integer.MAX_VALUE) {
            outParent[ordD.indexOf(v)] = lowestId;
        }
    }

    /**
     * OutChild[ordD[v]] = max({ordD[u_i] | (v, u_i) \elem E}).
     * 
     * @param v
     */
    private static void preComputeRMQChilds(List<Node> ordD, Node v, int[] outChild) {
        int highestId = Integer.MIN_VALUE;
        for (Node child : v.getOutEdges()) {
            highestId = Integer.max(ordD.indexOf(child), highestId);
        }
        if (highestId != Integer.MIN_VALUE) {
            outChild[ordD.indexOf(v)] = highestId;
        }
    }
}
