package nl.defsoftware.mrgb.fileparsers;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;

public interface FileParser {
	
	public void loadResource() throws IOException, UnsupportedEncodingException, FileNotFoundException;

	public void parseData();
	
	public HashMap<Short, short[]> getParsedEdges();
	
}
