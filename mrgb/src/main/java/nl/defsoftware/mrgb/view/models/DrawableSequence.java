package nl.defsoftware.mrgb.view.models;

import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;

/**
 * @author D.L. Ettema
 *
 */
public class DrawableSequence extends Rectangle {
    
    
    public DrawableSequence(NodeDrawingData nodeDrawingData) {
        
        setWidth(nodeDrawingData.width);
        setHeight(nodeDrawingData.height);

        setFill(Paint.valueOf("DARKRED"));
        setOpacity(0.7);
    }
}
