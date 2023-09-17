package org.openstreetmap.josm.plugins.ods.bag.entity.storage;

import org.openstreetmap.josm.plugins.ods.bag.entity.OdAddressNode;
import org.openstreetmap.josm.plugins.ods.entities.storage.GeoIndex;
import org.openstreetmap.josm.plugins.ods.entities.storage.GeoIndexImpl;

public class BagAddressNodeStore extends BagEntityStore<OdAddressNode> {
    private final GeoIndex<OdAddressNode> geoIndex = new GeoIndexImpl<>();

    public BagAddressNodeStore() {
        super(OdAddressNode::getAddressId);
    }
}
