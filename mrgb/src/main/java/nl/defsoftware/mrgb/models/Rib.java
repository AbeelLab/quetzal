package nl.defsoftware.mrgb.models;

import java.util.List;
import java.util.Map;

/**
 * @author D.L. Ettema
 *
 */
public class Rib {
    
    private Integer nodeId;
    private char[] sequence;
    private Map<Integer, String> genomeNames; //ORI attribute in the GFA file
    private Integer referenceGenomeId; //CRD attribute in the GFA file
    private Integer referenceGenomeCoordinatePosition; //START attribute in the GFA file
    
    private List<String> crdctg; //unknown what to use for
    private List<String> ctg; //unknown what to use for
    
    // Derived set of parameters
    private int amountOfGenomes = 0;
    
    public Rib(Integer nodeId, char[] sequence, Map<Integer, String> genomeNames, Integer referenceGenomeId,
            Integer referenceGenomeCoordinatePosition, List<String> crdctg, List<String> ctg) {
        this.nodeId = nodeId;
        this.sequence = sequence;
        this.genomeNames = genomeNames;
        this.referenceGenomeId = referenceGenomeId;
        this.referenceGenomeCoordinatePosition = referenceGenomeCoordinatePosition;
        this.crdctg = crdctg;
        this.ctg = ctg;
        calculateStats();
    }
    
    private void calculateStats() {
    }
    
    public int amountOfGenomes() {
        return genomeNames.size();
    }
}
