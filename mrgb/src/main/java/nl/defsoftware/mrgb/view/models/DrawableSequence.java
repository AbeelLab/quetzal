package nl.defsoftware.mrgb.view.models;


import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;

/**
 * @author D.L. Ettema
 *
 */
public class DrawableSequence extends Rectangle {
    
    private int id;
    
    public DrawableSequence(NodeDrawingData nodeDrawingData) {
        setFill(Paint.valueOf("DARKRED"));
        setOpacity(0.7);
        setHover(true);
        setStrokeType(StrokeType.OUTSIDE);
        setStrokeWidth(2.0);
        setStroke(Paint.valueOf("BLACK"));
        
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
    
    @Override
    public boolean equals(Object o) {
        if (o instanceof DrawableSequence) {
            DrawableSequence b = (DrawableSequence) o;
            return b.getDrawId() == this.getDrawId();
        }
        return false;
    }
}
