package nl.defsoftware.mrgb.services;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.defsoftware.mrgb.models.graph.Node;

/**
 * 
 * Start by initialising this GridHandler with method {@code initializeCoordinatesInGrid()}. Use this also to reset the
 * grid to a new coordinate system and leaving the nodes presently unchanged.
 * 
 * @author D.L. Ettema
 *
 */
public class GridHandler {

    private static final Logger log = LoggerFactory.getLogger(GridHandler.class);
    public final short BACKBONE_NODE = 0;
    public final short NON_BACKBONE_NODE = 1;
    
    private NodeCoordinate[][] grid;
    private BigDecimal spacingX;
    private BigDecimal spacingY;

    public GridHandler(int sizeX, int sizeY) {
        grid = new NodeCoordinate[sizeX][sizeY];
        grid[0][0] = new NodeCoordinate();
    }

    /**
     * Initialises the grid with a coordinate system. Provide left top coordinates and the spacing between each grid
     * element. Take not that spacing should include the width and height of the nodes.
     * 
     * @param startX
     *            The top left x-coordinate of your drawable Pane/canvas.
     * @param startY
     *            The top left y-coordinate of your drawable Pane/canvas.
     * @param spacingX
     *            The spacing on the x-axis between each grid element.
     * @param spacingY
     *            The spacing on the y-axis between each grid element.
     */
    public void initializeCoordinatesInGrid(double startX, double startY, double spacingX, double spacingY) {
        grid[0][0].x = startX;
        grid[0][0].y = startY;
        this.spacingX = BigDecimal.valueOf(spacingX);
        this.spacingY = BigDecimal.valueOf(spacingY);
        // check on coordinates not overflowing to max range of double on the y-axis
        double offset = grid[0][0].y;
        double YCoordinateLength = this.spacingY.multiply(BigDecimal.valueOf(grid.length)).doubleValue();
        double maxYCoordinate = offset + YCoordinateLength;
        if (Double.compare(Double.POSITIVE_INFINITY, maxYCoordinate) == 0) {
            log.info("Coordinate system too small for amount of nodes, reinitialising with smaller spacing");
            this.initializeCoordinatesInGrid(startX, startY, spacingX, spacingY - 1.0);
        }
    }
    
    public void addOrUpdateNodeInGrid(Node node, int row, short col) {
        ensureValidGridElement(row, col);
        List<Node> nodes = grid[row][col].attachedNodes;
        if (nodes.contains(node)) {
            nodes.set(nodes.indexOf(node), node);
        } else {
            nodes.add(node);
        }
    }
    
    public boolean hasNodeInGrid(int row, int col) {
        ensureValidGridElement(row, col);
        return !grid[row][col].attachedNodes.isEmpty();
    }
    
    private void ensureValidGridElement(int row, int col) {
        if (spacingX == null || spacingY == null) {
            throw new RuntimeException(
                    "GridHandler variables not initialised, please call initializeCoordinatesInGrid() method first");
        }
        if (grid[row][col] == null) {
            grid[row][col] = new NodeCoordinate();
            //the X coordinate expands along the columns and the Y coordinate expands along the rows
            grid[row][col].x = grid[0][0].x + spacingX.multiply(BigDecimal.valueOf((long) col)).doubleValue();
            grid[row][col].y = grid[0][0].y + spacingY.multiply(BigDecimal.valueOf((long) row)).doubleValue();
        }
    }
    
    public void scaleCoordinates(int scale) {
        throw new RuntimeException("Not yet implemented");
    }
    
    public void clearAllNodes() {
        throw new RuntimeException("Not yet implemented");
    }
    
    public void clearNodesInRange(int startRow, int endInclusiveRow) {
        throw new RuntimeException("Not yet implemented");
    }
}

class NodeCoordinate {
    double x = 0.0;
    double y = 0.0;
    List<Node> attachedNodes = new ArrayList<>();

}
