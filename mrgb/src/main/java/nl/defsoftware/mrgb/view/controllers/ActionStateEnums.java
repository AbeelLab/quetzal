package nl.defsoftware.mrgb.view.controllers;

/**
 * @author D.L. Ettema
 * @date 20 September 2016
 *
 */
public enum ActionStateEnums {

    
    /* Action stating loading files adata needs to be loaded into the display since maybe a view or filters have changed */
    LOAD_DATA_AND_PARSE,
    
    /* Action to reload data on the viewscreen */
    RELOAD_DATA, 
    
    /* View the data in the main screen */
    VIEW_GRAPH;
}
