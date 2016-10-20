package nl.defsoftware.mrgb;
	
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javafx.application.Application;
import javafx.stage.Stage;
import nl.defsoftware.mrgb.fileparsers.GraphGFAParser;
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

	private void initialiseParser() throws IOException, UnsupportedEncodingException, FileNotFoundException {
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
		System.out.println("id=" + id);
		
	}
	
	private static int summation() {
	    List<Integer> input = new ArrayList<>(); 
	    input.add(123);
	    input.add(-2);
	    input.add(477);
	    input.add(3);
	    input.add(14);
	    input.add(6551);
	    
	    List<Integer> result = input; 
	    
        for (Iterator iter = input.iterator(); iter.hasNext(); ) {
            Integer inet = (Integer) iter.next();
            fold(result, inet);
        }
        
        return result.get(result.size() - 1);
    }
    
    private static void fold(List<Integer> result, int add) {
        for( int i = 0; i < result.size(); i++) {
            result.set(i, result.get(i) + add);
        }
    }
	
}
