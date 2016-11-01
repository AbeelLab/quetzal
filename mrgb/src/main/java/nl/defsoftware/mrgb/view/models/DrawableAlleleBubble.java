package nl.defsoftware.mrgb.view.models;

import javafx.scene.paint.Paint;
import javafx.scene.shape.Ellipse;

/**
 * @author D.L. Ettema
 *
 */
public class DrawableAlleleBubble extends Ellipse {

    public DrawableAlleleBubble() {
        setFill(Paint.valueOf("PURPLE"));
        setOpacity(0.7);
    }

    public void onChanged() {

    }

    public double getWidth() {
        return getRadiusX() * 2.0;
    }
    
    public void setWidth(double width) {
        setRadiusX(width / 2);
    }

    public double getHeight() {
        return getRadiusY() * 2.0;
    }
    
    public void setHeight(double height) {
        setRadiusY(height/2);
    }
}
