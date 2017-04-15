package nl.defsoftware.mrgb.services;

import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap;
import nl.defsoftware.mrgb.models.graph.Node;

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

    public GridHandler(Grid grid) {
        this.grid = grid;
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
     * Add the given node to the grid according to the {@code AxisType}.
     * 
     * @param node
     *            The {@code Node} to be added.
     * @param isBackBone
     *            AxisType enum public accessible in this class
     */
    public void addNode(Node node, AxisType isBackBone) {
        int column = determineEmptyColumnIndex(isBackBone);
        registerNode(node, LEVEL_CURSOR, column);
    }

    /**
     * Finds an empty spot on the column axis of the grid and returns that index.
     * 
     * @param isBackBone 
     * @return The first column index that is empty
     */
    private int determineEmptyColumnIndex(AxisType isBackBone) {
        if (isBackBone == AxisType.NON_BACKBONE) {
            for (int col = 1; col < grid.width(); col++) {
                if (grid.isEmptyInGridLocation(LEVEL_CURSOR, col)) {
                    return col;
                }
            }
        }
        return 0;
    }

    private void registerNode(Node node, int row, int col) {
        grid.addOrUpdateNodeInGrid(node, row, col);
        gridIndex.put(node.getNodeId(), new GridIndex(row, col));
    }
}

class GridIndex {
    public int row;
    public int col;

    public GridIndex(int row, int col) {
        this.row = row;
        this.col = col;
    }
}
