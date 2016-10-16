package nl.defsoftware.mrgb.view.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javafx.scene.shape.Shape;
import nl.defsoftware.mrgb.models.graph.Node;

/**
 * @author D.L. Ettema
 * @date: 27 September 2016
 *
 */
public class RibbonGraphModel extends GraphModel {

    private static final int LINE_ENDING_LENGTH = 8;
    
    private List<Shape> allEdges;
    private Map<Integer, DrawableSequence> sequenceMap;
    
    public RibbonGraphModel() {
        super();
        this.clear();
    }
    
    /**
     * TODO
     * 
     * @param aNode
     * @param parentNode
     * @param rank
     */
    public void addSequence(Node aNode, Node parentNode, int rank) {
        Set<Node> nodesToDraw = new HashSet<>();
        switch (aNode.getNodeType()) {
        case SINGLE_NODE:
            break;
        case SNP_BUBBLE:
            break;
        case INDEL_BUBBLE:
            break;
        case ALLELE_BUBBLE:
            break;
        default:
            break;
        }
    }
    
    @Override
    public void addSequence(Integer id, int x, int y) {
        DrawableSequence seq = new DrawableSequence();
        seq.relocate(x, y);
        sequenceMap.put(id, seq);
        super.addedSequences.add(seq);
    }
    
    @Override
    public void addEdge(int id, int startX, int startY, int endX, int endY, int rank) {
        if (rank == 0) { //backbone part
            DrawableEdge ribbonLine = new DrawableEdge(0, determineLength(startY, endY));
            ribbonLine.relocate(startX, startY);
            allEdges.add(ribbonLine);
        } else {
            DrawableEdgeCurve ribbon = new DrawableEdgeCurve(rank, startX, startY, endX, endY, isOpeningCurve(startX, endX));
            allEdges.add(ribbon);
        }
    }
    
    public void addEdge(int fromId, int toId, int rank) {
        DrawableSequence from = sequenceMap.get(fromId);
        DrawableSequence to = sequenceMap.get(toId);
        allEdges.add(new DrawableEdge(from, to));
    }

    @Override
    public void clear() {
        super.clear();
        allEdges = new ArrayList<>();
        sequenceMap = new HashMap<>();
    }

    @Override
    public void merge() {
        super.merge();
    }
    
    public List<Shape> getAllEdges() {
        return this.allEdges;
    }
    
    private int determineLength(int start, int end) {
        return end - start - LINE_ENDING_LENGTH;
    }

    private boolean isOpeningCurve(int startX, int endX) {
        return endX - startX > 0;
    }
}
