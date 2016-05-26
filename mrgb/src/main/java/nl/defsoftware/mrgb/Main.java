package nl.defsoftware.mrgb;
	
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import nl.defsoftware.mrgb.services.GraphGFAParser;
import nl.defsoftware.mrgb.services.ParserService;
import nl.defsoftware.mrgb.services.ParserServiceImpl;
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
	
	private MrgbController mrgbController = new MrgbController();
	
	@Override
	public void start(Stage primaryStage) {
		try {
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Mrgb.fxml"));
			fxmlLoader.setController(mrgbController);
			VBox root = (VBox)fxmlLoader.load();
			Scene scene = new Scene(root, 1024, 768);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			
			
			loadProperties();
			initialiseParser();

			
			primaryStage.show();
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @TODO refactor to be user initiated, not at startup
	 * @throws UnsupportedEncodingException
	 * @throws FileNotFoundException
	 * 
	 */
	private void initialiseParser() throws UnsupportedEncodingException, FileNotFoundException {
		ParserServiceImpl parserService = new ParserServiceImpl();
		parserService.loadGraphData();
		mrgbController.setGraphMap(parserService.getParsedEdges());
	}

	private void loadProperties() throws IOException, FileNotFoundException {
		Properties properties = System.getProperties();
		FileInputStream fis = new FileInputStream(RESOURCES_PREFIX_PATH.concat("application.properties"));
		properties.load(fis);
		fis.close();
	}
	
	private void exit() {
		Platform.exit();
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
