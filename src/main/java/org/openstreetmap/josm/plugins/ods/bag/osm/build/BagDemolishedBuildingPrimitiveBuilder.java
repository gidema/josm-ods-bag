package org.openstreetmap.josm.plugins.ods.bag.osm.build;

import static org.openstreetmap.josm.plugins.ods.entities.Entity.Completeness.Complete;

import java.util.Map;

import org.openstreetmap.josm.plugins.ods.bag.entity.BagBuilding;
import org.openstreetmap.josm.plugins.ods.bag.entity.storage.BagBuildingStore;
import org.openstreetmap.josm.plugins.ods.context.OdsContext;
import org.openstreetmap.josm.plugins.ods.entities.opendata.OdLayerManager;

public class BagDemolishedBuildingPrimitiveBuilder extends BagEntityPrimitiveBuilder<BagBuilding> {

    public BagDemolishedBuildingPrimitiveBuilder() {
        super();
    }

    
    @Override
    public void run(OdsContext context) {
        BagBuildingStore buildingStore = context.getComponent(BagBuildingStore.class);
        OdLayerManager layerManager = context.getComponent(OdLayerManager.class);
        buildingStore.forEach(building -> {
            if (building.getPrimitive() == null && building.getCompleteness() == Complete) {
                 createPrimitive(building, layerManager);
            }
        });
    }

    @Override
    public void createPrimitive(BagBuilding building, OdLayerManager layerManager) {
        // Ignore buildings with no matching OSM building.
        if (building.getMatch() == null) {
            return;
        }
        super.createPrimitive(building, layerManager);
    }

    @Override
    protected void buildTags(BagBuilding building, Map<String, String> tags) {
        tags.put("source", "BAG");
        tags.put("source:date", building.getSourceDate());
        tags.put("ref:bag", building.getBuildingId().toString());
        if (building.getStartDate() != null) {
            tags.put("start_date", building.getStartDate());
        }
        tags.put("building", "yes");
    }
}
