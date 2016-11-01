package nl.defsoftware.mrgb.fileparsers;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;

import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.shorts.Short2ObjectOpenHashMap;
import nl.defsoftware.mrgb.models.graph.Node;

public interface FileParser {
	
	public void loadResource() throws IOException, UnsupportedEncodingException, FileNotFoundException;

	public void parseData();
	
	/** Temporarily methods, since the parser should persist the data. */
	public Map<Integer, int[]> getParsedEdges();

	/** Temporarily methods, since the parser should persist the data. */
	public Int2ObjectLinkedOpenHashMap<Node> getParsedSequences();

	/** Temporarily methods, since the parser should persist the data. */
    public Short2ObjectOpenHashMap<String> getParsedGenomeNames();

    public boolean isParsed();
	
}
