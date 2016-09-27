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

//    private Shape graphParentNode;
//    private List<Shape> allShapes;
//    private Map<Integer, Shape> shapesMap; // <id,Sequence>
    
    public RibbonGraphModel() {
        super();
//        graphParentNode = new Ribbon(new Integer(0));
    }
    
    public RibbonCurve addRibbon(Integer id, int x, int y) {
        RibbonCurve ribbon = new RibbonCurve(id, true);
        ribbon.relocate(x, y);
        addRibbon(ribbon, x);
        return ribbon;
    }
    
    private void addRibbon(RibbonCurve ribbon, int xCoord) {
        addedSequences.add(ribbon);
    }
}
