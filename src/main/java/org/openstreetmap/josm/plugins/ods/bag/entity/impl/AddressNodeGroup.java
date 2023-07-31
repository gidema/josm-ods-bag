package org.openstreetmap.josm.plugins.ods.bag.entity.impl;

import java.util.ArrayList;
import java.util.List;

import org.locationtech.jts.geom.Point;
import org.openstreetmap.josm.plugins.ods.bag.entity.BagAddressableObject;
import org.openstreetmap.josm.plugins.ods.bag.entity.OdAddressNode;

public class AddressNodeGroup {
    private final Point geometry;
    private final List<OdAddressNode> addressNodes = new ArrayList<>();
    private final BagAddressableObject addressableObject;

    public AddressNodeGroup(OdAddressNode addressNode) {
        geometry = addressNode.getGeometry();
        addressNodes.add(addressNode);
        addressableObject = addressNode.getAddressableObject();
    }

    public void addAddressNode(OdAddressNode node) {
        addressNodes.add(node);
    }

    public List<OdAddressNode> getAddressNodes() {
        return addressNodes;
    }

    public Point getGeometry() {
        return geometry;
    }

    public BagAddressableObject getAddressableObject() {
        return addressableObject;
    }
}
