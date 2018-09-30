package org.openstreetmap.josm.plugins.ods.bag.relations;

import org.openstreetmap.josm.plugins.ods.domains.buildings.OdBuilding;
import org.openstreetmap.josm.plugins.ods.domains.buildings.OdBuildingUnit;
import org.openstreetmap.josm.plugins.ods.domains.buildings.impl.OdBuildingStore;
import org.openstreetmap.josm.plugins.ods.domains.buildings.impl.OdBuildingUnitStore;
import org.openstreetmap.josm.tools.Logging;


/**
 * <p>Try to find a matching building for every OdBuildingUnit in the buildinUnitStore. The referenceId of the address node will be used to do the matching.</p>
 * <p>If the referenceId is null, or no building with this referenceId was found,
 * this must be an error in the integrity of the opendata object.

 * @author gertjan
 *
 */
public class OdBuildingUnitToBuildingBinder implements Runnable {
    private final BuildingToBuildingUnitRelation buildingUnitToBuildingRelation;
    private final OdBuildingStore buildingStore;
    private final OdBuildingUnitStore buildingUnitStore;

    public OdBuildingUnitToBuildingBinder(
            OdBuildingStore buildingStore,
            OdBuildingUnitStore buildingUnitStore,
            BuildingToBuildingUnitRelation buildingUnitToBuildingRelation) {
        super();
        this.buildingUnitToBuildingRelation = buildingUnitToBuildingRelation;
        this.buildingStore = buildingStore;
        this.buildingUnitStore = buildingUnitStore;
    }

    @Override
    public void run() {
        for(BuildingToBuildingUnitRelation.Tuple tuple : buildingUnitToBuildingRelation) {
            bindBuildingUnitToBuilding(tuple);
        }
    }

    /**
     * Find a matching building for an address.
     *
     * @param buildingUnit
     */
    public void bindBuildingUnitToBuilding(BuildingToBuildingUnitRelation.Tuple tuple) {
        OdBuildingUnit unit = buildingUnitStore.get(tuple.getBuildingUnitId());
        OdBuilding building = buildingStore.get(tuple.getBuildingId());
        if (building == null) Logging.warn("BuildingUnit connected to missing building: {0}",
                tuple.getBuildingId());
        if (unit != null && building != null) {
            unit.addBuilding(building);
            building.addBuildingUnit(unit);
        }
    }
}
