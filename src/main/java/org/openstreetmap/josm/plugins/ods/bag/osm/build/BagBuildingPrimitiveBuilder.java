package org.openstreetmap.josm.plugins.ods.bag.osm.build;

import org.openstreetmap.josm.data.osm.DataSet;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.plugins.ods.entities.builtenvironment.Address;
import org.openstreetmap.josm.plugins.ods.entities.builtenvironment.Building;
import org.openstreetmap.josm.plugins.ods.osm.build.AddressPrimitiveBuilder;

public class BagBuildingPrimitiveBuilder extends BagPrimitiveBuilder<Building> {

    public BagBuildingPrimitiveBuilder(DataSet targetDataSet) {
        super(targetDataSet);
    }

    @Override
    protected void buildTags(Building building, OsmPrimitive primitive) {
        Address address = building.getAddress();
        if (address != null) {
            AddressPrimitiveBuilder.buildTags(address, primitive);
        }
        primitive.put("source", "BAG");
        primitive.put("source:date", building.getSourceDate());
        primitive.put("ref:bag", building.getReferenceId().toString());
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
            primitive.put("floating", "yes");
            break;
        case INDUSTRIAL:
            type = "industrial";
            break;
        case OFFICE:
            type = "office";
            break;
        case PRISON:
            primitive.put("amenity", "prison");
            break;
        case RETAIL:
            type = "retail";
            break;
        case STATIC_CARAVAN:
            type = "static_caravan";
            break;
        case SUBSTATION:
            primitive.put("power", "substation");
            break;
        case OTHER:
            type = building.getBuildingType().getSubType();
            break;
        default:
            type = "yes";
            break;
        }
        
        if (building.isUnderConstruction()) {
            primitive.put("building", "construction");
            primitive.put("construction", type);                
        }
        else {
            primitive.put("building", type);
        }
    }
}
