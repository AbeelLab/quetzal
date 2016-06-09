/**
 * 
 */
package nl.defsoftware.mrgb.view.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 *
 * @author D.L. Ettema
 * @date 8 Jun 2016
 */
public class GraphModel {

	Sequence graphParent;

	List<Sequence> allSequences;
	List<Sequence> addedSequences;
	List<Sequence> removedSequences;

	List<Edge> allEdges;
	List<Edge> addedEdges;
	List<Edge> removedEdges;

	Map<Short, Sequence> sequenceMap; // <id,Sequence>
	Map<Integer, List<Short>> xCoordinateMap; // <xCoordinate, ids[]>

	public GraphModel() {
		graphParent = new Sequence(new Short("0"));
		// clear model, create lists
		clear();
	}

	public void clear() {
		allSequences = new ArrayList<>();
		addedSequences = new ArrayList<>();
		removedSequences = new ArrayList<>();

		allEdges = new ArrayList<>();
		addedEdges = new ArrayList<>();
		removedEdges = new ArrayList<>();

		sequenceMap = new HashMap<Short, Sequence>(); // <id,Sequence>
		xCoordinateMap = new HashMap<Integer, List<Short>>();
	}

	public void clearAddedLists() {
		addedSequences.clear();
		addedEdges.clear();
	}

	public List<Sequence> getAddedSequences() {
		return addedSequences;
	}

	public List<Sequence> getRemovedSequences() {
		return removedSequences;
	}

	public List<Sequence> getAllSequences() {
		return allSequences;
	}

	public List<Edge> getAddedEdges() {
		return addedEdges;
	}

	public List<Edge> getRemovedEdges() {
		return removedEdges;
	}

	public List<Edge> getAllEdges() {
		return allEdges;
	}

	public Sequence addSequence(Short id, int x, int y) {
		Sequence sequence = new Sequence(id);
		sequence.relocate(x, y);
		addSequence(sequence, x);
		return sequence;
	}
	
	/**
	 * Can be null
	 * @param id
	 * @return
	 * 
	 * Sequence
	 *
	 */
	public Sequence findSequenceById(Short id) {
		return sequenceMap.get(id);
	}

	public List<Short> findIdsAtXCoordinate(int xCoordinate) {
		return xCoordinateMap.getOrDefault(xCoordinateMap, new ArrayList<Short>());
	}

	private void addSequence(Sequence sequence, int xCoord) {
		addedSequences.add(sequence);
		sequenceMap.put(sequence.getSequenceId(), sequence);
		List<Short> ids = xCoordinateMap.getOrDefault(xCoord, new ArrayList<Short>());
		ids.add(sequence.getSequenceId());
		xCoordinateMap.put(xCoord, ids);
	}

	public void addEdge(Short sourceId, Short targetId) {
		Sequence sourceSequence = sequenceMap.get(sourceId);
		Sequence targetSequence = sequenceMap.get(targetId);
		Edge edge = new Edge(sourceSequence, targetSequence);

		addedEdges.add(edge);
	}

	/**
	 * Attach all Sequences which don't have a parent to graphParent
	 * 
	 * @param sequenceList
	 */
	public void attachOrphansToGraphParent(List<Sequence> sequenceList) {
		for (Sequence sequence : sequenceList) {
			if (sequence.getSequenceParents().size() == 0) {
				graphParent.addSequenceChild(sequence);
			}
		}
	}

	/**
	 * Remove the graphParent reference if it is set
	 * 
	 * @param sequenceList
	 */
	public void disconnectFromGraphParent(List<Sequence> sequenceList) {
		for (Sequence sequence : sequenceList) {
			graphParent.removeSequenceChild(sequence);
		}
	}

	public void merge() {
		// Sequences
		allSequences.addAll(addedSequences);
		allSequences.removeAll(removedSequences);

		addedSequences.clear();
		removedSequences.clear();

		// edges
		allEdges.addAll(addedEdges);
		allEdges.removeAll(removedEdges);

		addedEdges.clear();
		removedEdges.clear();
	}
}
