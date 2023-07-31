package org.openstreetmap.josm.plugins.ods.bag.entity.osm;

import org.openstreetmap.josm.plugins.ods.bag.entity.AddressableObjectStatus;
import org.openstreetmap.josm.plugins.ods.entities.OsmEntity;

public interface OsmAddressableObject extends OsmEntity {
    public OsmAddress getMainAddress();
    public AddressableObjectStatus getAddressableStatus();

}
