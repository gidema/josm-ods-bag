package org.openstreetmap.josm.plugins.ods.bag.entity.storage;

import org.openstreetmap.josm.plugins.ods.bag.entity.OdAddressNode;
import org.openstreetmap.josm.plugins.ods.entities.storage.GeoIndex;
import org.openstreetmap.josm.plugins.ods.entities.storage.GeoIndexImpl;

public class BagWithdrawnAddressNodeStore extends BagEntityStore<OdAddressNode> {
    private final GeoIndex<OdAddressNode> geoIndex = new GeoIndexImpl<>();

    public BagWithdrawnAddressNodeStore() {
        super(OdAddressNode::getAddressId);
    }
}
