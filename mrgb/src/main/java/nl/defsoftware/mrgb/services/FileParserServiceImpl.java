/**
 * 
 */
package nl.defsoftware.mrgb.services;

import java.util.HashMap;

/**
 * Generic file parser that is responsible for loading the various data files:
 * GFA, metadata, annotations etc.
 *
 * @author D.L. Ettema
 * @date 26 May 2016
 */
public class FileParserServiceImpl implements FileParserService {

    private FileParser graphParser = new GraphGFAParser();

    @Override
    public void loadGraphData() {
        try {
            graphParser.loadResource();
        } catch (Exception e) {
            e.printStackTrace();
        }
        graphParser.parseData();
    }

    @Override
    public HashMap<Short, short[]> getParsedEdges() {
        return graphParser.getParsedEdges();
    }
}
