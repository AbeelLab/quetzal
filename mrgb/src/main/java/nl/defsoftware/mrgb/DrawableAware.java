package nl.defsoftware.mrgb;

import nl.defsoftware.mrgb.graphs.models.NodeType;

public @interface DrawableAware {
String name();
NodeType nodeType();
}
