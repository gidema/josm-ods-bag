package org.openstreetmap.josm.plugins.ods.bag.relations;

import org.openstreetmap.josm.plugins.ods.domains.buildings.OdBuilding;
import org.openstreetmap.josm.plugins.ods.domains.buildings.OdBuildingUnit;
import org.openstreetmap.josm.plugins.ods.domains.buildings.impl.OdBuildingStore;
import org.openstreetmap.josm.plugins.ods.domains.buildings.impl.OdBuildingUnitStore;


/**
 * <p>Try to find a matching building for every OdBuildingUnit in the buildinUnitStore. The referenceId of the address node will be used to do the matching.</p>
 * <p>If the referenceId is null, or no building with this referenceId was found,
 * this must be an error in the integrity of the opendata object.

 * @author gertjan
 *
 */
public class OdBuildingUnitToBuildingBinder {
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

    public void run() {
        for(Building_BuildingUnitPair pair : buildingUnitToBuildingRelation) {
            bindBuildingUnitToBuilding(pair);
        }
    }

    /**
     * Find a matching building for an address.
     *
     * @param buildingUnit
     */
    public void bindBuildingUnitToBuilding(Building_BuildingUnitPair pair) {
        OdBuildingUnit unit = buildingUnitStore.get(pair.getBuildingUnitId());
        OdBuilding building = buildingStore.get(pair.getBuildingId());
        if (unit != null && building != null) {
            unit.addBuilding(building);
            building.addBuildingUnit(unit);
        }
        else {
            // TODO Is in necessary to handle this case?
        }
    }
}
