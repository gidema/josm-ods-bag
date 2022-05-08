package org.openstreetmap.josm.plugins.ods.bag.entity.osm;

import java.util.Collection;

import org.openstreetmap.josm.data.osm.Relation;

public interface AssociatedStreet {
    public Relation getOsmPrimitive();
    public String getName();
    public Collection<OsmBuilding> getBuildings();
    public Collection<OsmAddressNode> getAddressNodes();
    public Collection<OsmStreet> getStreets();
}
