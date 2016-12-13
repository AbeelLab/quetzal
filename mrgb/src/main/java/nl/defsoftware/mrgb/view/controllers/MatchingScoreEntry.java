package nl.defsoftware.mrgb.view.controllers;

import nl.defsoftware.mrgb.models.graph.Node;

/**
 * Utility class to maintain a score and a reference
 * @author D.L. Ettema
 *
 */
public class MatchingScoreEntry implements Comparable<MatchingScoreEntry> {
    
    private int score;
    private Node parentNode;
    private int childNodeId;
    
    public MatchingScoreEntry(int score, Node parentNode, int childNodeId) {
        this.score = score;
        this.parentNode = parentNode;
        this.childNodeId = childNodeId;
    }

    @Override
    public int compareTo(MatchingScoreEntry o) {
//        return Integer.compare(score, o.getScore());
        return Math.negateExact(Integer.compare(score, o.getScore()));
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public Node getParentNode() {
        return parentNode;
    }

    public void setParentNodeId(Node parentNode) {
        this.parentNode = parentNode;
    }

    public int getChildNodeId() {
        return childNodeId;
    }

    public void setChildNodeId(int childNodeId) {
        this.childNodeId = childNodeId;
    }
}
