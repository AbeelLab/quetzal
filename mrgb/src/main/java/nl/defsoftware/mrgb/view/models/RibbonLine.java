package nl.defsoftware.mrgb.view.models;

import javafx.scene.paint.Paint;
import javafx.scene.shape.Line;

/**
 * 
 * @author D.L. Ettema
 * @date 27 September 2016
 *
 */
public class RibbonLine extends Line {

    
    public RibbonLine(int length) {
        
        setEndY(length);

        setStrokeWidth(3.0);
        setFill(Paint.valueOf("BLACK"));
        setOpacity(0.5);
        setStartX(0);
        setStartY(0);
        setEndX(0);
        setRotate(0.0);
    }
    
    
}
