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
        if (rank > 0) {
            addRibbonCurve(1, startX, startY, true);
            addRibbonLine(1, startX, startY, 1);
        } else {
            addRibbonLine(1, startX, startY, 1);
        }
    }
    
    private void addRibbonLine(int id, float x, float y, float length) {
        RibbonLine ribbonLine = new RibbonLine(id);
        ribbonLine.relocate(x + 3, y + 5);//TODO remove manual radius of sequence node adjustment
        super.addedSequences.add(ribbonLine);
    }
    
    private void addRibbonCurve(int id, int startX, int startY, boolean isOpeningCurve) {
        RibbonCurve ribbon = new RibbonCurve(id, isOpeningCurve);
        ribbon.relocate(startX + 4, startY+5);
        super.addedSequences.add(ribbon);
    }
}
