package nl.defsoftware.mrgb.graphs;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.scene.shape.Shape;
import nl.defsoftware.mrgb.graphs.models.Node;
import nl.defsoftware.mrgb.view.models.IGraphViewModel;

/**
 * 
 * Start by initialising this {@ Grid} with method {@code initializeCoordinatesInGrid()}. Use this also to reset the
 * grid to a new coordinate system and leaving the nodes presently unchanged.
 * 
 * ATK hashmap2D
 * 
 * 
 * 
 * @author D.L. Ettema
 *
 */
public class Grid {

    private static final Logger log = LoggerFactory.getLogger(Grid.class);

    private NodeCoordinate[][] grid;
    private BigDecimal spacingX;
    private BigDecimal spacingY;
    
    public static int NO_MORE_WIDTH_SPACE = -1;
    public static int NO_MORE_HEIGHT_SPACE = -1;

    public Grid(int sizeX, int sizeY) {
        if (sizeX < 1 || sizeY < 1) {
            throw new RuntimeException(
                    "Grid initialisation variables sizeX or sizeY are too small. Should be higher then 0.");
        }
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

    public void addOrUpdateNodeInGrid(Node node, int row, int col) {
        checkGridIndexElement(row, col);
        List<Node> nodes = grid[row][col].attachedNodes;
        if (nodes.contains(node)) {
            nodes.set(nodes.indexOf(node), node);// replace current
        } else {
            nodes.add(node);
        }
    }

    public boolean removeNodeInGrid(Node nodeToBeRemoved, int row, int col) {
        checkGridIndexElement(row, col);
        List<Node> nodes = grid[row][col].attachedNodes;
        return nodes.remove(nodeToBeRemoved);
    }

    public boolean hasNodeInGridLocation(int row, int col) {
        checkGridIndexElement(row, col);
        return !grid[row][col].attachedNodes.isEmpty();
    }

    public boolean isEmptyInGridLocation(int row, int col) {
        checkGridIndexElement(row, col);
        return grid[row][col].attachedNodes.isEmpty();
    }

    private void checkGridIndexElement(int row, int col) {
        if (spacingX == null || spacingY == null) {
            throw new RuntimeException(
                    "Grid variables not initialised, please call initializeCoordinatesInGrid() method first");
        }
        if (row >= this.height() || col >= this.width()) {
            throw new RuntimeException("Row or column variables exceed limit of allocated grid");
        }
        if (row <= NO_MORE_HEIGHT_SPACE || col <= NO_MORE_WIDTH_SPACE) {
            throw new RuntimeException(
                    "Row or clumn variables were negative indexes. Probably no more space on grid to draw.");
        }
        if (grid[row][col] == null) {
            grid[row][col] = new NodeCoordinate();
            // the X coordinate expands along the columns and the Y coordinate expands along the rows
            grid[row][col].x = grid[0][0].x + spacingX.multiply(BigDecimal.valueOf((long) col)).doubleValue();
            grid[row][col].y = grid[0][0].y + spacingY.multiply(BigDecimal.valueOf((long) row)).doubleValue();
        }
    }
    
    public void getNodesInView(BigDecimal row, BigDecimal range, List<Node> list) {
        int startPosGrid = getStartPosOrDefault(row, range);
        for (int i = 0; i < startPosGrid; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                list.addAll(grid[i][j].attachedNodes);
            }
        }
    }

    private int getStartPosOrDefault(BigDecimal row, BigDecimal range) {
        int startPos = row.intValue() - range.intValue();
        return (startPos < 0) ? 0 : startPos;
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

    public int width() {
        return grid[0].length;
    }

    public int height() {
        return grid.length;
    }
}

class NodeCoordinate {
    double x = 0.0;
    double y = 0.0;
    List<Node> attachedNodes = new ArrayList<>();

}
