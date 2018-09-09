package org.openstreetmap.josm.plugins.ods.bag.osm.build;

import java.util.Map;

import org.openstreetmap.josm.plugins.ods.domains.buildings.OdAddress;
import org.openstreetmap.josm.plugins.ods.domains.buildings.OdBuilding;
import org.openstreetmap.josm.plugins.ods.domains.buildings.impl.OdBuildingStore;
import org.openstreetmap.josm.plugins.ods.entities.opendata.OdLayerManager;

public class BagBuildingEntityPrimitiveBuilder extends BagEntityPrimitiveBuilder<OdBuilding> {

    public BagBuildingEntityPrimitiveBuilder(OdLayerManager dataLayer, OdBuildingStore buildingStore) {
        super(dataLayer, buildingStore);
    }

    @Override
    public void createPrimitive(OdBuilding building) {
        // Ignore buildings with status "Bouwvergunning verleend"
        // Make an exception for buildings that already exist in OSM. In that case, the building permit is for reconstruction
        if ("Bouwvergunning verleend".equals(building.getStatus())
                && building.getMatch() == null) {
            return;
        }
        super.createPrimitive(building);
    }


    @Override
    protected void buildTags(OdBuilding building, Map<String, String> tags) {
        OdAddress address = building.getAddress();
        if (address != null) {
            createAddressTags(address, tags);
        }
        tags.put("source", "BAG");
        tags.put("source:date", building.getSourceDate());
        tags.put("ref:bag", building.getBuildingId().toString());
        if (building.getStartDate() != null) {
            tags.put("start_date", building.getStartDate());
        }
        if ("Sloopvergunning verleend".equals(building.getStatus())) {
            tags.put("note", "Sloopvergunning verleend");
        }
        String type = "yes";
        switch (building.getBuildingType()) {
        case APARTMENTS:
            type = "apartments";
            break;
        case GARAGE:
            type = "garage";
            break;
        case HOUSE:
            type = "house";
            break;
        case INDUSTRIAL:
            type = "industrial";
            break;
        case OFFICE:
            type = "office";
            break;
        case PRISON:
            tags.put("amenity", "prison");
            break;
        case RETAIL:
            type = "retail";
            break;
        case SUBSTATION:
            tags.put("power", "substation");
            break;
        case OTHER:
            type = building.getBuildingType().getSubType();
            break;
        default:
            type = "yes";
            break;
        }

        switch (building.getStatus()) {
        case UNDER_CONSTRUCTION:
        case PROJECTED:
            tags.put("building", "construction");
            tags.put("construction", type);
            break;
        default:
            tags.put("building", type);
        }
    }
}
