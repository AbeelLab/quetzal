/**
 * 
 */
package nl.defsoftware.mrgb.view.models;

import java.util.concurrent.ConcurrentHashMap;

import nl.defsoftware.mrgb.view.models.Graph;

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
