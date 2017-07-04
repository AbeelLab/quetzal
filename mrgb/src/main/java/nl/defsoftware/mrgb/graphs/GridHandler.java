package nl.defsoftware.mrgb.graphs;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap;
import javafx.scene.shape.Shape;
import nl.defsoftware.mrgb.graphs.models.Node;
import nl.defsoftware.mrgb.view.models.IGraphViewModel;

/**
 * This class occupies with the abstractified logic of placing the nodes into the grid and the spacing between the grid
 * elements. Finding out if a gridlocation is empty to be able to place an node in it, or move it to the next column. We
 * assume we are drawing in a vertical direction.
 * 
 * @author D.L. Ettema
 *
 */
public class GridHandler {

    /**
     * Enumeration of AxisType, telling this GridHandler if a node should be placed along the main axis.
     */
    public enum AxisType {
        BACKBONE, NON_BACKBONE
    };

    /** The actual grid */
    private Grid grid;
    
    /** Registers each placed node in the grid with their ID as key and the grid location as a GridIndex class */
    private Int2ObjectLinkedOpenHashMap<GridIndex> gridIndex;
    private int LEVEL_CURSOR = 0;
    
    //a bookmarker object
    private List<GridIndex> lastKnownViewingLocations = new ArrayList<>();

    public GridHandler(Grid grid) {
        this.grid = grid;
        gridIndex = new Int2ObjectLinkedOpenHashMap<>();
        gridIndex.defaultReturnValue(GridIndex.INVALID_GRID_INDEX);
    }

    /**
     * TODO: add garbage collection to cleanup the old grid, before using new keyword and orphaning the old grid.
     *
     * @param sizeX
     * @param sizeY
     */
    public void resetGrid(int sizeX, int sizeY) {
        grid = new Grid(sizeX, sizeY);
    }

    /**
     * Will put the level cursor one step back. If cursor is at maximum of the grid, it will return -1, otherwise the
     * new level of the cursor.
     * 
     * @return The value of the new level of this cursor. Returns -1 if max level of grid has been reached.
     */
    public int moveGridLevelCursorNext() {
        return (LEVEL_CURSOR + 1 >= grid.height()) ? -1 : ++LEVEL_CURSOR;
    }

    /**
     * Will put the level cursor one step back. If cursor is at 0, it will return -1, otherwise the new level of the
     * cursor.
     * 
     * @return The value of the new level of this cursor. Returns -1 if level 0 has been reached.
     */
    public int moveGridLevelCursorPrevious() {
        return (LEVEL_CURSOR - 1 <= 0) ? -1 : --LEVEL_CURSOR;
    }

    /**
     * Will return the level of the given node id in the {@code Grid}
     * 
     * @param id
     *            The {@code Node} ID.
     * @return The level of given node ID. Will return -1 if node ID is not found.
     */
    public int findLevelOfNode(int id) {
        return gridIndex.get(id).row;
    }

    /**
     * Will return the column of the given {@code Node} ID in the {@code Grid}
     * 
     * @param id
     *            The {@code Node} ID.
     * @return The column of given node ID. Will return -1 if node ID is not found.
     */
    public int findColumnOfNode(int id) {
        return gridIndex.get(id).col;
    }

    /**
     * Add the given node to the grid according to the {@code AxisType}.
     * 
     * @param node
     *            The {@code Node} to be added.
     * @param isBackbone
     *            AxisType enum public accessible in this class
     */
    public void addNode(Node node, AxisType isBackbone) {
        int column = determineEmptyColumnIndex(isBackbone, 0);
        registerNode(node, LEVEL_CURSOR, column);
    }

    /**
     * Add the given node to the grid according to the {@code AxisType}.
     * 
     * @param node
     *            The {@code Node} to be added.
     * @param isBackbone
     *            AxisType enum public accessible in this class
     * @param hasParentOnColumnIndex
     *            The given {@code Node} has a parent node on a higher column index and must be placed relative to that
     *            index.
     */
    public void addNode(Node node, AxisType isBackbone, int hasParentOnColumnIndex) {
        int column = determineEmptyColumnIndex(isBackbone, hasParentOnColumnIndex);
        registerNode(node, LEVEL_CURSOR, column);
    }

    /**
     * Finds an empty spot on the column axis of the grid and returns that index.
     * 
     * @param isBackbone
     * @return The first column index that is empty
     */
    private int determineEmptyColumnIndex(AxisType isBackbone, int hasParentOnColumnIndex) {
        if (isBackbone == AxisType.NON_BACKBONE) {
            for (int col = hasParentOnColumnIndex; col < grid.width(); col++) {
                if (grid.isEmptyInGridLocation(LEVEL_CURSOR, col)) {
                    return col;
                }
            }
            return Grid.NO_MORE_WIDTH_SPACE;
        }
        return 0;
    }

    private void registerNode(Node node, int row, int col) {
        grid.addOrUpdateNodeInGrid(node, row, col);
        gridIndex.put(node.getNodeId(), new GridIndex(row, col));
    }

    public void getNodesInView(BigDecimal viewPosition, BigDecimal range, List<Node> list) {
        //viewposition on the height of the grid should give me the center of the grid
        BigDecimal row = viewPosition.multiply(new BigDecimal(grid.height()));
        row = row.setScale(1, RoundingMode.HALF_UP);
        range = range.scaleByPowerOfTen(-10); //power of n=-10
        grid.getNodesInView(row, range, list);
    }
}

class GridIndex {
    public int row;
    public int col;

    public GridIndex(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public static GridIndex INVALID_GRID_INDEX = new GridIndex(-1, -1);
}
