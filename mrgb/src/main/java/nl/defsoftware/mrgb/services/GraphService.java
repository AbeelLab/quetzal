/**
 * 
 */
package nl.defsoftware.mrgb.services;

import java.util.Map;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.shorts.Short2ObjectOpenHashMap;
import nl.defsoftware.mrgb.models.Rib;

/**
 * A service that takes care of retrieving and storing graph data.
 * 
 * @author D.L. Ettema
 * @date 23 September 2016
 *
 */
public class GraphService {

    private FileParserService parserService = new FileParserServiceImpl();
    
    public GraphService() {
    }
    
    public void loadDataAndParse() {
        parserService.loadGraphData();
    }

    public Map<Integer, int[]> getEdges() {
        return parserService.getParsedEdges();
    }
    
    public Int2ObjectOpenHashMap<Rib> getSequences() {
        return parserService.getParsedSequences();
    }
    
    public Short2ObjectOpenHashMap<String> getGenomeNames() {
        return parserService.getParsedGenomeNames();
    }
    
}
 