package nl.defsoftware.mrgb;

import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author D.L. Ettema
 *
 */
public class Util {

    private static final Logger log = LoggerFactory.getLogger(Util.class);
    
    public static void printMemoryUsage() {
        Runtime runtime = Runtime.getRuntime();
        long bytes = (runtime.totalMemory() - runtime.freeMemory());
        double megabytes = bytes / 1024.0 / 1024.0;
        log.info("memory = " + String.format(Locale.US, "%.3f", megabytes) + " MB");
    }
}
