package nl.defsoftware.mrgb.fileparsers;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.defsoftware.mrgb.Constants;
import nl.defsoftware.mrgb.models.Sequence;

/**
 * A parser that as a result delivers one data structure that contains the links, nodes, sequences and the strains.
 *  
 * @author D.L. Ettema
 * @date 21 September 2016
 */
public class GraphGFAParser2 implements FileParser {
    private static final Logger log = LoggerFactory.getLogger(GraphGFAParser2.class);

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

    @Override
    public void loadResource() throws UnsupportedEncodingException, FileNotFoundException {
        String dataPath = Constants.PREFIX_PATH.concat(System.getProperties().getProperty(Constants.GRAPH_DATA));
        reader = new BufferedReader(new InputStreamReader(new FileInputStream(dataPath), "UTF-8"));
    }

    public void closeResources() throws IOException {
        reader.close();
    }

    @Override
    public HashMap<Short, short[]> getParsedEdges() {
        return edgesMap;
    }
    
    @Override
    public void parseData() {
        log.info("Parsing data");
        Scanner scanner = new Scanner(reader);
        Pattern pattern = Pattern.compile("\t");
        for (int i = 0; i < 1000 ; i++) {
        //for (int i = 0; scanner.hasNextLine(); i++) {
            String[] aLine = pattern.split(scanner.nextLine(), 0);
            if (StringUtils.equals(SEQUENCE, aLine[GFA_LINE_INDICATOR])) {
                processSequence(aLine);
            } else if (StringUtils.equals(LINK, aLine[GFA_LINE_INDICATOR])) {
                processEdges(aLine);
            }
        }
        log.info("Finished parsing graph data");
        scanner.close();
    }

    private void processSequence(String[] aLine) {
        for (int i = 0; i < aLine.length; i++) {
            Sequence aSequence = new Sequence(Integer.parseInt(aLine[GFA_FROM_NODE]), aLine[GFA_SEQUENCE].toCharArray(),
                    null, null, null, null);
            sequencesMap.put(Short.valueOf(aLine[GFA_FROM_NODE]), aSequence);
        }
    }

    private void processEdges(String[] aLine) {
        Short key = Short.valueOf(aLine[GFA_FROM_NODE]);
        short toNode = Short.parseShort(aLine[GFA_TO_NODE]);
        if (edgesMap.containsKey(key)) {
            short[] edges = Arrays.copyOf(edgesMap.get(key), edgesMap.get(key).length + 1);
            edges[edges.length - 1] = toNode;
            edgesMap.put(key, edges);
        } else {
            edgesMap.put(key, new short[] { toNode });
        }
    }
}
