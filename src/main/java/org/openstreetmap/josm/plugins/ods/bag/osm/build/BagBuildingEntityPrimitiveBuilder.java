package org.openstreetmap.josm.plugins.ods.bag.osm.build;

import static org.openstreetmap.josm.plugins.ods.entities.Entity.Completeness.Complete;

import java.util.Map;

import org.openstreetmap.josm.plugins.ods.ODS;
import org.openstreetmap.josm.plugins.ods.bag.entity.BagBuilding;
import org.openstreetmap.josm.plugins.ods.bag.entity.BuildingStatus;
import org.openstreetmap.josm.plugins.ods.bag.entity.storage.BagBuildingStore;
import org.openstreetmap.josm.plugins.ods.context.OdsContext;
import org.openstreetmap.josm.plugins.ods.entities.opendata.OdLayerManager;

public class BagBuildingEntityPrimitiveBuilder extends BagEntityPrimitiveBuilder<BagBuilding> {

    public BagBuildingEntityPrimitiveBuilder() {
        super();
    }

    
    @Override
    public void run(OdsContext context) {
        OdLayerManager layerManager = context.getComponent(OdLayerManager.class);
        BagBuildingStore buildingStore = context.getComponent(BagBuildingStore.class);
        buildingStore.forEach(building -> {
            if (building.getPrimitive() == null && building.getCompleteness() == Complete) {
                 createPrimitive(building, layerManager);
            }
        });
//        BagDemolishedBuildingStore demolishedBuildingStore = context.getComponent(BagDemolishedBuildingStore.class);
//        demolishedBuildingStore.forEach(building -> {
//            if (building.getPrimitive() == null && building.getCompleteness() == Complete) {
//                 createPrimitive(building, layerManager);
//            }
//        });
    }


    @Override
    public void createPrimitive(BagBuilding building, OdLayerManager layerManager) {
        // Ignore buildings with status "Bouwvergunning verleend"
        // Make an exception for buildings that already exist in OSM. In that case, the building permit is for reconstruction
        if (building.getStatus() == BuildingStatus.PLANNED && building.getMatch() == null) {
            return;
        }
        super.createPrimitive(building, layerManager);
    }


    @Override
    protected void buildTags(BagBuilding building, Map<String, String> tags) {
        tags.put("source", "BAG");
        tags.put("source:date", building.getSourceDate());
        tags.put("ref:bag", BagEntityPrimitiveBuilder.formatBagId(building.getBuildingId()));
        if (building.getStartDate() != null) {
            tags.put("start_date", building.getStartDate());
        }
        tags.put(ODS.KEY.STATUS, building.getStatus().toString());
        if (building.getStatus() == BuildingStatus.REMOVAL_DUE) {
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
        default:
            type = "yes";
            break;
        }

        if (building.getStatus().equals(BuildingStatus.CONSTRUCTION) ||
                building.getStatus().equals(BuildingStatus.PLANNED)) {
            tags.put("building", "construction");
            tags.put("construction", type);
        }
        else {
            tags.put("building", type);
        }
    }
}
