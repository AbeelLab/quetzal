package nl.defsoftware.mrgb.view.models;

import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import nl.defsoftware.mrgb.Constants;

/**
 * @author D.L. Ettema
 *
 */
public class DrawableAlleleBubble extends Rectangle {

    private int id;
    
    public DrawableAlleleBubble(NodeDrawingData nodeDrawingData) {
        setFill(Paint.valueOf("PURPLE"));
        setOpacity((nodeDrawingData.scale*nodeDrawingData.scale) / (Constants.MAX_ZOOM_VALUE / 2));
        
        setLayoutX(nodeDrawingData.xCoordinate);
        setLayoutY(nodeDrawingData.yCoordinate);
        setWidth(nodeDrawingData.width);
        setHeight(nodeDrawingData.height);
        
        this.id = nodeDrawingData.id;
        this.addEventFilter(MouseEvent.MOUSE_ENTERED, new EventHandler<MouseEvent>() {
            public void handle(MouseEvent event) { 
                System.out.println(nodeDrawingData.id);
                event.consume();
            };
        });
    }

    public int getDrawId() {
        return id;
    }
    
    public void onChanged() {

    }
    
    @Override
    public boolean equals(Object o) {
        if (o instanceof DrawableAlleleBubble) {
            DrawableAlleleBubble b = (DrawableAlleleBubble) o;
            return b.getDrawId() == this.getDrawId();
        }
        return false;
    }

//    public double getWidth() {
//        return getRadiusX() * 2.0;
//    }
//    
//    public void setWidth(double width) {
//        setRadiusX(width / 2);
//    }
//
//    public double getHeight() {
//        return getRadiusY() * 2.0;
//    }
//    
//    public void setHeight(double height) {
//        setRadiusY(height / 2);
//    }
}
