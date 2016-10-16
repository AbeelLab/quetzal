package nl.defsoftware.mrgb.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import nl.defsoftware.mrgb.models.Rib;
import nl.defsoftware.mrgb.models.graph.Node;

/**
 * @author D.L. Ettema
 *
 */
public class SuperBubbleDetectionHelper {

    /* Topological sort maintaining states */
    private static List<Boolean> state;

    public static List<Rib> topologicalSort(Rib[] orderedNodes) {
        List<Rib> resultOrdering = new ArrayList<>(orderedNodes.length);
        state = new ArrayList<>();
        for (int i = 0; i < orderedNodes.length; i++) {
            state.add(i, Boolean.FALSE);
        }
        recursiveTopologicalSort(resultOrdering, orderedNodes, orderedNodes[0], 0);
        Collections.reverse(resultOrdering);
        
        return resultOrdering;
    }

    private static void recursiveTopologicalSort(List<Rib> resultOrdering, Rib[] orderedNodes, Node rib, int orderingIndex) {
        state.set(orderingIndex, Boolean.TRUE);
        for (Node outNode : rib.getOutEdges()) {
            int index = findOrderingIndex(orderedNodes, outNode);
            if (state.get(index) == Boolean.FALSE) {
                recursiveTopologicalSort(resultOrdering, orderedNodes, outNode, index);
            }
        }
        resultOrdering.add((Rib) rib);
    }

    private static int findOrderingIndex(Rib[] orderedNodes, Node outNode) {
        for (int i = 0; i < orderedNodes.length; i++) {
            if (orderedNodes[i].getNodeId() == outNode.getNodeId())
                return i;
        }
        return 0;
    }
}
