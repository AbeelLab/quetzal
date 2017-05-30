package nl.defsoftware.mrgb.view;

import javafx.event.EventHandler;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import nl.defsoftware.mrgb.services.GraphHandler;
import nl.defsoftware.mrgb.services.GridHandler;

/**
 * @author D.L. Ettema
 *
 */
public class GraphToFxScenePainter implements EventHandler<ScrollEvent> {
    
    private Pane nodePane;
    private Canvas edgeCanvas;
    private ScrollPane scrollPane;
    private GridHandler gridHandler;
    private GraphHandler graphHandler;
        
    
    private int gridSize = 146;
    
    
    public GraphToFxScenePainter(Pane nodePane, Canvas edgeCanvas, ScrollPane scrollPane, GraphHandler graphHandler, GridHandler gridHandler) {
        this.nodePane = nodePane;
        this.edgeCanvas = edgeCanvas;
        this.scrollPane = scrollPane;
        this.gridHandler = gridHandler;
        this.graphHandler = graphHandler;
    }
    
    public void drawDrawing() {
//        nodePane.setPrefHeight(gridSize * nodeSize + gridSize * edgeLength);
        scrollPane.getViewportBounds().getHeight();
        
        
    }
    
    
    public void updateOnScroll() {
//        if (scrollPane.getVvalue() )
    }
    
    public void updateOnZoom() {
        
        
        scrollPane.getVvalue();
        scrollPane.getVmin();
        scrollPane.getVmax();
        
//        graphHandler.loadGraphViewModel(model, 
//                dummyViewingStartCoordinate, 
//                dummyRange, 
//                zoomFactor, 
//                longestPath);
        
    }

    @Override
    public void handle(ScrollEvent event) {
        // TODO Auto-generated method stub
        
    }
}
