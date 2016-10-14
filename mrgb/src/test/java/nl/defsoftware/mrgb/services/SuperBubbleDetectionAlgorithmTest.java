package nl.defsoftware.mrgb.services;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import nl.defsoftware.mrgb.models.Rib;
import nl.defsoftware.mrgb.models.graph.Bubble;

/**
 * @author D.L. Ettema
 *
 */
public class SuperBubbleDetectionAlgorithmTest {

    List<Rib> n = new ArrayList<>();
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

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        for (int i = 0; i < 15; i++) {
            n.add(new Rib(i+1));
        }

        // building a graph according to the example case in figure 1 of
        // Brankovic et al. 2016
        n.get(v1).addOutEdge(n.get(v2));
        n.get(v1).addOutEdge(n.get(v3));

        n.get(v2).addInEdge(n.get(v1));
        n.get(v2).addOutEdge(n.get(v3));

        n.get(v3).addInEdge(n.get(v1));
        n.get(v3).addInEdge(n.get(v2));
        n.get(v3).addOutEdge(n.get(v4));
        n.get(v3).addOutEdge(n.get(v5));
        n.get(v3).addOutEdge(n.get(v11));

        n.get(v4).addInEdge(n.get(v3));
        n.get(v4).addOutEdge(n.get(v8));

        n.get(v5).addInEdge(n.get(v3));
        n.get(v5).addOutEdge(n.get(v6));
        n.get(v5).addOutEdge(n.get(v9));

        n.get(v6).addInEdge(n.get(v5));
        n.get(v6).addOutEdge(n.get(v7));
        n.get(v6).addOutEdge(n.get(v10));

        n.get(v7).addInEdge(n.get(v6));
        n.get(v7).addInEdge(n.get(v10));
        n.get(v7).addOutEdge(n.get(v8));

        n.get(v8).addInEdge(n.get(v4));
        n.get(v8).addInEdge(n.get(v7));
        n.get(v8).addInEdge(n.get(v12));
        n.get(v8).addOutEdge(n.get(v13));
        n.get(v8).addOutEdge(n.get(v14));

        n.get(v9).addInEdge(n.get(v5));
        n.get(v9).addOutEdge(n.get(v10));

        n.get(v10).addInEdge(n.get(v6));
        n.get(v10).addInEdge(n.get(v9));
        n.get(v10).addOutEdge(n.get(v7));

        n.get(v11).addInEdge(n.get(v3));
        n.get(v11).addOutEdge(n.get(v12));

        n.get(v12).addInEdge(n.get(v11));
        n.get(v12).addOutEdge(n.get(v8));

        n.get(v13).addInEdge(n.get(v8));
        n.get(v13).addOutEdge(n.get(v14));
        n.get(v13).addOutEdge(n.get(v15));

        n.get(v14).addInEdge(n.get(v8));
        n.get(v14).addInEdge(n.get(v13));
        Rib node = n.get(v15);
        n.get(v14).addInEdge(node);

        n.get(v15).addInEdge(n.get(v13));
        n.get(v15).addOutEdge(n.get(v14));
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
        n = null;
    }

    /**
     * Test method for
     * {@link nl.defsoftware.mrgb.services.SuperBubbleDetectionAlgorithm#superBubble(it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap)}.
     */
    @Test
    public void testSuperBubble() {
        assertEquals(15, n.size());
        SuperBubbleDetectionAlgorithm b = new SuperBubbleDetectionAlgorithm();
        b.detectSuperBubbles(n.toArray(new Rib[n.size()]));
        
        List<Bubble> detected = b.getDetectedBubbles();
        System.out.printf("We found %d bubbles", detected.size());
        
        for (Bubble bubble : detected) {
            System.out.print("start: " + bubble.getStart().getNodeId());
            System.out.print(" end: " + bubble.getStop().getNodeId());
        }
    }

}
