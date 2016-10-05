package nl.defsoftware.mrgb.fileparsers;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.shorts.Short2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.shorts.ShortSet;
import nl.defsoftware.mrgb.Constants;
import nl.defsoftware.mrgb.models.Rib;

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
    private static final String SEQUENCE = "S";
    private static final String LINK = "L";
    private static final String HEADER = "H";
    private static final String ORI_HEADER = "ORI";

    private static final int GFA_LINE_INDICATOR = 0;
    private static final int GFA_GENOME_NAMES = 1;
    private static final int GFA_FROM_NODE = 1;
    private static final int GFA_SEQUENCE = 2;
    private static final int GFA_TO_NODE = 3;
    private static final int GFA_ORI = 4;
    private static final int GFA_CRD = 5;
    private static final int GFA_CRDCTG = 6;
    private static final int GFA_CTG = 7;
    private static final int GFA_START = 8;
    
    private static final int PREFIX_LENGTH = 2;

    private Map<Integer, int[]> edgesMap = new HashMap<>();
//    private Int2ObjectOpenHashMap<Rib> sequencesMap = new Int2ObjectOpenHashMap<>();
    private Int2ObjectLinkedOpenHashMap<Rib> sequencesMap = new Int2ObjectLinkedOpenHashMap<>();
    
    private Short2ObjectOpenHashMap<String> genomeNamesMap = new Short2ObjectOpenHashMap<>();

    @Override
    public void loadResource() throws UnsupportedEncodingException, FileNotFoundException {
        String dataPath = Constants.PREFIX_PATH.concat(System.getProperties().getProperty(Constants.GRAPH_DATA));
        reader = new BufferedReader(new InputStreamReader(new FileInputStream(dataPath), "UTF-8"));
    }

    public void closeResources() throws IOException {
        reader.close();
    }

    @Override
    public Map<Integer, int[]> getParsedEdges() {
        return edgesMap;
    }
    
    @Override
    public Int2ObjectLinkedOpenHashMap<Rib> getParsedSequences() {
        return sequencesMap;
    }

    @Override
    public Short2ObjectOpenHashMap<String> getParsedGenomeNames() {
        return genomeNamesMap;
    }
    
    @Override
    public boolean isParsed() {
        return sequencesMap.size() > 0 && genomeNamesMap.size() > 0;
    }
    
    @Override
    public void parseData() {
        log.info("Parsing data");
        if (reader == null) {
            try {
                loadResource();
            } catch (Exception ex) {
                log.error("Couldn't load file resources to parse graph data.", ex);
            }
        }
        
        Scanner scanner = new Scanner(reader);
        Pattern pattern = Pattern.compile("\t");
        for (int i = 0; i < 1000 ; i++) { // for testing purposes
//        for (int i = 0; scanner.hasNextLine(); i++) {
                String[] aLine = pattern.split(scanner.nextLine(), 0);
//                if (i < 2 || i > 2800) {
                if (StringUtils.equals(SEQUENCE, aLine[GFA_LINE_INDICATOR])) {
                    processSequence(aLine);
                } else if (StringUtils.equals(LINK, aLine[GFA_LINE_INDICATOR])) {
    //                processEdges(aLine);
                    processEdgesToSequenceMaps(aLine);
                } else if (StringUtils.equals(HEADER, aLine[GFA_LINE_INDICATOR])) {
                    processGenomeNames(aLine[GFA_GENOME_NAMES]);
                }
//            }
        }
        log.info("Finished parsing graph data");
        scanner.close();
    }

    private void processGenomeNames(String aLine) {
        Pattern pattern = Pattern.compile("(:|;)");
        String[] genomeNames = pattern.split(aLine, 0);
        if (genomeNames.length > (int) Short.MAX_VALUE) {
            throw new UnsupportedOperationException("The number of genomes exceed the capacity (" + Short.MAX_VALUE +  ") of this application, please limit the number of genomes. ");
        }
        if (ORI_HEADER.equals(genomeNames[0])) {
            for (int i = PREFIX_LENGTH; i < genomeNames.length; i++) {
                genomeNamesMap.put((short) (i - 1), genomeNames[i]);
            }
        }
    }

    private void processSequence(String[] aLine) {
        Pattern pattern = Pattern.compile("(:|;)");
        for (int i = 0; i < aLine.length; i++) {
            int fromNode = Integer.parseInt(aLine[GFA_FROM_NODE]);
            Rib aSequence = new Rib(fromNode,
                    aLine[GFA_SEQUENCE].toCharArray(), 
                    extractGenomeNames(pattern.split(aLine[GFA_ORI])), 
                    extractReferenceGenome(pattern.split(aLine[GFA_CRD])), 
                    extractGenomeCoordinates(pattern.split(aLine[GFA_START])));
                    
            sequencesMap.put(fromNode, aSequence);
        }
    }

    /**
     * Extracting all the genome name labels we can use to identify each genome sample.
     * @param oriString
     * @return Integer ID and the string name.
     */
    private short[] extractGenomeNames(String[] aLine) {
        short[] genomeIds = new short[aLine.length - PREFIX_LENGTH];
        for (int i = PREFIX_LENGTH; i < aLine.length; i++) {
            genomeIds[i - PREFIX_LENGTH] = getLabelIdForGenomeLabel(aLine[i]);
        }
        return genomeIds;
    }
    
    private short getLabelIdForGenomeLabel(String genomeLabel) {
        ShortSet keys = genomeNamesMap.keySet();
        for (short key : keys) {
            if (genomeNamesMap.get(key).equals(genomeLabel)) return key;
        }
        log.info("A genome label was not in the GFA header line. Please fix problem in GFA file. Will continue with loading, but for accurate analysis please fix problem.");
        return -1;
    }

    private short extractReferenceGenome(String[] aLine) {
        int REFERENCE_NAME_POSITION = 2;
        return getLabelIdForGenomeLabel(aLine[REFERENCE_NAME_POSITION]);
    }
    
    private Integer extractGenomeCoordinates(String[] aLine) {
        int START_COORDINATE_POSITION = 2;
        return Integer.parseInt(aLine[START_COORDINATE_POSITION]);
    }

    private void processEdges(String[] aLine) {
        int key = Integer.parseInt(aLine[GFA_FROM_NODE]);
        int toNode = Integer.parseInt(aLine[GFA_TO_NODE]);
        if (edgesMap.containsKey(key)) {
            int[] edges = Arrays.copyOf(edgesMap.get(key), edgesMap.get(key).length + 1);
            edges[edges.length - 1] = toNode;
            edgesMap.put(key, edges);
        } else {
            edgesMap.put(key, new int[] { toNode });
        }
    }
    
    private void processEdgesToSequenceMaps(String[] aLine) {
        int key = Integer.parseInt(aLine[GFA_FROM_NODE]);
        int toNode = Integer.parseInt(aLine[GFA_TO_NODE]);
        if (sequencesMap.containsKey(key)) {
            Rib rib = sequencesMap.get(key);
            int[] edges = Arrays.copyOf(rib.getConnectedEdges(), rib.getConnectedEdges().length + 1);
            edges[edges.length - 1] = toNode;
            rib.setConnectedEdges(edges);
            sequencesMap.put(key, rib);
        } else {
            log.info("ERROR: This should not happen: processEdgesToSequenceMaps()");//this should not happen.
        }
    }
}
