package nl.defsoftware.mrgb.view.models;

import javafx.scene.shape.Shape;

/**
 * @author D.L. Ettema
 * @date: 27 September 2016
 *
 */
public class RibbonGraphModel extends GraphModel {

    private Shape graphParentNode;
//    private List<Shape> allShapes;
//    private Map<Integer, Shape> shapesMap; // <id,Sequence>
    
    public RibbonGraphModel() {
        super();
        graphParentNode = new Sequence(new Integer(0));
    }
    
    @Override
    public void addSequence(Integer id, int x, int y) {
        Sequence seq = new Sequence(id);
        seq.relocate(x, y);
        super.addedSequences.add(seq);
    }
    
    @Override
    public void addEdge(int id, int startX, int startY, int endX, int endY, int rank) {
        if (rank == 0) { //backbone part
            addRibbonLine(1, startX, startY, determineLength(startY, endY));
        } else {
            addRibbonCurve(1, startX, startY, rank, isOpeningCurve(startX, endX));
        }
    }

    private int determineLength(int startY, int endY) {
        return endY - startY;
    }

    private boolean isOpeningCurve(int startX, int endX) {
        return endX - startX > 0;
    }
    
    private void addRibbonLine(int id, int startX, int startY, int length) {
        RibbonLine ribbonLine = new RibbonLine(id, length);
        ribbonLine.relocate(startX + 3, startY + 5);//TODO remove manual radius of sequence node adjustment
        super.addedSequences.add(ribbonLine);
    }
    
    private void addRibbonCurve(int id, int startX, int startY, int rank, boolean isOpeningCurve) {
        RibbonCurve ribbon = new RibbonCurve(id, rank, startX, startY, isOpeningCurve);
//        ribbon.relocate(startX + 4, startY+5);
        super.addedSequences.add(ribbon);
    }
}
