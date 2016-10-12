package nl.defsoftware.mrgb.services;

import java.util.ArrayList;
import java.util.List;

import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap;
import nl.defsoftware.mrgb.models.Rib;
import nl.defsoftware.mrgb.models.graph.Node;

/**
 * @author D.L. Ettema
 *
 */
public class BubbleDetectionAlgorithm {

    List<Rib> candidates = new ArrayList<>();
    Rib[] previousEntrance;
    Rib[] alternativeEntrance;

    public void superBubble(Int2ObjectLinkedOpenHashMap<Rib> graphData) {
        // TODO make sure that all starting nodes are connected to one source
        // and all leaf nodes are connected to 1 sink node
        // TODO reverse topological order

        Rib prevEnt = null;
        alternativeEntrance = new Rib[graphData.size()];
        previousEntrance = new Rib[graphData.size()];

        for (int vId = 0; vId < graphData.size(); vId++) {
            Rib v = graphData.get(vId);
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
        if (start == null || exit == null || start.getNodeId() >= exit.getNodeId()) {
            deleteTail();
            return;
        }
        Rib s = previousEntrance[exit.getNodeId()];
        while (s.getNodeId() >= start.getNodeId()) {
            Rib valid = validateSuperBubble(s, exit);
            if (valid == s || valid == alternativeEntrance[s.getNodeId()] || valid == -1) {
                break;
            }
            alternativeEntrance[s.getNodeId()] = valid;
            s = valid;
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
    }

    private boolean exit(Rib v) {
        return true; // satisfies lemma-2 otherwise return false;
    }

    private void insertExit(Rib v) {
        v.setAsBubbleExitNode();
        candidates.add(v);
    }

    /**
     * lemma 3: for any superbubble <s,t> in a DAG G, there must exist some
     * child c of s such that c has exactly one parent s.
     * 
     * 
     * @param s is a parent node which childs are checked if they satisfy lemma-3.
     * 
     * @return true if there is such a child c from s that has only 1 parent s.
     */
    private boolean entrance(Rib s) {
        for (Node c : s.getOutEdges()) {
            if (c.getInEdges().size() == 1 && c.getInEdges().contains(s)) {
                return true;
            }
        }
        return false;
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
}
