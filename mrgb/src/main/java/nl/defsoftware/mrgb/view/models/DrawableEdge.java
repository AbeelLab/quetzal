package nl.defsoftware.mrgb.view.models;

import javafx.scene.paint.Paint;
import javafx.scene.shape.Line;

/**
 * 
 * @author D.L. Ettema
 * @date 27 September 2016
 *
 */
public class DrawableEdge extends Line {

    
    public DrawableEdge(double xDelta, double yDelta) {
        
        setEndX(xDelta);
        setEndY(yDelta);

        setStrokeWidth(3.0);
        setFill(Paint.valueOf("BLACK"));
        setOpacity(0.5);
        setStartX(0);
        setStartY(0);
        setRotate(0.0);
    }
    
    public DrawableEdge(DrawableSequence from, DrawableSequence to) {
        
        setStrokeWidth(3.0);
        setFill(Paint.valueOf("BLACK"));
        setOpacity(0.5);
        
        startXProperty().bind(from.layoutXProperty().add(from.getBoundsInParent().getWidth() / 2.0));
        startYProperty().bind(from.layoutYProperty().add(from.getBoundsInParent().getHeight() / 2.0));

        endXProperty().bind(to.layoutXProperty().add( to.getBoundsInParent().getWidth() / 2.0));
        endYProperty().bind(to.layoutYProperty().add( to.getBoundsInParent().getHeight() / 2.0));
    }
    
    public double getLength() {
        return getEndY();
    }
    
}
