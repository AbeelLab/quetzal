package nl.defsoftware.mrgb.graphs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import nl.defsoftware.mrgb.graphs.models.Bubble;
import nl.defsoftware.mrgb.graphs.models.Node;
import nl.defsoftware.mrgb.graphs.models.NodeType;
import nl.defsoftware.mrgb.graphs.models.Rib;
import nl.defsoftware.mrgb.view.controllers.MatchingScoreEntry;

/**
 * A static util class for the graphhandler and its main methods.
 * 
 * @author D.L. Ettema
 *
 */
public class GraphHandlerUtil {

    private static final Logger log = LoggerFactory.getLogger(GraphHandlerUtil.class);
    
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
    public static void determineAndConstructNewBubble(Int2ObjectLinkedOpenHashMap<Rib> graphData) {
        int firstId = graphData.firstIntKey();
        HashSet<Node> visitedNodes = new HashSet<>();

        for (int key = firstId; key < graphData.size(); key++) {
            Node sourceNode = graphData.get(key);
            if (visitedNodes.contains(sourceNode)) {
                continue;
            } else if (sourceNode.getOutEdges().size() > 1) {
                determineAndConstructNewBubble(graphData, visitedNodes, sourceNode);
            }
        }
    }

    /**
     * @param graphMap
     * @param visitedNodes
     * @param sourceNode
     */
    private static void determineAndConstructNewBubble(
            Int2ObjectLinkedOpenHashMap<Rib> graphData,
            HashSet<Node> visitedNodes, 
            Node sourceNode) {
        
        HashSet<Node> nestedNodes = new HashSet<>();

        visitedNodes.add(sourceNode);
        Iterator<Node> iter = sourceNode.getOutEdges().iterator();
        Node firstChildNode = iter.next();
        nestedNodes.add(firstChildNode);
        Node sinkNode = findSinkNodeForSimpleBubble(firstChildNode);

        if (sinkNode != null && isSameSinkNode(nestedNodes, iter, sinkNode, true)) {
            // we found all siblings to be connected to the same
            // sink node
            visitedNodes.addAll(nestedNodes);
            int nodeId = firstChildNode.getNodeId();
            boolean isSnpBubble = true;
            for (Node aNestedNode : nestedNodes) {
                sourceNode.removeOutEdge(aNestedNode);
                sinkNode.removeInEdge(aNestedNode);
                graphData.remove(aNestedNode.getNodeId());
                isSnpBubble = isSnpBubbleType(isSnpBubble, aNestedNode);
            }

            NodeType bubbleType = isSnpBubble ? NodeType.SNP_BUBBLE : NodeType.ALLELE_BUBBLE;
            Bubble bubble = createNewBubble(nodeId, sourceNode, nestedNodes, sinkNode, bubbleType);
            // glue the parent of the source & sink node to this bubble
            sourceNode.addOutEdge(bubble);
            sinkNode.addInEdge(bubble);
//            graphData.put(nodeId, bubble);
        }
    }

    /**
     * Method will add siblingNodes to the given <code>nestedNodes</code> if
     * they are also part of this bubble.
     * 
     * @param nestedNodes
     * @param iter
     * @param sinkNode
     * @param sameSinkNode
     * @return <code>true</code> if the nodes in the iterator have the same
     *         sinkNode as the given <code>sinkNode</code>
     */
    private static boolean isSameSinkNode(HashSet<Node> nestedNodes, Iterator<Node> iter, Node sinkNode,
            boolean sameSinkNode) {
        while (iter.hasNext() && sameSinkNode) {
            Node siblingNode = iter.next();
            /*
             * This constraint determines if the sink node is still the same as
             * the first sink node found from the frist child node.
             */
            Node tmpNode = findSinkNodeForSimpleBubble(siblingNode);
            if (tmpNode == null || sinkNode.getNodeId() != tmpNode.getNodeId()) {
                sameSinkNode = false;
            } else {
                nestedNodes.add(siblingNode);
            }
        }
        return sameSinkNode;
    }

    private static boolean isSnpBubbleType(boolean isSnpBubble, Node aNestedNode) {
        if (aNestedNode.isNotComposite() && isSnpBubble && aNestedNode instanceof Rib) {
            return ((Rib) aNestedNode).getSequence().length == 1;
        } else {
            return isSnpBubble;
        }
    }

    /**
     * @param sourceNode
     * @param nestedNodes
     * @param firstChildNode
     * @param sinkNode
     * @param bubbleType
     * @return
     */
    private static Bubble createNewBubble(int nodeId, Node sourceNode, HashSet<Node> nestedNodes, Node sinkNode,
            NodeType bubbleType) {
        Bubble bubble = new Bubble(nodeId, bubbleType, sourceNode, sinkNode);
        bubble.setNestedNodes(nestedNodes);
        return bubble;
    }

    /**
     * This method assumes the given <code>node</code> is the mutation and its
     * only child is the sink for a possible bubble.
     * 
     * @param node
     * @return The node that is the sink for a bubble or <code>null</code> if otherwise
     */
    private static Node findSinkNodeForSimpleBubble(Node node) {
        if (node.getInEdges().size() == 1 && node.getOutEdges().size() == 1) {
            return node.getOutEdges().iterator().next();
        } else {
            return null; // this node is part of a more complex bubble
                         // structure.
        }
    }

    /**
     * @deprecated
     * 
     * @param node
     * @return
     */
    private static boolean isSequenceSingleNucleotide(Node node) {
        if (node.isNotComposite()) {
            Rib rib = (Rib) node;
            return (rib.getSequence().length == 1);
        }
        return false;
    }

    /**
     * An array is filled with node id's that may be used to visit these nodes
     * later for edge drawing.
     * 
     * @param edgeMapping
     * @param fromId
     * @param toId
     */
    public static void catalogEdgesForDrawing(Int2ObjectOpenHashMap<int[]> edgeMapping, int fromId, int[] toId) {
        for (int i = 0; i < toId.length; i++) {
//            if (toId[i] >= 285) {
//                log.info("edgesToQueue to: {} fromId: {}", toId[i], fromId);
//            }
            if (edgeMapping.containsKey(toId[i])) {
                int[] fromEdges = edgeMapping.get(toId[i]);
                int[] tmpFromEdges = Arrays.copyOf(fromEdges, fromEdges.length + 1);
                tmpFromEdges[tmpFromEdges.length - 1] = fromId;
                edgeMapping.put(toId[i], tmpFromEdges);
            } else {
                edgeMapping.put(toId[i], new int[] { fromId });
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
     * @param graphData
     * @return List<MatchingScoreEntry>
     */
    public static List<MatchingScoreEntry> determineSortedNodeRanking(Node aRib, int[] parentNodes, Int2ObjectLinkedOpenHashMap<Node> graphData) {
        List<MatchingScoreEntry> scoring = new ArrayList<>();

        for (int i = 0; i < parentNodes.length; i++) {
            Node parentRib = graphData.get(parentNodes[i]);
            int[] siblingIds = parentRib.getConnectedEdges();
            for (int j = 0; j < siblingIds.length; j++) {
                Node siblingRib = graphData.get(siblingIds[j]);
                if (siblingRib != null) {
//                    calculateNaiveRanking(scoring, siblingRib.getNodeId(), siblingRib.getGenomeIds(), parentRib);
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
    public static List<MatchingScoreEntry> determineSortedEdgeRanking(Node aRib, int[] parentNodes, Int2ObjectLinkedOpenHashMap<Node> graphMap) {
        List<MatchingScoreEntry> scoring = new ArrayList<>();

        for (int i = 0; i < parentNodes.length; i++) {
            Node parentRib = graphMap.get(parentNodes[i]);
            int[] siblingIds = parentRib.getConnectedEdges();

            for (int j = 0; j < siblingIds.length; j++) {
                Node siblingRib = graphMap.get(siblingIds[j]);
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

    private static void calculateMatchingScore(List<MatchingScoreEntry> scoresList, int childNodeId, short[] childGenomeIds, Node parentRib) {
        int matchingScore = 0;
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
    
    private static void calculateNaiveRanking(List<MatchingScoreEntry> scoresList, int childNodeId, short[] childGenomeIds, Node parentRib) {
        scoresList.add(new MatchingScoreEntry((int)parentRib.getXCoordinate(), parentRib, childNodeId));
    }

//    public static final int MINUMUM_BASE_SIZE = 150;
//    public static double calculateNodeHeight(Rib aRib, double zoomFactor) {
//        int nodeSize = aRib.getSequence().length;
//        double height = nodeSize * zoomFactor;
//        if (nodeSize < MINUMUM_BASE_SIZE && height < DECENT_NODE_HEIGHT) {
//          height = MINUMUM_BASE_SIZE * zoomFactor;
//          if (height > DECENT_NODE_HEIGHT) {
//            return DECENT_NODE_HEIGHT;
//          }
//        }
//        return height;
//      }
    
}
