/**
 * 
 */
package nl.defsoftware.mrgb.view.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.animation.AnimationTimer;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.MapChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import nl.defsoftware.mrgb.services.GraphHandler;
import nl.defsoftware.mrgb.services.GraphService;
import nl.defsoftware.mrgb.view.GraphScrollPane;
import nl.defsoftware.mrgb.view.actions.ActionStateEnums;
import nl.defsoftware.mrgb.view.models.IGraphViewModel;
import nl.defsoftware.mrgb.view.models.RibbonGraphModel;

/**
 * This controller ensures that the layers are set for interacting with the
 * graph. A graphHandler class will ensure the loading of the graph model and is
 * responsible for the layout algorithm.
 *
 * @author D.L. Ettema
 * @date 7 Jun 2016
 */
public class GraphController implements Initializable, MapChangeListener<ActionStateEnums, Boolean> {

    private static final Logger log = LoggerFactory.getLogger(GraphController.class);

    private GraphService graphService;

    private Group groupedNodes;

    private MouseGestures mouseGestures;

    private GraphHandler graphHandler;

    private GraphScrollPane scrollPane;

    private IGraphViewModel model;

    private static final double SCROLL_ZOOM_FACTOR = 0.0025;

    private CellLayer cellLayer;

    @FXML
    private AnchorPane mainPane;

    @FXML
    private Pane topPane;

    @FXML
    private ScrollBar scrollbar;

    @FXML
    private Canvas topEdgeCanvas;

    @FXML
    private Label showingRange;

    private final IntegerProperty amountOfLevels = new SimpleIntegerProperty(0);
    private final DoubleProperty zoomFactor = new SimpleDoubleProperty(1.0);
    private boolean needsUpdate = false;

    public GraphController() {
        graphService = new GraphService();
        model = new RibbonGraphModel();
        groupedNodes = new Group();
        cellLayer = new CellLayer();
        mouseGestures = new MouseGestures(this);
        scrollPane = new GraphScrollPane(groupedNodes);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setHbarPolicy(ScrollBarPolicy.ALWAYS);
        scrollPane.setVbarPolicy(ScrollBarPolicy.ALWAYS);
        scrollPane.setController(this);
        scrollPane.setChangeListener(graphHandler);

        groupedNodes.getChildren().add(cellLayer);
        cellLayer.setStyle("-fx-background-color: white;");

        GraphAnimationTimer timer = new GraphAnimationTimer();
        timer.start();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void updateView() {
        needsUpdate = true;
    }
    
    private class GraphAnimationTimer extends AnimationTimer {

        public GraphAnimationTimer() {
        }

        @Override
        public void handle(long now) {
          if (needsUpdate) {
            log.info("Updating graph");
            needsUpdate = false;
            updateGraph();
          }
        }
    }
    
    public void updateGraph() {
//        int viewRange = (int) (mainPane.getHeight() / zoomFactor.get()) + 1;
//        double viewingStart = Math.max(amountOfLevels.multiply(scrollbar.getValue()).doubleValue(), 0.0);
//        clear();
//        
        beginUpdate();
        graphHandler = new GraphHandler(graphService.getParsedSequences(), graphService.getGenomeNames());
        graphHandler.loadAlternateGraphViewModel(model, scrollPane.getScaleYProperty());
        endUpdate();
        log.info("VIEW GRAPH");
        
//        graphHandler.loadAlternateGraphViewModel(topPane, topEdgeCanvas, model, viewingStart, viewRange);
    }

    private void clear() {
        topPane.getChildren().clear();
        topEdgeCanvas.getGraphicsContext2D().clearRect(0, 0, topEdgeCanvas.getWidth(), topEdgeCanvas.getWidth());
    }
    
    
    @Override
    public void onChanged(MapChangeListener.Change<? extends ActionStateEnums, ? extends Boolean> change) {
        printMemoryUsage();
        if (change.getKey() instanceof ActionStateEnums) {
            if (ActionStateEnums.LOAD_DATA_AND_PARSE == change.getKey()) {
                // @TODO do this work in another thread.
                graphService.loadDataAndParse();
                log.info("LOAD DATA AND PARSE");
            } else if (ActionStateEnums.VIEW_GRAPH == change.getKey()) {
                updateGraph();
            }
        }
        printMemoryUsage();
    }

    private void printMemoryUsage() {
        Runtime runtime = Runtime.getRuntime();
        long bytes = (runtime.totalMemory() - runtime.freeMemory());
        double megabytes = bytes / 1024.0 / 1024.0;
        log.info("memory = " + String.format(Locale.US, "%.3f", megabytes) + " MB");
    }

    public ScrollPane getScrollPane() {
        return this.scrollPane;
    }

    public Pane getCellLayer() {
        return this.cellLayer;
    }

    private void beginUpdate() {
        getCellLayer().getChildren().clear();
    }

    private void endUpdate() {

        // add components to graph pane

        getCellLayer().getChildren().addAll(model.getAllEdges());
        getCellLayer().getChildren().addAll(model.getAddedSequences());
        getCellLayer().getChildren().addAll(model.getAllLabels());

        // remove components from graph pane
        // getCellLayer().getChildren().removeAll(model.getRemovedSequences());
        // getCellLayer().getChildren().removeAll(model.getRemovedEdges());

        // enable dragging of cells
        for (Node node : model.getAddedSequences()) {
            mouseGestures.makeDraggable(node);
        }

        // every cell must have a parent, if it doesn't, then the graphParent is
        // the parent
        // getModel().attachOrphansToGraphParent(model.getAddedSequences());
        //
        // // remove reference to graphParent
        // getModel().disconnectFromGraphParent(model.getRemovedSequences());

        // merge added & removed cells with all cells
        model.merge();
    }

    public double getScale() {
        return this.scrollPane.getScaleValue();
    }

    public class CellLayer extends Pane {

    }
}
