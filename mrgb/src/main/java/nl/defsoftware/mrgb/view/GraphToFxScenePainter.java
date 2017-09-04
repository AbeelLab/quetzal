package nl.defsoftware.mrgb.view;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Pane;
import nl.defsoftware.mrgb.graphs.GraphHandler;
import nl.defsoftware.mrgb.graphs.GridHandler;
import nl.defsoftware.mrgb.graphs.GridHandler.AxisType;
import nl.defsoftware.mrgb.graphs.models.Bubble;
import nl.defsoftware.mrgb.graphs.models.Node;
import nl.defsoftware.mrgb.view.models.NodeDrawingData;
import nl.defsoftware.mrgb.view.models.RibbonGraphModel;

/**
 * @author D.L. Ettema
 *
 */
public class GraphToFxScenePainter implements Observer {
    
    private Pane nodePane;
    private Canvas edgeCanvas;
    private ScrollPane scrollPane;
    private GridHandler gridHandler;
    private GraphHandler graphHandler;
    
    private Int2ObjectLinkedOpenHashMap<Bubble> bubbles;
    private Int2ObjectLinkedOpenHashMap<Node> graphData;
    
    private static final int BACKBONE_X_BASELINE = 100;
    private static final int BACKBONE_Y_BASELINE = 25;
    
    public GraphToFxScenePainter(Pane nodePane, Canvas edgeCanvas, ScrollPane scrollPane, GraphHandler graphHandler, GridHandler gridHandler) {
        this.nodePane = nodePane;
        this.edgeCanvas = edgeCanvas;
        this.scrollPane = scrollPane;
        this.gridHandler = gridHandler;
        this.graphHandler = graphHandler;
    }
    
    @Override
    public void update(Observable o, Object arg) {
        //scroll scenario
        
        //determine current window of scrollpane
        //seek grid equivalent
        //get nodes
        //draw nodes on pane
        
        //find out if buffer is getting to max, ifso, seek nodes that are furthest away from current position and delete from pane.
        
        ScrollAndZoomHandler scHandler = (ScrollAndZoomHandler) o;
        
        BigDecimal verticalPosition = BigDecimal.valueOf(scHandler.getVValue());
        verticalPosition = verticalPosition.setScale(4, RoundingMode.HALF_UP);//i.e.: 0.3452
        
        BigDecimal range = BigDecimal.valueOf(scHandler.getHeightOfWindow());//i.e.: 900.0 
        
        //TODO: verticalPanePosition to a gridviewPosition
        List<Node> nodes = new ArrayList<Node>();
        gridHandler.getNodesInView(verticalPosition, range, nodes);
        
        NodeDrawingData drawingData = new NodeDrawingData();
        drawingData.xCoordinate = BACKBONE_X_BASELINE;
        drawingData.yCoordinate = BACKBONE_Y_BASELINE;
        drawingData.width = 0;
        drawingData.height = 0;
        drawingData.scale = scHandler.getScaleValue();
        
        /* this section should be placed as part of the semanticgraphhandler */
        RibbonGraphModel model = new RibbonGraphModel();
        for (Node aNode : nodes) {
            model.addSequence(aNode, 0, drawingData);
            
        }
        
        //apply filter decoration
        
        //we should fill the pane from the gridhandler, not from the model anymore.
        nodePane.getChildren().addAll(model.getAddedSequences());
        nodePane.getChildren().remove(model.getRemovedSequences());
        nodePane.getChildren().addAll(model.getAddedEdges());
        nodePane.getChildren().remove(model.getRemovedEdges());
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
        
    }


   
}
