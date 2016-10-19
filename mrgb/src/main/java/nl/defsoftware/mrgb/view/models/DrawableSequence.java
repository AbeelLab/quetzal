package nl.defsoftware.mrgb.view.models;

import javafx.beans.property.DoubleProperty;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;

/**
 * @author D.L. Ettema
 *
 */
public class DrawableSequence extends Rectangle {
    
    
    public DrawableSequence(DoubleProperty scaleYProperty) {
        
        setWidth(3.0);
        setHeight(5.0);

        setFill(Paint.valueOf("DARKRED"));
        setOpacity(0.5);
    }
}
