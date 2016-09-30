package nl.defsoftware.mrgb.view.controllers;

import nl.defsoftware.mrgb.models.Rib;

/**
 * Utility class to maintain a score and a reference
 * @author D.L. Ettema
 *
 */
public class MatchingScoreEntry implements Comparable<MatchingScoreEntry>{
    
    private Short score;
    private Rib parentRib;
    private int childNodeId;
    
    public MatchingScoreEntry(Short score, Rib parentRib, int childNodeId) {
        this.score = score;
        this.parentRib = parentRib;
        this.childNodeId = childNodeId;
    }

    @Override
    public int compareTo(MatchingScoreEntry o) {
        return score.compareTo(o.getScore());
    }

    public Short getScore() {
        return score;
    }

    public void setScore(Short score) {
        this.score = score;
    }

    public Rib getParentRib() {
        return parentRib;
    }

    public void setParentNodeId(Rib parentRib) {
        this.parentRib = parentRib;
    }

    public int getChildNodeId() {
        return childNodeId;
    }

    public void setChildNodeId(int childNodeId) {
        this.childNodeId = childNodeId;
    }
}
