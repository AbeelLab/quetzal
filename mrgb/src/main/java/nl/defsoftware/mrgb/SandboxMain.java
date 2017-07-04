package nl.defsoftware.mrgb;
	
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import nl.defsoftware.mrgb.dao.GFAFileParser2;

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
//			BorderPane root = (BorderPane)FXMLLoader.load(getClass().getResource("Main.fxml"));
		    AnchorPane root = new AnchorPane();
		    root.getChildren().add(addScrollPane(root));
//		    root.getChildren().add(addAnotherPane());
		    
			Scene scene = new Scene(root);
			
//			loadProperties();
//			initialiseParser();
			
			primaryStage.setWidth(500.0);
			primaryStage.setHeight(500.0);
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	private Node addScrollPane(AnchorPane root) {
	    Pane content = new Pane(new Circle(150.0, Paint.valueOf("blue")));
        content.setMinHeight(5000.0);
        content.setMinWidth(700.0);
        content.setBorder(new Border(new BorderStroke(Paint.valueOf("purple"), BorderStrokeStyle.SOLID, null, new BorderWidths(5.0))));
        
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(content);
        scrollPane.setHbarPolicy(ScrollBarPolicy.ALWAYS);
        scrollPane.setVbarPolicy(ScrollBarPolicy.ALWAYS);
        scrollPane.setBorder(new Border(new BorderStroke(Paint.valueOf("red"), BorderStrokeStyle.SOLID, null, new BorderWidths(5.0))));
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);
        scrollPane.prefHeightProperty().bind(root.heightProperty());
        scrollPane.prefWidthProperty().bind(root.widthProperty());
        scrollPane.setPannable(true);
        return scrollPane;
    }

    private Node addAnotherPane() {
	    Pane aPane = new Pane();
	    aPane.setMinHeight(100.0);
	    aPane.setMinWidth(500.0);
	    aPane.setBorder(new Border(new BorderStroke(Paint.valueOf("green"), BorderStrokeStyle.SOLID, null, new BorderWidths(5.0))));
        return aPane;
    }

    private void initialiseParser() throws IOException, UnsupportedEncodingException, FileNotFoundException {
		GFAFileParser2 p = new GFAFileParser2();
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
