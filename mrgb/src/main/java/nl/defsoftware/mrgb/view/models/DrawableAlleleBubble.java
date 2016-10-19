package nl.defsoftware.mrgb.view.models;

import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;

/**
 * @author D.L. Ettema
 *
 */
public class DrawableAlleleBubble extends Circle {
    
    
    public DrawableAlleleBubble(int radius) {
        setRadius(radius);
        setFill(Paint.valueOf("PURPLE"));
        setOpacity(1);
    }
    
    public void onChanged() {
        
    }
}
