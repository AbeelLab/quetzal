package nl.defsoftware.mrgb.view;

import java.util.Observable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.EventHandler;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.ScrollEvent;
import javafx.scene.transform.Scale;
import nl.defsoftware.mrgb.Constants;
import nl.defsoftware.mrgb.view.controllers.GraphController;

/**
 * @author D.L. Ettema
 *
 */
public class ScrollAndZoomHandler extends Observable implements EventHandler<ScrollEvent> {

    private static final Logger log = LoggerFactory.getLogger(ScrollAndZoomHandler.class);
    private ScrollPane scrollPane;
    private GraphController graphController;
    
    //zooming
    private double scaleValue = Constants.MAX_ZOOM_VALUE;
    private double zoomDelta = 5.0;
    private Scale scaleTransform;
    
    private double vValue;
    private double height;
    
    //scrolling
    private DoubleProperty scrollDeltaProperty = new SimpleDoubleProperty(0.0);

    //zooming to point
    private double xMouseSource;
    private double yMouseSource;

    /** TODO this should only accept listeners for the different actions that it handles. 
     * Thus only a zoom listener, scroll listener and maybe zoom to point listener
     * 
     * @param scrollPane
     * @param graphController
     */
    public ScrollAndZoomHandler(ScrollPane scrollPane, GraphController graphController) {
        this.scrollPane = scrollPane;
        this.graphController = graphController;
        scaleTransform = new Scale(scaleValue, scaleValue);
    }
    
    
    @Override
    public void handle(ScrollEvent event) {
        if (event.isControlDown()) {
            //zooming
            if (event.getDeltaY() < 0) {
                scaleValue = Math.min(scaleValue + zoomDelta, Constants.MAX_ZOOM_VALUE);
            } else {
                scaleValue = Math.max(scaleValue - zoomDelta, Constants.MIN_ZOOM_VALUE);
            }
            zoomTo(scaleValue);
            zoomEventSource(event.getSceneX(), event.getSceneY());
            System.out.println("window.height:    " + scrollPane.getHeight());
            System.out.println("nodePane.height:    " + scrollPane.getContent().getBoundsInLocal().getHeight()); 
            event.consume();
        } else {
            //scrolling
            scrollTo(event.getDeltaY());
//            System.out.println("viewportBounds.height: " + scrollPane.getViewportBounds().getHeight());
            System.out.println("content.BIL.height:    " + scrollPane.getContent().getBoundsInLocal().getHeight());
            height = scrollPane.getContent().getBoundsInLocal().getHeight();
            System.out.println("VValue: " + scrollPane.getVvalue());
            vValue = scrollPane.getVvalue();
            System.out.println("scrollEvent.getDeltaY():    " + event.getDeltaY());
//            System.out.println("VMax:   " + scrollPane.getVmax());
//            System.out.println("VMin:   " + scrollPane.getVmin());
//            System.out.println("MousePointer.inScene(x,y): (" + event.getSceneX() + "," + event.getSceneY() + ")");
        }
    }
    
    public void zoomTo(double scaleValue) {
        this.scaleValue = scaleValue;
        scaleTransform.setY(scaleValue);
        //should be listener notifier system
        super.notifyObservers();
        graphController.notifyUpdateView();
    }

    public void scrollTo(double scrollDelta) {
        scrollDeltaProperty.set(scrollDelta);
    }

    public DoubleProperty getScaleYProperty() {
        return scaleTransform.yProperty();
    }
    
    public double getScaleValue() {
        return scaleValue;
    }
    
    public double getVValue() {
        return vValue;
    }
    
    public double getHeightOfWindow() {
        return height;
    }
    
    public void zoomEventSource(double xMouseCoordinate, double yMouseCoordinate) {
        xMouseSource = xMouseCoordinate;
        yMouseSource = yMouseCoordinate;
    }

    /**
     * 
     * @param minimizeOnly
     *            If the content fits already into the viewport, then we don't zoom if this parameter is true.
     */
    public void zoomToFit(boolean minimizeOnly) {
        double scaleX = scrollPane.getViewportBounds().getWidth() / scrollPane.getContent().getBoundsInLocal().getWidth();
        double scaleY = scrollPane.getViewportBounds().getHeight() / scrollPane.getContent().getBoundsInLocal().getHeight();

        // consider current scale (in content calculation)
        scaleX *= scaleValue;
        scaleY *= scaleValue;

        // distorted zoom: we don't want it => we search the minimum scale
        // factor and apply it
        double scale = Math.min(scaleX, scaleY);
        // check precondition
        if (minimizeOnly) {
            // check if zoom factor would be an enlargement and if so, just set
            // it to 1
            if (Double.compare(scale, 1) > 0) {
                scale = 1;
            }
        }
        // apply zoom
        zoomTo(scale);
    }
}
