/**
 * 
 */
package nl.defsoftware.mrgb.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.shorts.Short2ObjectOpenHashMap;
import nl.defsoftware.mrgb.models.graph.Bubble;
import nl.defsoftware.mrgb.models.graph.Node;
import nl.defsoftware.mrgb.view.models.AbstractGraphViewModel;

/**
 * A service that takes care of retrieving and storing graph data.
 * 
 * @author D.L. Ettema
 * @date 23 September 2016
 *
 */
public class GraphService {

    private FileParserService parserService;
    private SuperBubbleDetectionAlgorithm sbAlgorithm;
    private LongestPathAlgorithm lpAlgorithm;
    private GridHandler gridHandler;

    public GraphService() {
        parserService = new FileParserServiceImpl();
        sbAlgorithm = new SuperBubbleDetectionAlgorithm();
        lpAlgorithm = new LongestPathAlgorithm();
    }
    
    public void loadDataAndParse() {
        parserService.loadGraphData();
    }

    public Map<Integer, int[]> getEdges() {
        return parserService.getParsedEdges();
    }
    
    public void loadSequenceModel(AbstractGraphViewModel model) {
    }
    
    public Int2ObjectLinkedOpenHashMap<Node> getParsedSequences() {
        Int2ObjectLinkedOpenHashMap<Node> nodeMap = parserService.getParsedSequences();
        gridHandler = new GridHandler(new Grid(10, nodeMap.size()));//new Grid should come from storage
        //todo hashmap should be dumped into gridhandler so it can draw on its grid. 
        //the grid and gridhandler with the backing nodes should become the model that serves as the intermediate between backend and frontend
        return nodeMap;
    }
    
    public Short2ObjectOpenHashMap<String> getGenomeNames() {
        return parserService.getParsedGenomeNames();
    }
    
    public Int2ObjectLinkedOpenHashMap<Bubble> getDetectedBubbles(Int2ObjectLinkedOpenHashMap<Node> sequencesDataMap) {
        sbAlgorithm.detectSuperBubbles(sequencesDataMap.values().toArray(new Node[sequencesDataMap.values().size()]));
        return sbAlgorithm.getDetectedBubbles();
    }
    
    public List<Integer> calculateLongestPath(final Int2ObjectLinkedOpenHashMap<Node> sequencesDataMap, final int sourceNodeId, final int targetNodeId) {
        return lpAlgorithm.findLongestPathBFS(new ArrayList<Integer>(), sequencesDataMap, sourceNodeId, targetNodeId);
    }

    public GridHandler getGridHandler() {
        return gridHandler;
    }
}
 