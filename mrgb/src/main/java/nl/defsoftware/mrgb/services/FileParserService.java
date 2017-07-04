/**
 * 
 */
package nl.defsoftware.mrgb.services;

import java.util.Map;

import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.shorts.Short2ObjectOpenHashMap;
import nl.defsoftware.mrgb.graphs.models.Node;

/**
 *
 *
 * @author D.L. Ettema
 * @date 26 May 2016
 */
public interface FileParserService {

    public void loadGraphData();

    public Map<Integer, int[]> getParsedEdges();

    public Int2ObjectLinkedOpenHashMap<Node> getParsedSequences();

    public Short2ObjectOpenHashMap<String> getParsedGenomeNames();
}
