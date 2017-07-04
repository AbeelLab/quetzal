package nl.defsoftware.mrgb.services;

import java.util.Map;

import nl.defsoftware.mrgb.graphs.models.Node;
import nl.defsoftware.mrgb.graphs.models.Rib;

public class AlgorithmUtilTest {

    public AlgorithmUtilTest() {
        super();
    }

    protected Node findOrCreateNodeInMapping(Map<Integer, Node> mapping, Integer id) {
        if (!mapping.containsKey(id)) 
            mapping.put(id, new Rib(id));
        return mapping.get(id);
    }

}