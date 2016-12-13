package nl.defsoftware.mrgb.view.models;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javafx.scene.control.Label;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Shape;

public abstract class AbstractGraphViewModel<V> implements IGraphViewModel<V> {

    protected Sequence graphParent;
    protected Set<Shape> allSequences;
    protected Set<Shape> addedSequences;
    protected SortedSet<Shape> allSortedSequences;
    protected List<Shape> removedSequences;
    protected List<Label> allLabels;
    
    public AbstractGraphViewModel() {
        clear();
    }

    public void clear() {
        graphParent = new Sequence(new Integer(0));
    	allSequences = new HashSet<>();
    	addedSequences = new HashSet<>();
    	removedSequences = new ArrayList<>();
    	allLabels = new ArrayList<>();
    	allSortedSequences = new TreeSet<>();
    }

    @Override
    public void merge() {
        // Sequences
        allSequences.addAll(addedSequences);
        allSequences.removeAll(removedSequences);
    
        addedSequences.clear();
        removedSequences.clear();
        
        allLabels.clear();
    }

    public void addLabel(String text, int x, int y, int colour) {
        Label label = new Label(text);
        if (colour > 0) {
            label.setTextFill(Paint.valueOf("red"));
        }
        label.setStyle("font-size: 8px;");
        label.relocate(x, y);
        allLabels.add(label);
    }    
    
    public Set<Shape> getAllSequences() {
    	return allSequences;
    }

    public List<Label> getAllLabels() {
        return allLabels;
    }
    
    public Set<Shape> getAddedSequences() {
        return addedSequences;
    }

    public List<Shape> getRemovedSequences() {
        return removedSequences;
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
}