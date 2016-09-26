package nl.defsoftware.mrgb.models;

/**
 * @author D.L. Ettema
 *
 */
public class Rib {
    
    private Integer nodeId;
    private char[] sequence;
    private short[] genomeIds; //ORI attribute in the GFA file
    private short referenceGenomeId; //CRD attribute in the GFA file
    private Integer referenceGenomeCoordinates; //START attribute in the GFA file
    
//    private List<String> crdctg; //unknown what to use for. Not yet implemented
//    private List<String> ctg; //unknown what to use for. Not yet implemented
    
    // Derived set of parameters
    private int amountOfGenomes = 0;
    
    public Rib(Integer nodeId, char[] sequence, short[] genomeIds, short referenceGenomeId,
            Integer referenceGenomeCoordinates) {
        this.nodeId = nodeId;
        this.sequence = sequence;
        this.genomeIds = genomeIds;
        this.referenceGenomeId = referenceGenomeId;
        this.referenceGenomeCoordinates = referenceGenomeCoordinates;
    }
    
    public int amountOfGenomes() {
        return genomeIds.length;
    }
}
