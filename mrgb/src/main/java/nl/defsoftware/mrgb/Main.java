package nl.defsoftware.mrgb;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import nl.defsoftware.mrgb.services.ParserServiceImpl;
import nl.defsoftware.mrgb.view.controllers.RootController;

/**
 * 
 * @author D.L. Ettema 22 May 2016
 */
public class Main extends Application {

	private static String RESOURCES_PREFIX_PATH = "src/main/resources/";

	private RootController rootController;

	@Override
	public void start(Stage mainStage) {
		try {
			loadProperties();

			rootController = new RootController();
			createStage(mainStage, rootController);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void createStage(Stage mainWindow, Parent root) {
		Scene container = new Scene(root, 1024, 768);
		container.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		mainWindow.setScene(container);
		mainWindow.setTitle("Multi-reference comparative genome browser");
		mainWindow.show();
	}

	private void loadProperties() throws IOException, FileNotFoundException {
		FileInputStream fis = new FileInputStream(RESOURCES_PREFIX_PATH.concat("application.properties"));
		System.getProperties().load(fis);
		fis.close();
	}

	private void exit() {
		Platform.exit();
	}

	public static void main(String[] args) {
		launch(args);
	}
}
