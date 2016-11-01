/**
 * 
 */
package nl.defsoftware.mrgb.services;

import java.util.Map;

import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.shorts.Short2ObjectOpenHashMap;
import nl.defsoftware.mrgb.fileparsers.FileParser;
import nl.defsoftware.mrgb.fileparsers.GFAFileParser2;
import nl.defsoftware.mrgb.models.graph.Node;

/**
 * Generic file parser that is responsible for loading the various data files:
 * GFA, metadata, annotations etc.
 *
 * @author D.L. Ettema
 * @date 26 May 2016
 */
public class FileParserServiceImpl implements FileParserService {

    private FileParser graphGFAParser = new GFAFileParser2();
    // private FileParser metadataParser = new MetadataFileParser();
    // private FileParser annotationParser = new AnnotationFileParser();

    @Override
    public void loadGraphData() {
        try {
            graphGFAParser.loadResource();
        } catch (Exception e) {
            e.printStackTrace();
        }
        graphGFAParser.parseData();
        // @TODO store in persistent storage
    }

    @Override
    public Map<Integer, int[]> getParsedEdges() {
        if (!graphGFAParser.isParsed()) {
            this.loadGraphData();
        }
        return graphGFAParser.getParsedEdges();
    }

    @Override
    public Int2ObjectLinkedOpenHashMap<Node> getParsedSequences() {
        if (!graphGFAParser.isParsed()) {
            this.loadGraphData();
        }
        return graphGFAParser.getParsedSequences();
    }

    @Override
    public Short2ObjectOpenHashMap<String> getParsedGenomeNames() {
        if (!graphGFAParser.isParsed()) {
            this.loadGraphData();
        }
        return graphGFAParser.getParsedGenomeNames();
    }

}
