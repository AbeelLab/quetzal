/**
 * 
 */
package nl.defsoftware.mrgb.view.controllers;

import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.collections.MapChangeListener;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;
import nl.defsoftware.mrgb.services.GraphService;
import nl.defsoftware.mrgb.view.GraphScrollPane;
import nl.defsoftware.mrgb.view.models.GraphModel;
import nl.defsoftware.mrgb.view.models.ModelLine;
import nl.defsoftware.mrgb.view.models.RibbonGraphModel;

/**
 * This controller ensures that the layers are set for interacting with the
 * graph. A graphHandler class will ensure the loading of the graph model and is
 * responsible for the layout algorithm.
 *
 * @author D.L. Ettema
 * @date 7 Jun 2016
 */
public class GraphController extends Group implements Initializable, MapChangeListener<ActionStateEnums, Boolean> {

    private static final Logger log = LoggerFactory.getLogger(GraphController.class);

    private GraphService graphService;
    
    private RibbonGraphModel model;

    private Group groupedNodes;

    private GraphScrollPane scrollPane;

    private MouseGestures mouseGestures;

    /**
     * the pane wrapper is necessary or else the scrollpane would always align
     * the top-most and left-most child to the top and left eg when you drag the
     * top child down, the entire scrollpane would move down
     */
    CellLayer cellLayer;

    Canvas canvas;

    public GraphController() {
//        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("fff.fxml"));
//        fxmlLoader.setRoot(this);
//        fxmlLoader.setController(this);
//        try {
//            fxmlLoader.load();
//        } catch (IOException exception) {
//            log.error("Could not load resource related to toolbar.fxml" + exception.getMessage());
//            throw new RuntimeException(exception);
//        }
        
        graphService = new GraphService();
        
        model = new RibbonGraphModel();
        groupedNodes = new Group();
        cellLayer = new CellLayer();

        canvas  = new Canvas(500, 3000);
        groupedNodes.getChildren().add(cellLayer);
        groupedNodes.getChildren().add(canvas);

        mouseGestures = new MouseGestures(this);

        scrollPane = new GraphScrollPane(groupedNodes);

        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setHbarPolicy(ScrollBarPolicy.ALWAYS);
        scrollPane.setVbarPolicy(ScrollBarPolicy.ALWAYS);
    }
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onChanged(MapChangeListener.Change<? extends ActionStateEnums, ? extends Boolean> change) {
        printMemoryUsage();
        if (change.getKey() instanceof ActionStateEnums) {
            if (ActionStateEnums.LOAD_DATA_AND_PARSE == change.getKey()) {
                //@TODO do this work in another thread.
                graphService.loadDataAndParse();
                log.info("LOAD DATA AND PARSE");
            } else if (ActionStateEnums.VIEW_GRAPH == change.getKey()) {
                beginUpdate();
//                graphService.loadSequenceModel(model, semanticView);//TODO
                graphService.loadSequenceModel(model);
                endUpdate();
                log.info("VIEW GRAPH");
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
    
    public Canvas getCanvas() {
        return canvas;
    }

    public RibbonGraphModel getModel() {
        return model;
    }

    private void beginUpdate() {
    }

    private void endUpdate() {

        // add components to graph pane
        
//        GraphicsContext gc = getCanvas().getGraphicsContext2D();
//        gc.setStroke(Color.BLUE);
//        gc.setLineWidth(3);
//        for (Shape shape : model.getAllEdges()) {
//            if (shape instanceof ModelLine) {
//                ModelLine l = (ModelLine) shape;
//                gc.strokeLine(shape.getLayoutX(), shape.getLayoutY(), shape.getLayoutX(), shape.getLayoutY() + 17);
//            } else { 
//                getCellLayer().getChildren().addAll(shape);
//            }
//        }
        
        getCellLayer().getChildren().addAll(model.getAllEdges());
        getCellLayer().getChildren().addAll(model.getAddedSequences());
//        getCellLayer().getChildren().addAll(model.getAllLabels());

        // remove components from graph pane
//        getCellLayer().getChildren().removeAll(model.getRemovedSequences());
//        getCellLayer().getChildren().removeAll(model.getRemovedEdges());

        // enable dragging of cells
//        for (Node node : model.getAddedSequences()) {
//            mouseGestures.makeDraggable(node);
//        }

        // every cell must have a parent, if it doesn't, then the graphParent is
        // the parent
//        getModel().attachOrphansToGraphParent(model.getAddedSequences());
//
//        // remove reference to graphParent
//        getModel().disconnectFromGraphParent(model.getRemovedSequences());

        // merge added & removed cells with all cells
        getModel().merge();
    }

    public double getScale() {
        return this.scrollPane.getScaleValue();
    }

    public class CellLayer extends Pane {

    }
}
