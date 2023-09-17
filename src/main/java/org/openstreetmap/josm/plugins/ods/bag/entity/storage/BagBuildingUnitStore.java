package org.openstreetmap.josm.plugins.ods.bag.entity.storage;

import org.openstreetmap.josm.plugins.ods.bag.entity.BagBuildingUnit;
import org.openstreetmap.josm.plugins.ods.entities.storage.GeoIndex;
import org.openstreetmap.josm.plugins.ods.entities.storage.GeoIndexImpl;

public class BagBuildingUnitStore extends BagEntityStore<BagBuildingUnit> {
    private final GeoIndex<BagBuildingUnit> geoIndex = new GeoIndexImpl<>();

    public BagBuildingUnitStore() {
        super(BagBuildingUnit::getId);
    }

//    public Map<Long, BagBuildingUnit> getPrimaryIndex() {
//        // TODO Auto-generated method stub
//        return null;
//    }
}
