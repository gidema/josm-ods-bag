package org.openstreetmap.josm.plugins.ods.bag.enrichment;

import java.util.function.Consumer;

import org.openstreetmap.josm.plugins.ods.bag.entity.BagBuilding;
import org.openstreetmap.josm.plugins.ods.bag.entity.storage.BagBuildingStore;


/**
 * This tasks verifies if there are adjacent buildings in
 * the down loaded data.
 *
 * TODO consider running over all buildings, not just the new ones.
 *
 * @author gertjan
 *
 */
public class BuildingNeighboursEnricher implements Consumer<BagBuilding> {
    private final BagBuildingStore buildingStore;

    public BuildingNeighboursEnricher(BagBuildingStore buildingStore) {
        super();
        this.buildingStore = buildingStore;
    }

    @Override
    public void accept(BagBuilding building) {
        // TODO consider using a buffer around the building
        for (BagBuilding candidate : buildingStore.getGeoIndex().intersection(building.getGeometry())) {
            if (candidate == building) continue;
            if (building.getNeighbours().contains(candidate)) continue;
            building.getNeighbours().add(candidate);
            candidate.getNeighbours().add(building);
        }
    }
}
