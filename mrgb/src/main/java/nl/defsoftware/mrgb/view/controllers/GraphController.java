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

	private static final int HOR_NODE_SPACING = 20;
	private static final int VER_NODE_SPACING = 20;

	private static final int VER_NODE_BASELINE = 200;

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
		int HORIZONTAL_NODE_CURSOR = 1;
		for (Short fromKey : graphMap.keySet()) {
			int VERTICAL_NODE_CURSOR = 1;// reset after each new key
			int xCoord = HORIZONTAL_NODE_CURSOR * HOR_NODE_SPACING;
			int yCoord = VER_NODE_BASELINE;

			// @TODO do check if node is already placed and which coords
			// Sequence prevPlacedSequence = model.findSequenceById(fromKey);
			// if (prevPlacedSequence != null) {
			// // set new y baseline
			// yCoord = (int) prevPlacedSequence.getCenterY();
			// } else {
			// model.addSequence(fromKey, xCoord, yCoord);
			// }
			model.addSequence(fromKey, xCoord, yCoord);
			HORIZONTAL_NODE_CURSOR++;

			int prevFollowUpNodeId = fromKey.intValue();
			// @TODO do topological sort first
			short[] connectedSequences = graphMap.get(fromKey);
			for (int i = 0; i < connectedSequences.length; i++) {
				// Sequence prevPlacedSequence =
				// model.findSequenceById(connectedSequences[i]);
				// if (prevPlacedSequence != null) {
				// int avgYCoord =
				// calculateAverageYCoordinate(prevPlacedSequence.getSequenceParents());
				// prevPlacedSequence.setCenterY(avgYCoord);
				//
				// } else {
				if (prevFollowUpNodeId + 1 == Short.toUnsignedInt(connectedSequences[i])) {
					if (i == 0) { // backbone vertical position
						xCoord = HORIZONTAL_NODE_CURSOR * HOR_NODE_SPACING;
						HORIZONTAL_NODE_CURSOR++;
					}
					yCoord = VER_NODE_BASELINE + (VERTICAL_NODE_CURSOR * VER_NODE_SPACING);
					model.addSequence(connectedSequences[i], xCoord, yCoord);
					VERTICAL_NODE_CURSOR++;
					prevFollowUpNodeId = connectedSequences[i];
				} else {
					xCoord = (HORIZONTAL_NODE_CURSOR * HOR_NODE_SPACING);
					yCoord = VER_NODE_BASELINE;
					model.addSequence(connectedSequences[i], xCoord, yCoord);
					HORIZONTAL_NODE_CURSOR++;
				}
				// add edge @TODO add weight and know if we need to go around
				// other
				// nodes when drawing
				model.addEdge(fromKey, connectedSequences[i]);
			}
			// }
		}
		endUpdate();
	}

	/**
	 * @param sequenceParents
	 * @return
	 * 
	 *         int
	 *
	 */
	private int calculateAverageYCoordinate(List<Sequence> sequenceParents) {
		BigDecimal avgYCoord = new BigDecimal(0);
		for (Sequence sequence : sequenceParents) {
			avgYCoord.add(new BigDecimal(sequence.getCenterY()));
		}
		return avgYCoord.divideToIntegralValue(BigDecimal.valueOf(sequenceParents.size())).intValue();
	}

	private void addEdges(short[] toNodes, GraphModel model, Short from) {
		for (int i = 0; i < toNodes.length; i++) {
			model.addEdge(from, toNodes[i]);
		}
	}

	private void layoutGraph() {
		log.info("Layout graph");
		// List<Sequence> sequences = getModel().getAllSequences();

	}

	public void beginUpdate() {
	}

	public void endUpdate() {

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
