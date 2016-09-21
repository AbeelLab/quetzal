package nl.defsoftware.mrgb.view.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.MenuBar;
import javafx.scene.layout.VBox;

public class RootController extends VBox implements Initializable {
	private static final Logger log = LoggerFactory.getLogger(RootController.class);

	@FXML
	private MenuBar menubar;
	
	@FXML
	private ToolBarController toolbar;
	
	private GraphController graphController;

	public RootController() {
		log.info("Root Controller constructor");
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("mrgb.fxml"));
		fxmlLoader.setRoot(this);
		fxmlLoader.setController(this);
		try {
			fxmlLoader.load();
		} catch (IOException exception) {
			log.error(exception.getMessage());
			throw new RuntimeException(exception);
		}
		setManaged(true);
		
		graphController = new GraphController();
		graphController.getScrollPane().prefWidthProperty().bind(this.widthProperty());
		graphController.getScrollPane().prefHeightProperty().bind(this.heightProperty());
		this.getChildren().add(graphController.getScrollPane());
	}
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		log.info("Root Controller init");
		toolbar.setRootController(this);
		
	}
	
	/** @TODO refactor to listener construction **/
	public void setGraphMap(HashMap<Short, short[]> graphMap) {
		graphController.setGraphMap(graphMap);
	}
}
