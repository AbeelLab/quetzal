package nl.defsoftware.mrgb;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import nl.defsoftware.mrgb.view.controllers.RootController;

/**
 * 
 * @author D.L. Ettema 22 May 2016
 */
public class Main extends Application {

	private static String RESOURCES_PREFIX_PATH = "src/main/resources/";

	@Override
	public void start(Stage mainStage) {
		try {
			loadProperties();
			createStage(mainStage, new RootController());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void createStage(Stage mainWindow, Pane root) {
		Scene container = new Scene(root, 1024, 768);
		root.prefWidthProperty().bind(container.widthProperty());
		root.prefHeightProperty().bind(container.heightProperty());
		container.getStylesheets().add(getClass().getResource("/css/application.css").toExternalForm());
		mainWindow.setScene(container);
		mainWindow.setTitle("Multi-reference comparative genome browser");
		mainWindow.show();
	}

	private void loadProperties() throws IOException, FileNotFoundException {
		FileInputStream fis = new FileInputStream(RESOURCES_PREFIX_PATH.concat("application.properties"));
		System.getProperties().load(fis);
		fis.close();
	}

	public static void main(String[] args) {
		launch(args);
	}
}
