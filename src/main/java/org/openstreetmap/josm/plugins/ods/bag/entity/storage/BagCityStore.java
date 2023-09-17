package org.openstreetmap.josm.plugins.ods.bag.entity.storage;

import org.openstreetmap.josm.plugins.ods.bag.entity.impl.BagCity;
import org.openstreetmap.josm.plugins.ods.entities.storage.GeoIndex;
import org.openstreetmap.josm.plugins.ods.entities.storage.GeoIndexImpl;

public class BagCityStore extends BagEntityStore<BagCity> {
    private final GeoIndex<BagCity> geoIndex = new GeoIndexImpl<>();

    public BagCityStore() {
        super(BagCity::getCityId);
    }
}
