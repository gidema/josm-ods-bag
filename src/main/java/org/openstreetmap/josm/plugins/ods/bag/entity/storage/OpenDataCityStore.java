package org.openstreetmap.josm.plugins.ods.bag.entity.storage;

import java.util.Arrays;
import java.util.List;

import org.openstreetmap.josm.plugins.ods.bag.entity.impl.BagCity;
import org.openstreetmap.josm.plugins.ods.entities.storage.AbstractGeoEntityStore;
import org.openstreetmap.josm.plugins.ods.entities.storage.GeoIndex;
import org.openstreetmap.josm.plugins.ods.entities.storage.GeoIndexImpl;
import org.openstreetmap.josm.plugins.ods.entities.storage.Index;
import org.openstreetmap.josm.plugins.ods.entities.storage.PrimaryIndex;
import org.openstreetmap.josm.plugins.ods.entities.storage.UniqueIndexImpl;

public class OpenDataCityStore extends AbstractGeoEntityStore<BagCity> {
    private final PrimaryIndex<BagCity> primaryIndex = new UniqueIndexImpl<>(BagCity::getCityId);
    private final GeoIndex<BagCity> geoIndex = new GeoIndexImpl<>(BagCity.class, "geometry");
    private final List<Index<BagCity>> allIndexes = Arrays.asList(primaryIndex, geoIndex);

    public OpenDataCityStore() {
        super();
    }

    @Override
    public PrimaryIndex<BagCity> getPrimaryIndex() {
        return primaryIndex;
    }

    public PrimaryIndex<BagCity> getCityIdIndex() {
        return primaryIndex;
    }

    @Override
    public GeoIndex<BagCity> getGeoIndex() {
        return geoIndex;
    }

    @Override
    public List<Index<BagCity>> getAllIndexes() {
        return allIndexes;
    }
}
