package org.openstreetmap.josm.plugins.ods.bag.relations;

import org.openstreetmap.josm.plugins.ods.domains.buildings.OdAddressNode;
import org.openstreetmap.josm.plugins.ods.domains.buildings.OdBuildingUnit;
import org.openstreetmap.josm.plugins.ods.domains.buildings.impl.OdAddressNodeStore;
import org.openstreetmap.josm.plugins.ods.domains.buildings.impl.OdBuildingUnitStore;


/**
 * <p>Try to find a matching building for every OdBuildingUnit in the buildinUnitStore. The referenceId of the address node will be used to do the matching.</p>
 * <p>If the referenceId is null, or no building with this referenceId was found,
 * this must be an error in the integrity of the opendata object.

 * @author gertjan
 *
 */
public class OdBuildingUnitToAddressNodeBinder implements Runnable {
    private final BuildingUnitToAddressNodeRelation buildingUnitToAddressNodeRelation;
    private final OdBuildingUnitStore buildingUnitStore;
    private final OdAddressNodeStore addressNodeStore;

    public OdBuildingUnitToAddressNodeBinder(
            OdBuildingUnitStore buildingUnitStore,
            OdAddressNodeStore addressNodeStore,
            BuildingUnitToAddressNodeRelation buildingUnitToAddressNodeRelation) {
        super();
        this.buildingUnitToAddressNodeRelation = buildingUnitToAddressNodeRelation;
        this.buildingUnitStore = buildingUnitStore;
        this.addressNodeStore = addressNodeStore;
    }

    @Override
    public void run() {
        for(BuildingUnit_AddressNodePair pair : buildingUnitToAddressNodeRelation) {
            bindBuildingUnitToAddressNode(pair);
        }
    }

    /**
     * Find a matching buildingUnit for an address.
     *
     * @param buildingUnit
     */
    public void bindBuildingUnitToAddressNode(BuildingUnit_AddressNodePair pair) {
        OdBuildingUnit buildingUnit = buildingUnitStore.get(pair.getBuildingUnitId());
        OdAddressNode addressNode = addressNodeStore.get(pair.getAddressNodeId());
        if (buildingUnit != null && addressNode != null) {
            buildingUnit.addSecondaryAddressNode(addressNode);
            addressNode.setBuildinUnit(buildingUnit);
        }
        else {
            // TODO Is in necessary to handle this case?
        }
    }
}
