package nl.defsoftware.mrgb;

import java.net.URL;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import nl.defsoftware.mrgb.services.GraphDataParser;

public class MrgbController implements Initializable {
	Logger logger = LoggerFactory.getLogger(GraphDataParser.class);
	
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
	
	public MrgbController() {
		super();
	}
	
	/* (non-Javadoc)
	 * @see javafx.fxml.Initializable#initialize(java.net.URL, java.util.ResourceBundle)
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		logger.info("MRGB Controller");
		Circle c = new Circle(20.0, new Color(0.1,0.8,0.8,0.5));
		c.setLayoutY(200);
		c.setLayoutX(200);
		graphAnchorPane.getChildren().add(c);
		
		
	}
	
	@FXML
	private void onMouseEntered(ActionEvent event) {
		logger.info("mouse detected");
		textField.setText("mouse detected");
	}
	
	
}
