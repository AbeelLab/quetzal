package nl.defsoftware.mrgb.view.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.scene.shape.Shape;

/**
 * @author D.L. Ettema
 * @date: 27 September 2016
 *
 */
public class RibbonGraphModel extends GraphModel {

    private static final int LINE_ENDING_LENGTH = 8;
    
    private List<Shape> allEdges;
    private Map<Integer, RibbonSequence> sequenceMap;
    
    public RibbonGraphModel() {
        super();
        this.clear();
    }
    
    @Override
    public void addSequence(Integer id, int x, int y) {
        RibbonSequence seq = new RibbonSequence();
        seq.relocate(x, y);
        sequenceMap.put(id, seq);
        super.addedSequences.add(seq);
    }
    
    @Override
    public void addEdge(int id, int startX, int startY, int endX, int endY, int rank) {
        if (rank == 0) { //backbone part
            RibbonLine ribbonLine = new RibbonLine(0, determineLength(startY, endY));
            ribbonLine.relocate(startX, startY);
            allEdges.add(ribbonLine);
        } else {
            RibbonCurve ribbon = new RibbonCurve(rank, startX, startY, endX, endY, isOpeningCurve(startX, endX));
            allEdges.add(ribbon);
        }
    }
    
    public void addEdge(int fromId, int toId, int rank) {
        RibbonSequence from = sequenceMap.get(fromId);
        RibbonSequence to = sequenceMap.get(toId);
        
        RibbonLine ribbonLine = new RibbonLine(from, to);
//        ribbonLine.relocate(startX, startY);
        allEdges.add(ribbonLine);
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
