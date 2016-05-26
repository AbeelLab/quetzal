package nl.defsoftware.mrgb;
	
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

import javafx.application.Application;
import javafx.stage.Stage;
import nl.defsoftware.mrgb.services.GraphGFAParser;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.fxml.FXMLLoader;

/**
 * 
 * @author D.L. Ettema
 * 22 May 2016
 */
public class SandboxMain extends Application {
	
	private static String RESOURCES_PREFIX_PATH = "src/main/resources/";
	
	@Override
	public void start(Stage primaryStage) {
		try {
			BorderPane root = (BorderPane)FXMLLoader.load(getClass().getResource("Main.fxml"));
			Scene scene = new Scene(root,400,400);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			
			loadProperties();
			initialiseParser();
			
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	private void initialiseParser() throws UnsupportedEncodingException, FileNotFoundException {
		GraphGFAParser p = new GraphGFAParser();
		p.loadResource();
		p.parseData();
	}

	private void loadProperties() throws IOException, FileNotFoundException {
		Properties properties = System.getProperties();
		properties.load(new FileInputStream(RESOURCES_PREFIX_PATH.concat("application.properties")));
	}
	
	public static void main(String[] args) {
		
		int id = summation();
		System.out.println("id=" + id + " Integertype="+ Integer.class.isInstance(id));
//		launch(args);
		System.exit(0);
	}
	
	private static int summation() {
		short id = 2;
		short offset = 5;
		short arrayOffset = 10;
		return (id + offset) * arrayOffset;
	}
}
