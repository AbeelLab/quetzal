package nl.defsoftware.mrgb.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ArrayUtils;

import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import nl.defsoftware.mrgb.models.Rib;
import nl.defsoftware.mrgb.view.controllers.MatchingScoreEntry;

/**
 * A static util class for the graphhandler and its main methods.
 * 
 * @author D.L. Ettema
 *
 */
public class GraphHandlerUtil {

    /**
     * <p>
     * Definition of a simple bubble: Given a grid on which we draw a DAG where
     * nodes are placed along the y-axis and stacked on the x-axis <br />
     * - a node u at position y has out-degree of t where t >= 2. <br />
     * - a node v at position y + 2 with an in-degree the same as t.
     * </p>
     * 
     * @param graphMap
     */
    public static void detectSimpleBubbles(Int2ObjectLinkedOpenHashMap<Rib> graphMap) {

        int sourceNode = graphMap.firstIntKey();

        for (int key = sourceNode; key < graphMap.size(); key++) {
            Rib rib = graphMap.get(key);
            if (rib.getConnectedEdges().length > 1) {
                short[] startIds = rib.getGenomeIds();
                Set<Short> startGenomeIds = new HashSet(Arrays.asList(ArrayUtils.toObject(startIds)));
                
            }
        }
    }
    


    /**
     * An array is filled with node id's that may be used to visit these nodes
     * later for edge drawing.
     * 
     * @param edgeQueue
     * @param fromId
     * @param toId
     */
    public static void addEdgesToQueue(Int2ObjectOpenHashMap<int[]> edgeQueue, int fromId, int[] toId) {
        for (int i = 0; i < toId.length; i++) {
            if (edgeQueue.containsKey(toId[i])) {
                int[] fromEdges = edgeQueue.get(toId[i]);
                int[] tmpFromEdges = Arrays.copyOf(fromEdges, fromEdges.length + 1);
                tmpFromEdges[tmpFromEdges.length - 1] = fromId;
                edgeQueue.put(toId[i], tmpFromEdges);
            } else {
                edgeQueue.put(toId[i], new int[] { fromId });
            }
        }
    }

    /**
     * This method will score and rank the matching genome id's between a child
     * node and its parent nodes in a greedy determination and provide a ranking
     * of heaviest parent edge. A rank number rank(p,c): determined by the
     * number of genomes shared between a child node <code>c</code> and a parent
     * node <code>p</code> ranked in relation to the other number of genomes
     * shared between <code>c</code> other direct parent nodes <code>p'</code>.
     * A score 0 means it is equal to the backbone and higher then 0 means it is
     * shifted from the backbone and should be placed in a high lane parallel to
     * the backbone.
     * 
     * REFACTOR to the use of streams
     * 
     * @param aRib
     * @param parentNodes
     * @param graphMap
     * @return List<MatchingScoreEntry>
     */
    public static List<MatchingScoreEntry> determineSortedNodeRanking(Rib aRib, int[] parentNodes,
            Int2ObjectLinkedOpenHashMap<Rib> graphMap) {
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

    /**
     * Similar method to <code>determineSortedNodeRanking</code> but removes
     * scores where a child node is also present as a parent node, since these
     * might be InDels. An InDel causes a sibling node to be counted as a parent
     * node, which gives a incorrect edge ranking.
     * 
     * @param aRib
     * @param parentNodes
     * @param graphMap
     * @return
     */
    public static List<MatchingScoreEntry> determineSortedEdgeRanking(Rib aRib, int[] parentNodes,
            Int2ObjectLinkedOpenHashMap<Rib> graphMap) {
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

    /**
     * Method to determine if a sibling is not also a parent based on the given
     * array
     * 
     * @param siblingId
     * @param parentNodes
     * @return
     */
    private static boolean siblingIsNotAParentNode(int siblingId, int[] parentNodes) {
        for (int i = 0; i < parentNodes.length; i++) {
            if (parentNodes[i] == siblingId) {
                return false;
            }
        }
        return true;
    }

    private static void calculateMatchingScore(List<MatchingScoreEntry> scoresList, int childNodeId,
            short[] childGenomeIds, Rib parentRib) {
        short matchingScore = 0;
        short[] parentGenomeIds = parentRib.getGenomeIds();
        for (int j = 0; j < parentGenomeIds.length; j++) {
            for (int k = 0; k < childGenomeIds.length; k++) {
                if (parentGenomeIds[j] == childGenomeIds[k]) {
                    matchingScore++;
                }
            }
        }
        scoresList.add(new MatchingScoreEntry(matchingScore, parentRib, childNodeId));
    }

}
