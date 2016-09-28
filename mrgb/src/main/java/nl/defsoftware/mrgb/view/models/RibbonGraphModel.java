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
    public void addEdge(Integer startx, Integer toId) {
//        RibbonLine ribbonLine = new RibbonLine(fromId);
//        ribbonLine.setStartX(value);
//        ribbonLine.relocate(, y-100);
//        super.addedEdges.add(ribbonLine);
    }
}
