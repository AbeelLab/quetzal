package nl.defsoftware.mrgb.view.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.MenuBar;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;

public class RootController extends VBox implements Initializable {
	private static final Logger log = LoggerFactory.getLogger(RootController.class);

	@FXML
	private MenuBar menubar;
	
	@FXML
	private ToolBarController toolbar;
	
	private GraphController graphController;

	public RootController() {
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
	}
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
	    
		graphController = new GraphController();
	    graphController.getContent().prefWidthProperty().bind(this.widthProperty());
	    graphController.getContent().prefHeightProperty().bind(this.heightProperty());
	    this.getChildren().add(graphController.getContent());
	    
	    this.setBackground(new Background(new BackgroundFill(Paint.valueOf("blue"), null, null)));
        setManaged(true);
		toolbar.setRootController(this);
		toolbar.addObserver(graphController);
	}
}
