package nl.defsoftware.mrgb.models.graph;

/**
 * @author D.L. Ettema
 *
 */
public enum NodeType {

    SINGLE_NODE,
    
    INDEL_BUBBLE,
    
    SNP_BUBBLE,
    
    ALLELE_BUBBLE;
    
    public static boolean isSame(NodeType a, NodeType b) {
        return a != null && b != null && a.equals(b);
    }
}
