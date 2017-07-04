package nl.defsoftware.mrgb.view.models;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

/**
 * 
 *
 *
 * @author D.L. Ettema
 * @date 6 Jun 2016
 */
public class Edge extends Group {

    protected int capacity;
    protected int lower;
    protected int flow;
    
    protected Sequence from;
    protected Sequence to;

    private Line line;

    // used to construct the backwards edge
    private Edge(Edge e) {
        this.flow = e.getCapacity();// saturating this edge, so that we don't consider it in a path.
        this.capacity = e.getCapacity();
        this.lower = e.getLowerCapacity();
        this.from = e.getTo();
        this.to = e.getFrom();
    }

    protected Edge(int capacity, Sequence from, Sequence to) {
        this.capacity = capacity;
        this.from = from;
        this.to = to;
        this.flow = 0;
    }

    protected Edge(int lower, int upper, Sequence from, Sequence to) {
        this.lower = lower;
        this.capacity = upper;
        this.from = from;
        this.to = to;
        this.flow = 0;
    }
    
    protected Edge(Sequence from, Sequence to) {
    	from.addSequenceChild(to);
      to.addSequenceParent(from);

      line = new Line();
      line.setStrokeWidth(0.85);
      line.setStroke(Color.BLUE);
      line.startXProperty().bind( from.layoutXProperty().add(from.getBoundsInParent().getWidth() / 2.0));
      line.startYProperty().bind( from.layoutYProperty().add(from.getBoundsInParent().getHeight() / 2.0));

      line.endXProperty().bind( to.layoutXProperty().add( to.getBoundsInParent().getWidth() / 2.0));
      line.endYProperty().bind( to.layoutYProperty().add( to.getBoundsInParent().getHeight() / 2.0));

      getChildren().add(line);
    }

    public void redrawLineOfEdge() {
        line.startXProperty().bind( from.layoutXProperty().add(from.getBoundsInParent().getWidth() / 2.0));
      line.startYProperty().bind( from.layoutYProperty().add(from.getBoundsInParent().getHeight() / 2.0));

      line.endXProperty().bind( to.layoutXProperty().add( to.getBoundsInParent().getWidth() / 2.0));
      line.endYProperty().bind( to.layoutYProperty().add( to.getBoundsInParent().getHeight() / 2.0));
    }
    
    /**
     * This increases the flow for this edge and sets the flow for the backwards (could be the residual) edge.
     * 
     * @param add
     */
    public void augmentFlow(int add) {

        assert (flow + add <= capacity);
        flow += add;
    }

    public int getLowerCapacity() {
        return lower;
    }

    public void setLowerCapacity(int lower) {
        this.lower = lower;
    }

    public int getCapacity() {
        return capacity;
    }

    public int getFlow() {
        return flow;
    }
    public int getResidual() {
        return capacity - flow;
    }
    protected void setCapacity(int upper) {
        this.capacity = upper;
    }

		public Sequence getFrom() {
			return from;
		}

		public void setFrom(Sequence from) {
			this.from = from;
		}

		public Sequence getTo() {
			return to;
		}

		public void setTo(Sequence to) {
			this.to = to;
		}

		public void setFlow(int flow) {
			this.flow = flow;
		}
    
}
