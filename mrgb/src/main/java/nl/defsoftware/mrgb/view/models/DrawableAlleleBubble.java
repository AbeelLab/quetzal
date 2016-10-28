package nl.defsoftware.mrgb.view.models;

import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;

/**
 * @author D.L. Ettema
 *
 */
public class DrawableAlleleBubble extends Circle {
    
    
    public DrawableAlleleBubble() {
        setFill(Paint.valueOf("PURPLE"));
        setOpacity(0.7);
    }
    
    public void onChanged() {
        
    }
}
