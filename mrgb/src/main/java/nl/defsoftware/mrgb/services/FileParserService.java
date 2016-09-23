/**
 * 
 */
package nl.defsoftware.mrgb.services;

import java.util.HashMap;

/**
 *
 *
 * @author D.L. Ettema
 * @date 26 May 2016
 */
public interface FileParserService {

	void loadGraphData();
	
	HashMap<Short, short[]> getParsedEdges();
}
