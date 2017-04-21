package nl.defsoftware.mrgb.view;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.ScrollEvent;
import javafx.scene.transform.Scale;
import nl.defsoftware.mrgb.Constants;
import nl.defsoftware.mrgb.view.controllers.GraphController;

/**
 * ZoomableScrollPane
 *
 *
 * @author http://stackoverflow.com/questions/30679025/graph-visualisation-like- yfiles-in-javafx
 * 
 * @date 6 Jun 2016
 */
public class GraphScrollPane extends ScrollPane {

    private static final Logger log = LoggerFactory.getLogger(GraphScrollPane.class);

    @FXML
    private ScrollBar scrollbar;
    
    private Group zoomGroup;
    private Scale scaleTransform;
    private double scaleValue = Constants.MAX_ZOOM_VALUE;
    private double zoomDelta = 5.0;
    private double xMouseSource;
    private double yMouseSource;
    private DoubleProperty scrollDeltaProperty = new SimpleDoubleProperty(0.0);

    // should be interface for listener object so different things can listen to changes
    private GraphController graphController;

    public GraphScrollPane(Group content) {
        scaleTransform = new Scale(scaleValue, scaleValue);
        zoomGroup = content;
        // zoomGroup.getTransforms().add(scaleTransform);
        scrollbar = new ScrollBar();
    //        scrollbar.prefHeightProperty().bind(this.heightProperty());
    //        scrollbar.prefWidthProperty().bind(this.widthProperty());
        setContent(zoomGroup);
        setManaged(true);
    }
    
    public void setOnScrollEventHandler(EventHandler<ScrollEvent> handler) {
        zoomGroup.setOnScroll(handler);
    }

    public DoubleProperty getScaleYProperty() {
        return scaleTransform.yProperty();
    }

    public DoubleProperty getScrollDeltaProperty() {
        return scrollDeltaProperty;
    }

    public double getScaleValue() {
        return scaleValue;
    }

    public void scrollTo(double scrollDelta) {
        scrollDeltaProperty.set(scrollDelta);
    }

    public void zoomSource(double xMouseCoordinate, double yMouseCoordinate) {
        xMouseSource = xMouseCoordinate;
        yMouseSource = yMouseCoordinate;
    }

    public void zoomToActual() {
        zoomTo(1.0);
    }

    public void zoomTo(double scaleValue) {
        this.scaleValue = scaleValue;
        scaleTransform.setY(scaleValue);
        graphController.notifyUpdateView();
    }

    public void zoomActual() {
        scaleValue = 1;
        zoomTo(scaleValue);
    }

    public void zoomOut() {
        scaleValue -= zoomDelta;
        if (Double.compare(scaleValue, 0.1) < 0) {
            scaleValue = 0.1;
        }
        zoomTo(scaleValue);
    }

    public void zoomIn() {
        scaleValue += zoomDelta;
        if (Double.compare(scaleValue, 10) > 0) {
            scaleValue = 10;
        }
        zoomTo(scaleValue);
    }

    /**
     * 
     * @param minimizeOnly
     *            If the content fits already into the viewport, then we don't zoom if this parameter is true.
     */
    public void zoomToFit(boolean minimizeOnly) {
        double scaleX = getViewportBounds().getWidth() / getContent().getBoundsInLocal().getWidth();
        double scaleY = getViewportBounds().getHeight() / getContent().getBoundsInLocal().getHeight();

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

    private class ZoomAndScrollHandler implements EventHandler<ScrollEvent> {
        @Override
        public void handle(ScrollEvent event) {
            // zooming
            if (event.isControlDown()) {
                if (event.getDeltaY() < 0) {
                    scaleValue = Math.min(scaleValue + zoomDelta, Constants.MAX_ZOOM_VALUE);
                } else {
                    scaleValue = Math.max(scaleValue - zoomDelta, Constants.MIN_ZOOM_VALUE);
                }
                zoomTo(scaleValue);
                zoomSource(event.getSceneX(), event.getSceneY());
                event.consume();
            } else {
                // scrolling
                scrollTo(event.getDeltaY());
                System.out.println("content.BIL.height:    " + getContent().getBoundsInLocal().getHeight());
                System.out.println("group.BIP.height:      " + ((Group) event.getSource()).getBoundsInParent().getHeight());
                System.out.println("ViewportBounds.height: " + getViewportBounds().getHeight());
                System.out.println("VValue: " + getVvalue());
                System.out.println("VMax:   " + getVmax());
                System.out.println("VMin:   " + getVmin());
                System.out.println("scrollEvent.getDeltaY():    " + event.getDeltaY());
                System.out.println("MousePointer.inScene(x,y): (" + event.getSceneX() + "," + event.getSceneY() + ")");
                System.out.println("-----------------------");
            }
        }
    }

    public void setController(GraphController controller) {
        this.graphController = controller;
    }
}
