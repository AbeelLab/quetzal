package nl.defsoftware.mrgb;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap.KeySetView;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import nl.defsoftware.mrgb.view.models.Graph;
import nl.defsoftware.mrgb.view.models.GraphHashMap;
import nl.defsoftware.mrgb.view.models.Node;

public class MrgbController implements Initializable {
	Logger logger = LoggerFactory.getLogger(MrgbController.class);
	
	@FXML
	private ScrollPane graphPane;
	
	@FXML
	private AnchorPane graphAnchorPane;

	@FXML
	private VBox root;
	
	@FXML
	Circle circle;

	@FXML
	private TextField textField;
	
//	private GraphHashMap graphMap;
	private HashMap<Short, short[]> graphMap;
	private Color nodeColor = new Color(0, 0, 0, 0.5);
	
	public MrgbController() {
		super();
	}
	
	public void setGraphMap(HashMap<Short, short[]> graphMap) {
		this.graphMap = graphMap;
		Set<Short> keys = graphMap.keySet();
		
		graphAnchorPane.getChildren().add(createNode(1, 100));
		
		for (Short key : keys) {
			graphAnchorPane.getChildren().addAll(createNodes(graphMap.get(key)));
		}
	}
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		logger.info("init MRGBController");
	}
	
	private List<Circle> createNodes(short[] ids) {
		List<Circle> nodes = new ArrayList<Circle>(ids.length);
		logger.info(String.valueOf(ids.length));
		int heightPos = 100;
		int heightOffset = 0;
		for (int i = 0; i < ids.length; i++) {
			if( i > 0) {
				heightOffset += 40;
			}
			nodes.add(createNode(ids[i], heightPos + heightOffset));
		}
		return nodes;
	}
	
	private Circle createNode(int id, int heightPos) {
		Circle c = new Circle(10.0, nodeColor);
		c.setAccessibleText(Integer.toString(id));
		c.setCenterX(id * 100);
		c.setCenterY(heightPos);
		return c;
	}
	
}
