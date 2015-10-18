package org.openstreetmap.josm.plugins.ods.bag.osm.build;

import java.util.Map;

import org.openstreetmap.josm.plugins.ods.DataLayer;
import org.openstreetmap.josm.plugins.ods.entities.actual.Address;
import org.openstreetmap.josm.plugins.ods.entities.actual.Building;

public class BagBuildingPrimitiveBuilder extends BagPrimitiveBuilder<Building> {

    public BagBuildingPrimitiveBuilder(DataLayer dataLayer) {
        super(dataLayer);
    }

    @Override
    protected void buildTags(Building building, Map<String, String> tags) {
        Address address = building.getAddress();
        if (address != null) {
            AddressPrimitiveBuilder.buildTags(address, tags);
        }
        tags.put("source", "BAG");
        tags.put("source:date", building.getSourceDate());
        tags.put("ref:bag", building.getReferenceId().toString());
        tags.put("start_date", building.getStartDate());
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
        case HOUSEBOAT:
            type = "houseboat";
            tags.put("floating", "yes");
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
        case STATIC_CARAVAN:
            type = "static_caravan";
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
        
        if (building.isUnderConstruction()) {
            tags.put("building", "construction");
            tags.put("construction", type);                
        }
        else {
            tags.put("building", type);
        }
    }
}
