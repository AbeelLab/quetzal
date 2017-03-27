package nl.defsoftware.mrgb.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap;
import nl.defsoftware.mrgb.model.GraphTestContainer;
import nl.defsoftware.mrgb.models.graph.Graph;
import nl.defsoftware.mrgb.models.graph.Node;
import nl.defsoftware.mrgb.models.graph.NodeType;

public class LongestPathAlgorithmTest extends AlgorithmUtilTest {

    private LongestPathAlgorithm lpAlgorithm = new LongestPathAlgorithm();
    private GraphTestContainer graphTester = new GraphTestContainer();
    private static String COMMENT_LINE = "--";

    @Before
    public void setUp() throws Exception {
    }

    private void loadTestSampleGraphs(File sampleFile) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(sampleFile), "UTF-8"));
        Scanner scanner = new Scanner(reader);
        Pattern pattern = Pattern.compile(" ");

        Int2ObjectLinkedOpenHashMap<Node> sequencesDataMap = new Int2ObjectLinkedOpenHashMap<>();

        while (scanner.hasNextLine()) {
            String[] aLine = pattern.split(scanner.nextLine(), 0);
            switch (aLine[0]) {
            case "S":
                graphTester.setSourceNodeId(Integer.parseInt(aLine[1]));
                break;
            case "T":
                graphTester.setTargetNodeId(Integer.parseInt(aLine[1]));
                break;
            case "N":
                graphTester.setNumberOfHopsInLongestPath(Integer.parseInt(aLine[1]));
                break;
            case "P":
                readPath(aLine);
                break;
            case "L":
                createNodesAndEdges(sequencesDataMap, aLine);
                break;
            case "--": // comment line
                break;
            default:
                break;
            }
        }
        // sanity check
        assertEquals("Sanity check sourcenode", graphTester.getSourceNodeId(),
                sequencesDataMap.get(graphTester.getSourceNodeId()).getNodeId());
        assertEquals("Sanity check sourcenode type", NodeType.SINGLE_NODE,
                sequencesDataMap.get(graphTester.getSourceNodeId()).getNodeType());

        assertEquals("Sanity check targetnode", graphTester.getTargetNodeId(),
                sequencesDataMap.get(graphTester.getTargetNodeId()).getNodeId());
        assertEquals("Sanity check targetnode type", NodeType.SINGLE_NODE,
                sequencesDataMap.get(graphTester.getTargetNodeId()).getNodeType());
        // Creating the graph
        graphTester.setGraph(new Graph(null, sequencesDataMap, sequencesDataMap.get(graphTester.getSourceNodeId()),
                sequencesDataMap.get(graphTester.getTargetNodeId())));
        scanner.close();
        reader.close();
    }

    /**
     * @param sequencesDataMap
     * @param aLine
     */
    private void createNodesAndEdges(Int2ObjectLinkedOpenHashMap<Node> sequencesDataMap, String[] aLine) {
        Node from = super.findOrCreateNode(sequencesDataMap, Integer.parseInt(aLine[1]));
        Node to = super.findOrCreateNode(sequencesDataMap, Integer.parseInt(aLine[2]));
        from.addOutEdge(to);
        to.addInEdge(from);
    }

    /**
     * @param aLine
     */
    private void readPath(String[] aLine) {
        List<Integer> pathList = new ArrayList<>();
        for (int i = 1; i < aLine.length; i++) {
            if (aLine[i].startsWith(COMMENT_LINE)) {
                break;
            }
            pathList.add(Integer.parseInt(aLine[i]));
        }
        int pathNumber = graphTester.getPossibleLongestPaths().size() + 1;
        graphTester.getPossibleLongestPaths().put(pathNumber, pathList);
    }

    @After
    public void tearDown() throws Exception {
    }

    // @Test
    public void testFindLongestPath() {
        fail("Not yet implemented");
    }

    @Test
    public void testFindLongestPathBFS_SampleDistance1() throws IOException {
        loadTestSampleGraphs(new File("src/test/resources/data/longestpath_sample_1.txt"));

        int numberOfSteps = lpAlgorithm.findLongestPathBFS(graphTester.getGraph().getMap(),
                graphTester.getGraph().getSource().getNodeId(), graphTester.getGraph().getSink().getNodeId());

        assertEquals("Distance did not match", graphTester.getNumberOfHopsInLongestPath(), numberOfSteps);
    }

    @Test
    public void testFindLongestPathBFS_SampleDistance2() throws IOException {
        loadTestSampleGraphs(new File("src/test/resources/data/longestpath_sample_2.txt"));

        int numberOfSteps = lpAlgorithm.findLongestPathBFS(graphTester.getGraph().getMap(),
                graphTester.getGraph().getSource().getNodeId(), graphTester.getGraph().getSink().getNodeId());

        assertEquals("Distance did not match", graphTester.getNumberOfHopsInLongestPath(), numberOfSteps);
    }

    @Test
    public void testFindLongestPathBFS_SampleDistance3() throws IOException {
        loadTestSampleGraphs(new File("src/test/resources/data/longestpath_sample_3.txt"));

        int numberOfSteps = lpAlgorithm.findLongestPathBFS(graphTester.getGraph().getMap(),
                graphTester.getGraph().getSource().getNodeId(), graphTester.getGraph().getSink().getNodeId());

        assertEquals("Distance did not match", graphTester.getNumberOfHopsInLongestPath(), numberOfSteps);
    }

    @Test
    public void testFindLongestPathBFS_SampleDistance3a() throws IOException {
        loadTestSampleGraphs(new File("src/test/resources/data/longestpath_sample_3a.txt"));

        int numberOfSteps = lpAlgorithm.findLongestPathBFS(graphTester.getGraph().getMap(),
                graphTester.getGraph().getSource().getNodeId(), graphTester.getGraph().getSink().getNodeId());

        assertEquals("Distance did not match", graphTester.getNumberOfHopsInLongestPath(), numberOfSteps);
    }

    @Test
    public void testFindLongestPathBFS_SampleDistance3b() throws IOException {
        loadTestSampleGraphs(new File("src/test/resources/data/longestpath_sample_3b.txt"));

        int numberOfSteps = lpAlgorithm.findLongestPathBFS(graphTester.getGraph().getMap(),
                graphTester.getGraph().getSource().getNodeId(), graphTester.getGraph().getSink().getNodeId());

        assertEquals("Distance did not match", graphTester.getNumberOfHopsInLongestPath(), numberOfSteps);
    }
    
    @Test
    public void testFindLongestPathBFS_SampleDistance4() throws IOException {
        loadTestSampleGraphs(new File("src/test/resources/data/longestpath_sample_4.txt"));

        int numberOfSteps = lpAlgorithm.findLongestPathBFS(graphTester.getGraph().getMap(),
                graphTester.getGraph().getSource().getNodeId(), graphTester.getGraph().getSink().getNodeId());

        assertEquals("Distance did not match", graphTester.getNumberOfHopsInLongestPath(), numberOfSteps);
    }

}
