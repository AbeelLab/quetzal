package nl.defsoftware.mrgb.services;

import java.util.ArrayList;
import java.util.List;

import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap;
import nl.defsoftware.mrgb.models.Rib;

/**
 * @author D.L. Ettema
 *
 */
public class BubbleDetectionAlgorithm {

    List<Rib> candidates = new ArrayList<>();
    
    public void superBubble(Int2ObjectLinkedOpenHashMap<Rib> graphData) {
        Rib prevEnt = null;
        Rib[] alternativeEntrance = new Rib[graphData.size()];
        Rib[] previousEntrance = new Rib[graphData.size()];
        for (int i = 0; i < graphData.size(); i++) {
            Rib v = graphData.get(i);
            alternativeEntrance[i] = null;
            previousEntrance[i] = prevEnt;
            if(exit(v)) {
                insertExit(v);
            }
            if (entrance(v)) {
                insertEntrance(v);
                prevEnt = v;
            }
        }
        
        while(!candidates.isEmpty()) {
            if (entrance(tail())) {
                deleteTail();
            } else {
                reportSuperBubble(head(), tail());
            }
        }
    }
    
    private void reportSuperBubble(Rib head, Rib tail) {
        
    }
    
    private boolean exit(Rib v) {
        return true; //satisfies lemma-2 otherwise return false;
    }
    
    private void insertExit(Rib v) {
        v.setAsBubbleExitNode();
        candidates.add(v);
    }
    
    private boolean entrance(Rib v) {
        return true; //satisfies lemma-3, false otherwise
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
}
