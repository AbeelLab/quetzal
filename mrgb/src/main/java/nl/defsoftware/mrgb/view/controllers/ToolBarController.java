package nl.defsoftware.mrgb.view.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.collections.MapChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.HBox;
import nl.defsoftware.mrgb.view.models.ActionStates;

public class ToolBarController extends HBox implements Initializable {
	private static final Logger log = LoggerFactory.getLogger(ToolBarController.class);

	
	private RootController parentContoller;
	private ActionStates<ActionStateEnums, Boolean> actionStates;

	@FXML
	private ToolBar toolbar;
	
	@FXML
	private Button loadDataButton;

	public ToolBarController() {
		log.info("init action toolbar Controller");
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("toolbar.fxml"));
		fxmlLoader.setRoot(this);
		fxmlLoader.setController(this);
		try {
			fxmlLoader.load();
		} catch (IOException exception) {
			log.error("Could not load resource related to toolbar.fxml" + exception.getMessage());
			throw new RuntimeException(exception);
		}
	}
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		actionStates = new ActionStates<>();
	}

	public void setRootController(RootController parentContoller) {
		this.parentContoller = parentContoller;
	}

	@FXML
	private void loadDataButtonAction(ActionEvent event) {
	    actionStates.setState(ActionStateEnums.LOAD_DATA_AND_PARSE, Boolean.TRUE);
	}

    public void addObserver(MapChangeListener<ActionStateEnums, Boolean> o) {
        actionStates.addListener(o);
    }
    
    public void removeObserver(MapChangeListener<ActionStateEnums, Boolean> o) {
        actionStates.removeListener(o);
    }
}
