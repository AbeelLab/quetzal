package nl.defsoftware.mrgb.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nl.defsoftware.mrgb.models.graph.Graph;

/**
 * This class can hold some test data for a graph that is in turn utilised to test algorithms.
 * @author D.L. Ettema
 *
 */
public class GraphTestContainer {
    
    private Graph graph;
    private int sourceNodeId;
    private int targetNodeId;
    private int numberOfHopsInLongestPath;
    private Map<Integer, List<Integer>> possibleLongestPaths;
    
    public GraphTestContainer() {
        possibleLongestPaths = new HashMap<>();
    }

    public Graph getGraph() {
        return graph;
    }

    public void setGraph(Graph graph) {
        this.graph = graph;
    }

    public int getSourceNodeId() {
        return sourceNodeId;
    }

    public void setSourceNodeId(int sourceNodeId) {
        this.sourceNodeId = sourceNodeId;
    }

    public int getTargetNodeId() {
        return targetNodeId;
    }

    public void setTargetNodeId(int targetNodeId) {
        this.targetNodeId = targetNodeId;
    }

    public int getNumberOfHopsInLongestPath() {
        return numberOfHopsInLongestPath;
    }

    public void setNumberOfHopsInLongestPath(int numberOfHopsInLongestPath) {
        this.numberOfHopsInLongestPath = numberOfHopsInLongestPath;
    }

    public Map<Integer, List<Integer>> getPossibleLongestPaths() {
        return possibleLongestPaths;
    }

    public void setPossibleLongestPaths(Map<Integer, List<Integer>> possibleLongestPaths) {
        this.possibleLongestPaths = possibleLongestPaths;
    }

}
