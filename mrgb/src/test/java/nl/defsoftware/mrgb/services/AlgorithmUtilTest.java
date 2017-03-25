package nl.defsoftware.mrgb.services;

import java.util.Map;

import nl.defsoftware.mrgb.models.Rib;
import nl.defsoftware.mrgb.models.graph.Node;

public class AlgorithmUtilTest {

    public AlgorithmUtilTest() {
        super();
    }

    protected Node findOrCreateNode(Map<Integer, Node> mapping, Integer id) {
        if (!mapping.containsKey(id)) 
            mapping.put(id, new Rib(id));
        return mapping.get(id);
    }

}