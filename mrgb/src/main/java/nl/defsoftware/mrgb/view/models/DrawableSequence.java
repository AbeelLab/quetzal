package nl.defsoftware.mrgb.view.models;

import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;

/**
 * @author D.L. Ettema
 *
 */
public class DrawableSequence extends Rectangle {
    
    
    public DrawableSequence(NodeDrawingData nodeDrawingData) {
        super(nodeDrawingData.xCoordinate, nodeDrawingData.yCoordinate, nodeDrawingData.width, nodeDrawingData.height);
        setFill(Paint.valueOf("DARKRED"));
        setOpacity(0.7);
        
        setStrokeType(StrokeType.OUTSIDE);
        setStrokeWidth(2.0);
        setStroke(Paint.valueOf("BLACK"));
    }
}
