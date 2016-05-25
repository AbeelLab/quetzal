package nl.defsoftware.mrgb;
	
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

import javafx.application.Application;
import javafx.stage.Stage;
import nl.defsoftware.mrgb.services.GraphDataParser;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.fxml.FXMLLoader;

/**
 * 
 * @author D.L. Ettema
 * 22 May 2016
 */
public class Main extends Application {
	
	private static String RESOURCES_PREFIX_PATH = "src/main/resources/";
	
	@Override
	public void start(Stage primaryStage) {
		try {
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Mrgb.fxml"));
			fxmlLoader.setController(new MrgbController());
			VBox root = (VBox)fxmlLoader.load();
			Scene scene = new Scene(root, 1024, 768);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			
//			loadProperties();
//			initialiseParser();
			
			primaryStage.show();
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	private void initialiseParser() throws UnsupportedEncodingException, FileNotFoundException {
		GraphDataParser p = new GraphDataParser();
		p.loadResource();
		p.parseData();
	}

	private void loadProperties() throws IOException, FileNotFoundException {
		Properties properties = System.getProperties();
		properties.load(new FileInputStream(RESOURCES_PREFIX_PATH.concat("application.properties")));
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
