package nl.defsoftware.mrgb.view.models;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.shape.Shape;

/**
 * @author D.L. Ettema
 * @date: 27 September 2016
 *
 */
public class RibbonGraphModel extends GraphModel {

    private List<Shape> allEdges;
    
    public RibbonGraphModel() {
        super();
        this.clear();
    }
    
    @Override
    public void addSequence(Integer id, int x, int y) {
        RibbonSequence seq = new RibbonSequence();
        seq.relocate(x, y);
        super.addedSequences.add(seq);
    }
    
    @Override
    public void addEdge(int id, int startX, int startY, int endX, int endY, int rank) {
        if (rank == 0) { //backbone part
            RibbonLine ribbonLine = new RibbonLine(determineLength(startY, endY));
            ribbonLine.relocate(startX, startY);
            allEdges.add(ribbonLine);
        } else {
            RibbonCurve ribbon = new RibbonCurve(rank, startX, startY, isOpeningCurve(startX, endX));
            allEdges.add(ribbon);
        }
    }

    @Override
    public void clear() {
        super.clear();
        allEdges = new ArrayList<>();
    }

    @Override
    public void merge() {
        super.merge();
    }
    
    public List<Shape> getAllEdges() {
        return this.allEdges;
    }
    
    private int determineLength(int startY, int endY) {
        return endY - startY - 8;
    }

    private boolean isOpeningCurve(int startX, int endX) {
        return endX - startX > 0;
    }
}
