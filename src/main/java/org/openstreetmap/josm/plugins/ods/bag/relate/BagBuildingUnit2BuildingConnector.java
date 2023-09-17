package org.openstreetmap.josm.plugins.ods.bag.relate;

import java.util.function.Consumer;

import org.openstreetmap.josm.plugins.ods.bag.entity.BagBuilding;
import org.openstreetmap.josm.plugins.ods.bag.entity.BagBuildingUnit;
import org.openstreetmap.josm.plugins.ods.bag.entity.OdAddressNode;
import org.openstreetmap.josm.plugins.ods.bag.entity.storage.BagBuildingUnit2BuildingPairStore;
import org.openstreetmap.josm.plugins.ods.bag.entity.storage.BagBuildingUnitStore;
import org.openstreetmap.josm.plugins.ods.bag.entity.storage.BagBuildingStore;
import org.openstreetmap.josm.plugins.ods.context.OdsContext;
import org.openstreetmap.josm.plugins.ods.context.OdsContextJob;


/**
 * <p>Try to find a matching building for every BagBuildingUnit
 * consumer. The referenceId of the address node will be used to do the matching.</p>
 * <p>If a match is found, the building parameter of the addressNode will be set to the related address
 * and the addressNode will be added to the related addresses list of the building.</p>
 * <p>If the referenceId is null, or no building with this referenceId was found,
 * this must be an error in the integrity of the opendata object. The faulty addressNode will
 * be forwarded to the unmatchedAddressNode consumer if available;
 *
 * @author gertjan
 *
 */
public class BagBuildingUnit2BuildingConnector implements OdsContextJob {
    private Consumer<OdAddressNode> unmatchedAddressNodeHandler;

    public BagBuildingUnit2BuildingConnector() {
        super();
    }

    @Override
    public void run(OdsContext context) {
        BagBuildingStore buildingStore = context.getComponent(BagBuildingStore.class);
        BagBuildingUnitStore buildingUnitStore = context.getComponent(BagBuildingUnitStore.class);
        BagBuildingUnit2BuildingPairStore buildingUnit2BuildingPairStore = context.getComponent(BagBuildingUnit2BuildingPairStore.class);
        buildingUnit2BuildingPairStore.forEach(pair -> {
            Long buildingUnitId = pair.getBuildingUnitId();
            Long buildingId = pair.getBuildingId();
            BagBuildingUnit buildingUnit = buildingUnitStore.get(buildingUnitId);
            BagBuilding building = buildingStore.get(buildingId);
            if (building != null && buildingUnit != null) {
                buildingUnit.setBuilding(building);
                building.getBuildingUnits().put(buildingUnitId, buildingUnit);
            };
        });
    }
    /**
     * Find a matching building for an address.
     *
     * @param addressNode
     */
//    public void matchAddressToBuilding(OdAddressNode addressNode) {
//        OpenDataBuildingStore buildings = context.getComponent(OpenDataBuildingStore.class);
//        if (addressNode.getBuilding() == null) {
//            Object buildingRef = addressNode.getBuildingRef();
//            if (buildingRef != null) {
//                List<BagBuilding> matchedbuildings = buildings.getById(buildingRef);
//                if (matchedbuildings.size() == 1) {
//                    BagBuilding building = matchedbuildings.get(0);
//                    addressNode.setBuilding(building);
//                    building.getAddressNodes().add(addressNode);
//                }
//                else {
//                    reportUnmatched(addressNode);
//                }
//            }
//            else {
//                reportUnmatched(addressNode);
//            }
//        }
//    }

    private void reportUnmatched(OdAddressNode addressNode) {
        if (unmatchedAddressNodeHandler != null) {
            unmatchedAddressNodeHandler.accept(addressNode);
        }
    }
}
