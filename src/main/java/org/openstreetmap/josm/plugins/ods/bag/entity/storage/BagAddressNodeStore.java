package org.openstreetmap.josm.plugins.ods.bag.entity.storage;

import java.util.Arrays;
import java.util.List;

import org.openstreetmap.josm.plugins.ods.bag.entity.OdAddressNode;
import org.openstreetmap.josm.plugins.ods.entities.storage.AbstractGeoEntityStore;
import org.openstreetmap.josm.plugins.ods.entities.storage.GeoIndex;
import org.openstreetmap.josm.plugins.ods.entities.storage.GeoIndexImpl;
import org.openstreetmap.josm.plugins.ods.entities.storage.Index;
import org.openstreetmap.josm.plugins.ods.entities.storage.PrimaryIndex;
import org.openstreetmap.josm.plugins.ods.entities.storage.UniqueIndexImpl;

public class BagAddressNodeStore extends AbstractGeoEntityStore<OdAddressNode> {
    private final PrimaryIndex<OdAddressNode> primaryIndex = new UniqueIndexImpl<>(OdAddressNode::getAddressId);
    private final GeoIndex<OdAddressNode> geoIndex = new GeoIndexImpl<>(OdAddressNode.class, "geometry");
    private final List<Index<OdAddressNode>> allIndexes = Arrays.asList(primaryIndex, geoIndex);

    public BagAddressNodeStore() {
    }

    @Override
    public PrimaryIndex<OdAddressNode> getPrimaryIndex() {
        return primaryIndex;
    }

    @Override
    public GeoIndex<OdAddressNode> getGeoIndex() {
        return geoIndex;
    }

    @Override
    public List<Index<OdAddressNode>> getAllIndexes() {
        return allIndexes;
    }
}
