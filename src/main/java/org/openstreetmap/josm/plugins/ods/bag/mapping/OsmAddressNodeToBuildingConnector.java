package org.openstreetmap.josm.plugins.ods.bag.mapping;

import java.util.LinkedList;
import java.util.List;

import org.openstreetmap.josm.plugins.ods.bag.entity.osm.OsmAddressNode;
import org.openstreetmap.josm.plugins.ods.bag.entity.osm.OsmAddressNodeStore;
import org.openstreetmap.josm.plugins.ods.bag.entity.osm.OsmBuilding;
import org.openstreetmap.josm.plugins.ods.bag.entity.osm.OsmBuildingStore;
import org.openstreetmap.josm.plugins.ods.context.OdsContext;
import org.openstreetmap.josm.plugins.ods.context.OdsContextJob;
import org.openstreetmap.josm.plugins.ods.entities.storage.GeoIndex;

/**
 * <p>Try to find a matching building for every OdAddressNode passed to the OdAddressNode
 * consumer. The geometry of the OdAddressNode will be used to do the matching</p>
 * <p>If a match is found, the building parameter of the addressNode will be set to the related address
 * and the addressNode will be added to the related addresses list of the building.</p>
 * <p>If no matching building was found, The unmatched addressNode will
 * be forwarded to the unmatchedAddressNodeConsumer if available;
 *
 * @author gertjan
 *
 */
public class OsmAddressNodeToBuildingConnector implements OdsContextJob {

    public OsmAddressNodeToBuildingConnector() {
        super();
    }

    @Override
    public void run(OdsContext context) {
        OsmAddressNodeStore addressNodeStore = context.getComponent(OsmAddressNodeStore.class);
        OsmBuildingStore buildingStore = context.getComponent(OsmBuildingStore.class);
        // We can't use forEach here, because of concurrent modification exceptions
        for (OsmAddressNode an : addressNodeStore) {
            match(an, buildingStore);
        }
    }

    /**
     * Find a matching building for an address.
     * TODO use the geometry index to find the building
     *
     * @param addressNode
     */
    private static void match(OsmAddressNode addressNode, OsmBuildingStore buildingStore) {
        GeoIndex<OsmBuilding> geoIndex = buildingStore.getGeoIndex();
        if (addressNode.getBuilding() == null) {
            List<OsmBuilding> buildings = geoIndex.intersection(addressNode.getGeometry());
            if (buildings.size() == 0) {
                //                reportUnmatched(addressNode);
                return;
            }
            if (buildings.size() == 1) {
                OsmBuilding building = buildings.get(0);
                addressNode.setBuilding(building);
                building.getAddressNodes().add(addressNode);
                return;
            }
            List<OsmBuilding> bagBuildings = new LinkedList<>();
            List<OsmBuilding> otherBuildings = new LinkedList<>();
            for (OsmBuilding building : buildings) {
                if (building.getBuildingId() != null) {
                    bagBuildings.add(building);
                }
                else {
                    otherBuildings.add(building);
                }
            }
            if (bagBuildings.size() == 1) {
                OsmBuilding building = bagBuildings.get(0);
                addressNode.setBuilding(building);
                building.getAddressNodes().add(addressNode);
                return;
            }
            // TODO report duplicateBuildings
        }
    }
}
