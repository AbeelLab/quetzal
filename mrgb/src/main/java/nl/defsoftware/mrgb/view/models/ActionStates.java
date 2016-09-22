/**
 * 
 */
package nl.defsoftware.mrgb.view.models;

import java.util.HashMap;
import java.util.Map;

import com.sun.javafx.collections.ObservableMapWrapper;

/**
 * @author D.L. Ettema
 *
 */
public class ActionStates<K,V> extends ObservableMapWrapper<K,V> {

    public ActionStates() {
        super(new HashMap<K,V>());
    }

//    private ObservableMap<K,V> states = new ObservableMapWrapper<K,V>(new HashMap<K,V>());
    
    public void setState(K stateType, V newState) {
        put(stateType, newState);
    }
    
    public void setStates(Map<K, V> newStates) {
        for (Entry<K, V> newState : newStates.entrySet()) {
            put(newState.getKey(), newState.getValue());
        }
    }
    
    public Map<K, V> getStates() {
        return this;
    }
}
