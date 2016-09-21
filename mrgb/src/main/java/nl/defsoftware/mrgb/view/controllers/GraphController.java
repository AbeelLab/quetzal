/**
 * 
 */
package nl.defsoftware.mrgb.view.controllers;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.scene.Group;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.layout.Pane;
import nl.defsoftware.mrgb.view.GraphScrollPane;
import nl.defsoftware.mrgb.view.models.GraphModel;
import nl.defsoftware.mrgb.view.models.Sequence;

/**
 * This controller ensures that the layers are set for interacting with the
 * graph. A graphHandler class will ensure the loading of the graph model and is
 * responsible for the layout algorithm.
 *
 * @author D.L. Ettema
 * @date 7 Jun 2016
 */
public class GraphController {

    private static final Logger log = LoggerFactory.getLogger(GraphController.class);

    private GraphHandler graphHandler;

    private GraphModel model;

    private Group groupedNodes;

    private GraphScrollPane scrollPane;

    private MouseGestures mouseGestures;

    /**
     * the pane wrapper is necessary or else the scrollpane would always align
     * the top-most and left-most child to the top and left eg when you drag the
     * top child down, the entire scrollpane would move down
     */
    CellLayer cellLayer;

    public GraphController() {
        this.model = new GraphModel();
        groupedNodes = new Group();
        cellLayer = new CellLayer();
        graphHandler = new GraphHandler();

        groupedNodes.getChildren().add(cellLayer);

        mouseGestures = new MouseGestures(this);

        scrollPane = new GraphScrollPane(groupedNodes);

        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setHbarPolicy(ScrollBarPolicy.ALWAYS);
        scrollPane.setVbarPolicy(ScrollBarPolicy.ALWAYS);
    }

    public ScrollPane getScrollPane() {
        return this.scrollPane;
    }

    public Pane getCellLayer() {
        return this.cellLayer;
    }

    public GraphModel getModel() {
        return model;
    }

    public void setGraphMap(HashMap<Short, short[]> graphMap) {
        beginUpdate();
        graphHandler.setGraphViewModel(model, graphMap);
        endUpdate();
    }

    private void layoutGraph() {
        log.info("Layout graph");
        // List<Sequence> sequences = getModel().getAllSequences();
    }

    private void beginUpdate() {
    }

    private void endUpdate() {

        // add components to graph pane
        getCellLayer().getChildren().addAll(model.getAddedEdges());
        getCellLayer().getChildren().addAll(model.getAddedSequences());

        // remove components from graph pane
        getCellLayer().getChildren().removeAll(model.getRemovedSequences());
        getCellLayer().getChildren().removeAll(model.getRemovedEdges());

        // enable dragging of cells
        for (Sequence sequence : model.getAddedSequences()) {
            mouseGestures.makeDraggable(sequence);
        }

        // every cell must have a parent, if it doesn't, then the graphParent is
        // the parent
        getModel().attachOrphansToGraphParent(model.getAddedSequences());

        // remove reference to graphParent
        getModel().disconnectFromGraphParent(model.getRemovedSequences());

        // merge added & removed cells with all cells
        getModel().merge();

        layoutGraph();
    }

    public double getScale() {
        return this.scrollPane.getScaleValue();
    }

    public class CellLayer extends Pane {

    }
}
