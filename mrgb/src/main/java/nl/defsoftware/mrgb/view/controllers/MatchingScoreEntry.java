package nl.defsoftware.mrgb.view.controllers;

import nl.defsoftware.mrgb.models.graph.AbstractNode;
import nl.defsoftware.mrgb.models.graph.Node;

/**
 * Utility class to maintain a score and a reference
 * @author D.L. Ettema
 *
 */
public class MatchingScoreEntry implements Comparable<MatchingScoreEntry>{
    
    private short score;
    private Node parentRib;
    private int childNodeId;
    
    public MatchingScoreEntry(short score, Node parentRib, int childNodeId) {
        this.score = score;
        this.parentRib = parentRib;
        this.childNodeId = childNodeId;
    }

    @Override
    public int compareTo(MatchingScoreEntry o) {
        return Math.negateExact(Short.compare(score, o.getScore()));
    }

    public short getScore() {
        return score;
    }

    public void setScore(short score) {
        this.score = score;
    }

    public Node getParentRib() {
        return parentRib;
    }

    public void setParentNodeId(Node parentRib) {
        this.parentRib = parentRib;
    }

    public int getChildNodeId() {
        return childNodeId;
    }

    public void setChildNodeId(int childNodeId) {
        this.childNodeId = childNodeId;
    }
}
