package nl.defsoftware.mrgb.view.models;

/**
 * @author D.L. Ettema
 * @date: 27 September 2016
 *
 */
public class RibbonGraphModel extends GraphModel {

    public RibbonGraphModel() {
        super();
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
            addRibbonLine(1, startX, startY+5, determineLength(startY, endY));
        } else {
            addRibbonCurve(1, startX, startY, rank, isOpeningCurve(startX, endX));
        }
    }

    private int determineLength(int startY, int endY) {
        return endY - startY - 8;
    }

    private boolean isOpeningCurve(int startX, int endX) {
        return endX - startX > 0;
    }
    
    private void addRibbonLine(int id, int startX, int startY, int length) {
        RibbonLine ribbonLine = new RibbonLine(length);
        ribbonLine.relocate(startX, startY);//TODO remove manual radius of sequence node adjustment
        super.addedSequences.add(ribbonLine);
    }
    
    private void addRibbonCurve(int id, int startX, int startY, int rank, boolean isOpeningCurve) {
        RibbonCurve ribbon = new RibbonCurve(rank, startX, startY, isOpeningCurve);
//        ribbon.relocate(startX+1, startY+2);
        super.addedSequences.add(ribbon);
    }
}
