package nl.defsoftware.mrgb.view.controllers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import nl.defsoftware.mrgb.models.Rib;

/**
 * @author D.L. Ettema
 *
 */
public class GraphHandlerUtil {

    
    public static void addEdgesToQueue(Int2ObjectOpenHashMap<int[]> edgeQueue, int fromId, int[] toId) {
        for (int i = 0; i < toId.length; i++) {
            if (edgeQueue.containsKey(toId[i])) {
                int[] fromEdges = edgeQueue.get(toId[i]);
                int[] newFromEdges = Arrays.copyOf(fromEdges, fromEdges.length + 1);
                newFromEdges[newFromEdges.length - 1] = fromId;
                edgeQueue.put(toId[i], newFromEdges);
            } else {
                edgeQueue.put(toId[i], new int[] { fromId });
            }
        }
    }
    
    /**
     * Ranking based on matching genome id's. 
     * A rank number rank(p,c): determined by the number of genomes shared
     * between a child node 'c' and a parent node 'p' ranked in relation to the
     * other number of genomes shared between 'c' other parent nodes 'p' ' of
     * that child node. A number 0 means it is equal to the backbone. A number
     * higher then 0 means it is shifted from the backbone and should be placed
     * in a high lane parallel to the backbone.
     * 
     * REFACTOR to the use of streams
     * 
     * @param aRib
     * @param parentNodes
     * @param graphMap
     * @return List<MatchingScoreEntry>
     */
    public static List<MatchingScoreEntry> determineSortedNodeRanking(Rib aRib, int[] parentNodes, Int2ObjectLinkedOpenHashMap<Rib> graphMap) {
        List<MatchingScoreEntry> scoring = new ArrayList<>();
        
        for (int i = 0; i < parentNodes.length; i++) {
            Rib parentRib = graphMap.get(parentNodes[i]);
            int[] siblingIds = parentRib.getConnectedEdges();
            for (int j = 0; j < siblingIds.length; j++) {
                Rib siblingRib = graphMap.get(siblingIds[j]);
                if (siblingRib != null) {
                    calculateMatchingScore(scoring, siblingRib.getNodeId(), siblingRib.getGenomeIds(), parentRib);
                }
            }
        }
        Collections.sort(scoring);
        return scoring;
    }
    
    public static List<MatchingScoreEntry> determineSortedEdgeRanking(Rib aRib, int[] parentNodes, Int2ObjectLinkedOpenHashMap<Rib> graphMap) {
        List<MatchingScoreEntry> scoring = new ArrayList<>();
        
        for (int i = 0; i < parentNodes.length; i++) {
            Rib parentRib = graphMap.get(parentNodes[i]);
            int[] siblingIds = parentRib.getConnectedEdges();
            
            for (int j = 0; j < siblingIds.length; j++) {
                Rib siblingRib = graphMap.get(siblingIds[j]);
                if (siblingRib != null && siblingIsNotAParentNode(siblingIds[j], parentNodes)) {
                    calculateMatchingScore(scoring, siblingRib.getNodeId(), siblingRib.getGenomeIds(), parentRib);
                }
            }
        }
        Collections.sort(scoring);
        return scoring;
    }
    
    private static boolean siblingIsNotAParentNode(int siblingId, int[] parentNodes) {
        for (int i = 0; i < parentNodes.length; i++) {
            if (parentNodes[i] == siblingId) { return false; }
        }
        return true;
    }

    private static void calculateMatchingScore(List<MatchingScoreEntry> scoresList, int childNodeId, short[] childGenomeIds, Rib parentRib) {
        short matchingScore = 0;
        short[] parentGenomeIds = parentRib.getGenomeIds();
        for (int j = 0; j < parentGenomeIds.length; j++) {
            for (int k = 0; k < childGenomeIds.length; k++) {
                if (parentGenomeIds[j] == childGenomeIds[k]) { matchingScore++; }
            }
        }
        scoresList.add(new MatchingScoreEntry(matchingScore, parentRib, childNodeId)) ;
    }
    
}
