package org.openstreetmap.josm.plugins.ods.bag.entity.impl;

import java.util.ArrayList;
import java.util.List;

import org.locationtech.jts.geom.Point;
import org.openstreetmap.josm.plugins.ods.bag.entity.BagBuilding;
import org.openstreetmap.josm.plugins.ods.bag.entity.OdAddressNode;

public class AddressNodeGroup {
    private final Point geometry;
    private final List<OdAddressNode> addressNodes = new ArrayList<>();
    private final BagBuilding building;

    public AddressNodeGroup(OdAddressNode addressNode) {
        geometry = addressNode.getGeometry();
        addressNodes.add(addressNode);
        building = addressNode.getBuilding();
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

    public BagBuilding getBuilding() {
        return building;
    }
}
