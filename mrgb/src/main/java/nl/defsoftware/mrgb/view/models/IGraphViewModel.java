package nl.defsoftware.mrgb.view.models;

import java.util.List;
import java.util.Set;

import javafx.beans.property.DoubleProperty;
import javafx.scene.control.Label;
import javafx.scene.shape.Shape;
import nl.defsoftware.mrgb.models.graph.Node;

/**
 * @author D.L. Ettema
 *
 */
public interface IGraphViewModel<V> {

    //current
    public void addSequence(Node aNode, int rank, NodeDrawingData drawingData);
    
    public void addSequence(Integer id, int x, int y);
    
    
    public void addEdge(int childId, int parentId, int rank, DoubleProperty scaleYProperty);
    
    public void addEdge(int id, int startX, int startY, int endX, int endY, int rank);
    
    public void addEdge(int childId, int parentId, int rank);
    
    
    public void addLabel(String text, int x, int y, int colour);
    
    public void clear();
    
    public void merge();
    
    public Set<Shape> getAllSequences();
    
    public Set<Shape> getAddedSequences();

    public List<Shape> getRemovedSequences();
    
//    public List<Edge> getAllEdges(); //for GraphModel class only.
    
    public Set<V> getAllEdges();
    
    public Set<V> getAddedEdges();

    public Set<V> getRemovedEdges();
    
    public List<Label> getAllLabels();
}
