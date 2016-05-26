package nl.defsoftware.mrgb.services;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Properties;
import java.util.Scanner;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.defsoftware.mrgb.Constants;
import nl.defsoftware.mrgb.models.Sequence;

/**
 * 
 * @author D.L. Ettema
 * 22 May 2016
 */
public class GraphGFAParser implements Parser {
	Logger logger = LoggerFactory.getLogger(GraphGFAParser.class);
	
	/* Local Main only */
	private Properties properties = new Properties();

	
	/* Reader for the datasource */
	private BufferedReader reader = null;
	private String SEQUENCE = "S";
	private String LINK = "L";
	
	private static int GFA_LINE_INDICATOR = 0;
	private static int GFA_FROM_NODE = 1;
	private static int GFA_SEQUENCE = 2;
	private static int GFA_TO_NODE = 3;
	private static int GFA_ORI = 4;
	private static int GFA_CRD = 5;
	private static int GFA_CRDCTG = 6;
	private static int GFA_CTG = 7;
	private static int GFA_START = 8;
	
	private HashMap<Short, short[]> edgesMap = new HashMap<>();
	private HashMap<Short, Sequence> sequencesMap = new HashMap<>();
	
//	public static void main(String [] args) throws Exception {
//		GraphDataParser p = new GraphDataParser();
//		p.loadProperties();
//		p.loadResource();
//		p.parseData();
//		
//	}
//	
//	/* Local Main only */
//	private void loadProperties() throws IOException {
//		properties.load(new FileInputStream(Constants.PREFIX_PATH.concat("application.properties")));
//	}
	
	@Override
	public void loadResource() throws UnsupportedEncodingException, FileNotFoundException {
		String dataPath = Constants.PREFIX_PATH.concat(System.getProperties().getProperty(Constants.GRAPH_DATA));
		reader = new BufferedReader(new InputStreamReader(new FileInputStream(dataPath), "UTF-8"));
	}
	
	public void closeResources() throws IOException {
		reader.close();
	}
	
	public void parseData() {
		logger.info("Parsing data");
		Scanner scanner = new Scanner(reader);
		Pattern pattern = Pattern.compile("\t");
		while( scanner.hasNextLine() ) {
			String [] aLine = pattern.split(scanner.nextLine(), 0);
			if (StringUtils.equals(SEQUENCE, aLine[GFA_LINE_INDICATOR])) {
				processSequence(aLine);
			} else if (StringUtils.equals(LINK, aLine[GFA_LINE_INDICATOR])) {
				processLink(aLine);
			}
		}
		logger.info("Finished parsing graph data");
		scanner.close();
	}


	
	private void processSequence(String [] aLine) {
		for (int i = 0; i < aLine.length; i++) {
			Sequence aSequence = new Sequence(Integer.parseInt(aLine[GFA_FROM_NODE]), aLine[GFA_SEQUENCE].toCharArray() , null, null, null, null);
			sequencesMap.put(Short.parseShort(aLine[GFA_FROM_NODE]), aSequence);
		}
	}
	
	private void processLink(String [] aLine) {
		for (int i = 0; i < aLine.length; i++) {
			if (edgesMap.containsKey(aLine[GFA_FROM_NODE])) {
				short [] link = edgesMap.get(aLine[1]);
				link[link.length] = Short.valueOf(aLine[GFA_TO_NODE]);
			} else {
				edgesMap.put(Short.valueOf(aLine[GFA_FROM_NODE]), new short[]{ Short.valueOf(aLine[GFA_TO_NODE]) });
			}
		}
	}

	public HashMap<Short, short[]> getParsedEdges() {
		return edgesMap;
		
	}
}
