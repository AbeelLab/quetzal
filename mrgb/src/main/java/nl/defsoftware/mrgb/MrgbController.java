package nl.defsoftware.mrgb;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;

public class MrgbController implements Initializable {

	@FXML
	ScrollPane graphPane;
	
	@FXML
	AnchorPane graphAnchorPane;

	/* (non-Javadoc)
	 * @see javafx.fxml.Initializable#initialize(java.net.URL, java.util.ResourceBundle)
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		System.out.println("MRGB Controller");
		graphAnchorPane.getChildren().add(new Circle(10.0, Paint.valueOf("red")));
		
	}
	
	
}

