package nl.defsoftware.mrgb.services;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Pattern;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.defsoftware.mrgb.models.Rib;
import nl.defsoftware.mrgb.models.graph.Bubble;

/**
 * @author D.L. Ettema
 *
 */
public class SuperBubbleDetectionAlgorithmTest {

    private static final Logger log = LoggerFactory.getLogger(SuperBubbleDetectionAlgorithmTest.class);
    
    private List<Rib> graph15n = new ArrayList<>();
    private Map<Integer,Rib> graph8n = new HashMap<>();
    private  Map<Integer,Rib> graph4n = new HashMap<>();
    List<Rib> expectedOrdering = new ArrayList<>();
    int v1 = 0;
    int v2 = 1;
    int v3 = 2;
    int v4 = 3;
    int v5 = 4;
    int v6 = 5;
    int v7 = 6;
    int v8 = 7;
    int v9 = 8;
    int v10 = 9;
    int v11 = 10;
    int v12 = 11;
    int v13 = 12;
    int v14 = 13;
    int v15 = 14;

    @Before
    public void setUp() throws Exception {
        setupPaperTestGraph();
        setupTestCase8Nodes();
        setupTestCase4Nodes();
    }

    private void setupTestCase8Nodes() throws Exception{
        File sample8nFile = new File("src/test/resources/data/sample.txt");
        loadTestSampleGraphs(sample8nFile, graph8n);
    }
    
    private void setupTestCase4Nodes() throws Exception {
        File file = new File("src/test/resources/data/sample2.txt");
        loadTestSampleGraphs(file, graph4n);
    }

    private void loadTestSampleGraphs(File sampleFile, Map<Integer, Rib> graph) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(sampleFile), "UTF-8"));
        Scanner scanner = new Scanner(reader);
        Pattern pattern = Pattern.compile(" ");
        String[] aLine = pattern.split(scanner.nextLine(), 0);
        while (scanner.hasNextLine()) {
            aLine = pattern.split(scanner.nextLine(), 0);
            Rib from = findOrCreateRib(graph, Integer.parseInt(aLine[0]));
            Rib to = findOrCreateRib(graph, Integer.parseInt(aLine[1]));
            from.addOutEdge(to);
            to.addInEdge(from);
        }
        scanner.close();
        reader.close();
    }

    private Rib findOrCreateRib(Map<Integer, Rib> graph, Integer id) {
        if (!graph.containsKey(id)) 
            graph.put(id, new Rib(id));
        return graph.get(id);
    }
    /**
     * This graph is the example presented in the paper.
     */
    private void setupPaperTestGraph() {
        for (int i = 0; i < 15; i++) {
            graph15n.add(new Rib(i+1));
        }

        // building a graph according to the example case in figure 1 of
        // Brankovic et al. 2016
        graph15n.get(v1).addOutEdge(graph15n.get(v2));
        graph15n.get(v1).addOutEdge(graph15n.get(v3));

        graph15n.get(v2).addInEdge(graph15n.get(v1));
        graph15n.get(v2).addOutEdge(graph15n.get(v3));

        graph15n.get(v3).addInEdge(graph15n.get(v1));
        graph15n.get(v3).addInEdge(graph15n.get(v2));
        graph15n.get(v3).addOutEdge(graph15n.get(v4));
        graph15n.get(v3).addOutEdge(graph15n.get(v5));
        graph15n.get(v3).addOutEdge(graph15n.get(v11));

        graph15n.get(v4).addInEdge(graph15n.get(v3));
        graph15n.get(v4).addOutEdge(graph15n.get(v8));

        graph15n.get(v5).addInEdge(graph15n.get(v3));
        graph15n.get(v5).addOutEdge(graph15n.get(v6));
        graph15n.get(v5).addOutEdge(graph15n.get(v9));

        graph15n.get(v6).addInEdge(graph15n.get(v5));
        graph15n.get(v6).addOutEdge(graph15n.get(v7));
        graph15n.get(v6).addOutEdge(graph15n.get(v10));

        graph15n.get(v7).addInEdge(graph15n.get(v6));
        graph15n.get(v7).addInEdge(graph15n.get(v10));
        graph15n.get(v7).addOutEdge(graph15n.get(v8));

        graph15n.get(v8).addInEdge(graph15n.get(v4));
        graph15n.get(v8).addInEdge(graph15n.get(v7));
        graph15n.get(v8).addInEdge(graph15n.get(v12));
        graph15n.get(v8).addOutEdge(graph15n.get(v13));
        graph15n.get(v8).addOutEdge(graph15n.get(v14));

        graph15n.get(v9).addInEdge(graph15n.get(v5));
        graph15n.get(v9).addOutEdge(graph15n.get(v10));

        graph15n.get(v10).addInEdge(graph15n.get(v6));
        graph15n.get(v10).addInEdge(graph15n.get(v9));
        graph15n.get(v10).addOutEdge(graph15n.get(v7));

        graph15n.get(v11).addInEdge(graph15n.get(v3));
        graph15n.get(v11).addOutEdge(graph15n.get(v12));

        graph15n.get(v12).addInEdge(graph15n.get(v11));
        graph15n.get(v12).addOutEdge(graph15n.get(v8));

        graph15n.get(v13).addInEdge(graph15n.get(v8));
        graph15n.get(v13).addOutEdge(graph15n.get(v14));
        graph15n.get(v13).addOutEdge(graph15n.get(v15));

        graph15n.get(v14).addInEdge(graph15n.get(v8));
        graph15n.get(v14).addInEdge(graph15n.get(v13));
        graph15n.get(v14).addInEdge(graph15n.get(v15));

        graph15n.get(v15).addInEdge(graph15n.get(v13));
        graph15n.get(v15).addOutEdge(graph15n.get(v14));
        
        
        expectedOrdering.add(graph15n.get(v1));
        expectedOrdering.add(graph15n.get(v2));
        expectedOrdering.add(graph15n.get(v3));
        expectedOrdering.add(graph15n.get(v11));
        expectedOrdering.add(graph15n.get(v12));
        expectedOrdering.add(graph15n.get(v5));
        expectedOrdering.add(graph15n.get(v9));
        expectedOrdering.add(graph15n.get(v6));
        expectedOrdering.add(graph15n.get(v10));
        expectedOrdering.add(graph15n.get(v7));
        expectedOrdering.add(graph15n.get(v4));
        expectedOrdering.add(graph15n.get(v8));
        expectedOrdering.add(graph15n.get(v13));
        expectedOrdering.add(graph15n.get(v15));
        expectedOrdering.add(graph15n.get(v14));
    }

    @After
    public void tearDown() throws Exception {
        graph15n = null;
        graph8n = null;
        graph4n = null;
    }
    
    @Test
    public void testgraph8n() {
        SuperBubbleDetectionAlgorithm b = new SuperBubbleDetectionAlgorithm();
        b.detectSuperBubbles(graph8n.values().toArray(new Rib[graph8n.size()]));
        
        List<Bubble> detected = b.getDetectedBubbles();
        log.info("8n: We found {} bubbles.", detected.size());
        
        for (Bubble bubble : detected) {
            log.info("start: " + bubble.getStart().getNodeId());
            log.info(" end: " + bubble.getStop().getNodeId());
        }
    }
    
    @Test
    public void testgraph4n() {
        SuperBubbleDetectionAlgorithm b = new SuperBubbleDetectionAlgorithm();
        b.detectSuperBubbles(graph4n.values().toArray(new Rib[graph4n.size()]));
        
        List<Bubble> detected = b.getDetectedBubbles();
        assertEquals("We should have detected 1 bubble but found "+ detected.size(), 1, detected.size());
        
        for (Bubble bubble : detected) {
            log.info("start: " + bubble.getStart().getNodeId());
            log.info(" end: " + bubble.getStop().getNodeId());
        }
    }
    
    /**
     * Test method for
     * {@link nl.defsoftware.mrgb.services.SuperBubbleDetectionAlgorithm#superBubble(it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap)}.
     */
    @Test
    public void testPaperSuperBubble() {
        assertEquals(15, graph15n.size());
        SuperBubbleDetectionAlgorithm b = new SuperBubbleDetectionAlgorithm();
        b.detectSuperBubbles(graph15n.toArray(new Rib[graph15n.size()]));
        
        List<Bubble> detected = b.getDetectedBubbles();
        log.info("15n: We found {} bubbles\n", detected.size());
        
        for (Bubble bubble : detected) {
            log.info("start: " + bubble.getStart().getNodeId());
            log.info(" end: " + bubble.getStop().getNodeId());
        }
    }
    
    @Test
    public void testTopologicalOrdering() {
        List<Rib> actualOrdering = SuperBubbleDetectionHelper.topologicalSort(graph15n.toArray(new Rib[graph15n.size()]));
        assertEquals("Size of ordering does not match", expectedOrdering.size(), actualOrdering.size());
        for (int i = 0; i < actualOrdering.size(); i++) {
            assertEquals("Node not in order as expected.", expectedOrdering.get(i).getNodeId(), actualOrdering.get(i).getNodeId());
        }
    }

}
