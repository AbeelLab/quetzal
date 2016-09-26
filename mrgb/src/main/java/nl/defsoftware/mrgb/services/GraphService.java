/**
 * 
 */
package nl.defsoftware.mrgb.services;

import java.util.HashMap;

/**
 * A service that takes care of retrieving and storing graph data.
 * 
 * @author D.L. Ettema
 * @date 23 September 2016
 *
 */
public class GraphService {

    private FileParserService parserService;
    
    public GraphService() {
        parserService = new FileParserServiceImpl();
    }
    
    public HashMap<Short, short[]> loadDataAndParse() {
        parserService.loadGraphData();
        return parserService.getParsedEdges();
    }
    
}
 