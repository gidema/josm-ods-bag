package org.openstreetmap.josm.plugins.ods.bag.entity.storage;

import java.util.Arrays;
import java.util.List;

import org.openstreetmap.josm.plugins.ods.bag.entity.BagBuildingUnit;
import org.openstreetmap.josm.plugins.ods.entities.storage.AbstractGeoEntityStore;
import org.openstreetmap.josm.plugins.ods.entities.storage.GeoIndex;
import org.openstreetmap.josm.plugins.ods.entities.storage.GeoIndexImpl;
import org.openstreetmap.josm.plugins.ods.entities.storage.Index;
import org.openstreetmap.josm.plugins.ods.entities.storage.PrimaryIndex;
import org.openstreetmap.josm.plugins.ods.entities.storage.UniqueIndexImpl;

public class BagBuildingUnitStore extends AbstractGeoEntityStore<BagBuildingUnit> {
    private final PrimaryIndex<BagBuildingUnit> primaryIndex = new UniqueIndexImpl<>(BagBuildingUnit::getId);
    private final GeoIndex<BagBuildingUnit> geoIndex = new GeoIndexImpl<>(BagBuildingUnit.class, "geometry");
    private final List<Index<BagBuildingUnit>> allIndexes = Arrays.asList(primaryIndex, geoIndex);

    public BagBuildingUnitStore() {
    }

    @Override
    public PrimaryIndex<BagBuildingUnit> getPrimaryIndex() {
        return primaryIndex;
    }

    public PrimaryIndex<BagBuildingUnit> getBuildingUnitIdIndex() {
        return primaryIndex;
    }

    @Override
    public GeoIndex<BagBuildingUnit> getGeoIndex() {
        return geoIndex;
    }

    @Override
    public List<Index<BagBuildingUnit>> getAllIndexes() {
        return allIndexes;
    }
}
