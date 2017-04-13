package nl.defsoftware.mrgb.services;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntSortedSet;
import nl.defsoftware.mrgb.models.graph.Bubble;
import nl.defsoftware.mrgb.models.graph.Node;

/**
 * @author D.L. Ettema
 *
 */
public class GraphDrawerToGrid {
    
    private GridHandler gridHandler;
    
    private Int2ObjectLinkedOpenHashMap<Node> graphData;
    private Int2ObjectLinkedOpenHashMap<Bubble> bubbles;
    private Set<Integer> drawnSubGraphNodeIds;
    
    private Int2ObjectOpenHashMap<int[]> edgeMapping;
    private Int2ObjectOpenHashMap<int[]> bubbleMapping;
    private List<Integer> backbone = new ArrayList<>();
    
    public GraphDrawerToGrid(GridHandler gridHandler) {
        this.gridHandler = gridHandler;
        this.drawnSubGraphNodeIds = new HashSet<>();
        this.edgeMapping = new Int2ObjectOpenHashMap<>();
        this.bubbleMapping = new Int2ObjectOpenHashMap<>();
    }
    
    public void mapGraphToGrid(Int2ObjectLinkedOpenHashMap<Node> graphData, Int2ObjectLinkedOpenHashMap<Bubble> bubbles) {
        this.graphData = graphData;
        this.bubbles = bubbles;
        int [] keys = graphData.keySet().toIntArray();
        Node sourceNode = graphData.get(keys[0]);
        gridHandler.addOrUpdateNodeInGrid(sourceNode, 0, 0);//first node
        int count = countChildren(sourceNode);
        //which node is on longest path
        //draw main path node
        //draw other nodes with spacing according to the amount of children they have 
        int levelCursor = 1;
        //topological order
        for (int i = 1; i < keys.length; i++) {
            Node node = graphData.get(keys[i]);
            countChildren((Node[]) node.getOutEdges().toArray());
            gridHandler.addOrUpdateNodeInGrid(node, levelCursor, isBackbone(node));
        }
    }
    
    private short isBackbone(Node node) {
        return backbone.contains(Integer.valueOf(node.getNodeId())) ? gridHandler.BACKBONE_NODE : gridHandler.NON_BACKBONE_NODE;
    }
    
    private int countChildren(Node ... nodes) {
        int total = 0;
        for (Node node : nodes) {
            total += node.getOutEdges().size();
        }
        return total;
    }
}
