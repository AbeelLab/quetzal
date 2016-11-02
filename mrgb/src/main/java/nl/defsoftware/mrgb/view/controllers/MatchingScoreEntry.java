package nl.defsoftware.mrgb.view.controllers;

import nl.defsoftware.mrgb.models.graph.Node;

/**
 * Utility class to maintain a score and a reference
 * @author D.L. Ettema
 *
 */
public class MatchingScoreEntry implements Comparable<MatchingScoreEntry>{
    
    private short score;
    private Node parentNode;
    private int childNodeId;
    
    public MatchingScoreEntry(short score, Node parentNode, int childNodeId) {
        this.score = score;
        this.parentNode = parentNode;
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
