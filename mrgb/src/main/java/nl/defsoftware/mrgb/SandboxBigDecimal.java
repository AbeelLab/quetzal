package nl.defsoftware.mrgb;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @author D.L. Ettema
 *
 */
public class SandboxBigDecimal {

    /**
     * @param args
     */
    public static void main(String[] args) {
        SandboxBigDecimal sand = new SandboxBigDecimal();
        sand.run();
        sand.compare();
    }
    
    public void run() {
        double d = 123.456789;
        
        System.out.println(new BigDecimal(d).toPlainString());
        System.out.println(BigDecimal.valueOf(d).toPlainString());
        System.out.println(Double.toString(d));
        System.out.println(new BigDecimal(Double.toString(d)).toPlainString());
    }
    
    public void compare() {
        double d = 123.456789;
        double e = 123.456789;
        BigDecimal bigD = BigDecimal.valueOf(d);
        BigDecimal bigE = BigDecimal.valueOf(e);
        System.out.println("D: " + bigD.toPlainString() + " compare to " + bigE.toPlainString() + " = " + bigD.compareTo(bigE));
        
        BigDecimal scaledD = bigD.setScale(2, RoundingMode.HALF_UP);
        System.out.println("scaled: " + scaledD);
    }

}
