package nl.defsoftware.mrgb.view;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.beans.property.DoubleProperty;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.ScrollEvent;
import javafx.scene.transform.Scale;
import nl.defsoftware.mrgb.services.GraphHandler;
import nl.defsoftware.mrgb.view.controllers.GraphController;

/**
 * ZoomableScrollPane
 *
 *
 * @author http://stackoverflow.com/questions/30679025/graph-visualisation-like-
 *         yfiles-in-javafx
 * 
 * @date 6 Jun 2016
 */
public class GraphScrollPane extends ScrollPane {
    
    private static final Logger log = LoggerFactory.getLogger(GraphScrollPane.class);
    
	private Group zoomGroup;
	private Scale scaleTransform;
	private double scaleValue = 1.0;
	private double delta = 0.05;
	
	//should be interface for listener object so different things can listen to changes
	private GraphHandler graphHandler;
	private GraphController graphController;
	
	public GraphScrollPane(Node content) {
	    scaleTransform = new Scale(scaleValue, scaleValue, 0, 0);
		zoomGroup = new Group(content);
		zoomGroup.getTransforms().add(scaleTransform);
		zoomGroup.setOnScroll(new ZoomHandler());
		
		setContent(zoomGroup);
		setManaged(true);
	}

	public DoubleProperty getScaleYProperty() {
	    return scaleTransform.yProperty();
	}
	public double getScaleValue() {
		return scaleValue;
	}

	public void zoomToActual() {
		zoomTo(1.0);
	}

	public void zoomTo(double scaleValue) {
		this.scaleValue = scaleValue;
		scaleTransform.setY(scaleValue);
		graphController.updateView();
	}

	public void zoomActual() {
		scaleValue = 1;
		zoomTo(scaleValue);
	}

	public void zoomOut() {
		scaleValue -= delta;
		if (Double.compare(scaleValue, 0.1) < 0) {
			scaleValue = 0.1;
		}
		zoomTo(scaleValue);
	}

	public void zoomIn() {
		scaleValue += delta;
		if (Double.compare(scaleValue, 10) > 0) {
			scaleValue = 10;
		}
		zoomTo(scaleValue);
	}

	/**
	 * 
	 * @param minimizeOnly
	 *          If the content fits already into the viewport, then we don't zoom
	 *          if this parameter is true.
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

	private class ZoomHandler implements EventHandler<ScrollEvent> {
		@Override
		public void handle(ScrollEvent scrollEvent) {
			// if (scrollEvent.isControlDown())
			{
				if (scrollEvent.getDeltaY() < 0) {
					scaleValue -= delta;
				} else {
					scaleValue += delta;
				}
				zoomTo(scaleValue);
				scrollEvent.consume();
			}
		}
	}

    public void setChangeListener(GraphHandler graphHandler) {
        this.graphHandler = graphHandler;        
    }
    
    public void setController(GraphController graphController) {
        this.graphController = graphController;
    }
}
