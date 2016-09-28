package nl.defsoftware.mrgb.view.models;

/**
 * @author D.L. Ettema
 * @date: 27 September 2016
 *
 */
public class RibbonGraphModel extends GraphModel {

//    private Shape graphParentNode;
//    private List<Shape> allShapes;
//    private Map<Integer, Shape> shapesMap; // <id,Sequence>
    
    public RibbonGraphModel() {
        super();
//        graphParentNode = new Ribbon(new Integer(0));
    }
    
    @Override
    public void addSequence(Integer id, int x, int y) {
        RibbonCurve ribbon = new RibbonCurve(id, false);
        ribbon.relocate(x, y);
        super.addedSequences.add(ribbon);
        RibbonCurve ribbon2 = new RibbonCurve(id, true);
        ribbon2.relocate(x+100, y+100);
        super.addedSequences.add(ribbon2);
        RibbonLine ribbonLine = new RibbonLine(id);
        ribbonLine.relocate(x-100, y-100);
        super.addedSequences.add(ribbonLine);
    }
    
    @Override
    public void addEdge(int id, int startX, int startY, int endX, int endY, int rank) {
        if (rank > 0) {
            addRibbonCurve(1, startX, startY, true);
            addRibbonLine(1, startX, startY, endX, endY);
            addRibbonCurve(1, endX, endY, false);
        } else {
            addRibbonLine(1, startX, startY, endX, endY);
        }
    }
    
    private void addRibbonLine(int id, int startX, int startY, int endX, int endY) {
        RibbonLine ribbonLine = new RibbonLine(id);
        ribbonLine.setStartX(startX);
        ribbonLine.setStartY(startY);
        ribbonLine.setEndX(endX);
        ribbonLine.setEndY(endY);
        super.addedSequences.add(ribbonLine);
    }
    
    private void addRibbonCurve(int id, int startX, int startY, boolean isOpeningCurve) {
        RibbonCurve ribbon = new RibbonCurve(id, isOpeningCurve);
        ribbon.relocate(startX, startY);
        super.addedSequences.add(ribbon);
    }
}
