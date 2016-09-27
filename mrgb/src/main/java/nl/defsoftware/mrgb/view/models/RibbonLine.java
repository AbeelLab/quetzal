package nl.defsoftware.mrgb.view.models;

import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeType;

/**
 * 
 * @author D.L. Ettema
 * @date 27 September 2016
 *
 */
public class RibbonLine extends Line {

    private Integer id;
    
    public RibbonLine(Integer id) {
        this.id = id;
        
        setStrokeLineCap(StrokeLineCap.BUTT);
        setStrokeWidth(5.0);
        setStroke(Color.valueOf("black"));
        setStrokeType(StrokeType.CENTERED);
        setFill(Color.valueOf("black"));
    }
    
    
}
