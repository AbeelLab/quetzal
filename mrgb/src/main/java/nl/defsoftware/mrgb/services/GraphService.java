/**
 * 
 */
package nl.defsoftware.mrgb.services;

import java.util.Map;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.shorts.Short2ObjectOpenHashMap;
import nl.defsoftware.mrgb.models.Rib;
import nl.defsoftware.mrgb.view.controllers.GraphHandler;
import nl.defsoftware.mrgb.view.models.GraphModel;

/**
 * A service that takes care of retrieving and storing graph data.
 * 
 * @author D.L. Ettema
 * @date 23 September 2016
 *
 */
public class GraphService {

    private FileParserService parserService = new FileParserServiceImpl();
    private GraphHandler graphHandler;
    
    public GraphService() {
        graphHandler = new GraphHandler();
    }
    
    public void loadDataAndParse() {
        parserService.loadGraphData();
    }

    public Map<Integer, int[]> getEdges() {
        return parserService.getParsedEdges();
    }
    
    public void loadSequenceModel(GraphModel model) {
        graphHandler.setAlternateGraphViewModel(model, parserService.getParsedSequences(), parserService.getParsedGenomeNames());
    }
    
    public Short2ObjectOpenHashMap<String> getGenomeNames() {
        return parserService.getParsedGenomeNames();
    }
    
}
 