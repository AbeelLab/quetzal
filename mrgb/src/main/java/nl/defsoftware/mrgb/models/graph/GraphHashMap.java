/**
 * 
 */
package nl.defsoftware.mrgb.models.graph;

import java.util.concurrent.ConcurrentHashMap;

/**
 *
 *
 * @author D.L. Ettema
 * @date 26 May 2016
 */
public class GraphHashMap extends ConcurrentHashMap<Integer, Graph> {

	public void putGraph(Integer graphId, Graph graph) {
		put(graphId, graph);
	}
	
	public Graph getGraph(Integer graphId) {
		return get(graphId);
	}
	
}
