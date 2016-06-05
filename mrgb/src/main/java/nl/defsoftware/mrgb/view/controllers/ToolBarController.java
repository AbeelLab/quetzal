package nl.defsoftware.mrgb.view.controllers;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ToolBar;
import nl.defsoftware.mrgb.services.ParserServiceImpl;

public class ToolBarController extends ToolBar {
	private static final Logger log = LoggerFactory.getLogger(ToolBarController.class);

	private ParserServiceImpl parserService;
	private RootController parentContoller;

	@FXML
	private Button loadDataButton;

	public ToolBarController() {
		log.info("init action toolbar Controller");
		parserService = new ParserServiceImpl();
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("toolbar.fxml"));
		fxmlLoader.setRoot(this);
		fxmlLoader.setController(this);
		try {
			fxmlLoader.load();
		} catch (IOException exception) {
			throw new RuntimeException(exception);
		}
	}

	public void setRootController(RootController parentContoller) {
		this.parentContoller = parentContoller;
	}

	@FXML
	private void loadDataButtonAction(ActionEvent event) {
		System.out.println("You clicked me!");
		initialiseParser();
	}

	/**
	 * @throws UnsupportedEncodingException
	 * @throws FileNotFoundException
	 * 
	 */
	private void initialiseParser() {
		parserService.loadGraphData();
		parentContoller.setGraphMap(parserService.getParsedEdges());
		// setGraphMapForNodeLabels(parserService.getParsedEdges());
	}
}
