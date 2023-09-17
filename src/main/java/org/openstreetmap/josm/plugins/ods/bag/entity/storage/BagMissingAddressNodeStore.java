package org.openstreetmap.josm.plugins.ods.bag.entity.storage;

import org.openstreetmap.josm.plugins.ods.bag.entity.OdAddressNode;

public class BagMissingAddressNodeStore extends BagEntityStore<OdAddressNode> {
    public BagMissingAddressNodeStore() {
        super(OdAddressNode::getAddressId);
    }
}
