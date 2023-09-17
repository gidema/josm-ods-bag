package org.openstreetmap.josm.plugins.ods.bag.osm.build;

import static org.openstreetmap.josm.plugins.ods.entities.Entity.Completeness.Complete;

import java.util.Map;

import org.openstreetmap.josm.plugins.ods.ODS;
import org.openstreetmap.josm.plugins.ods.bag.entity.BuildingStatus;
import org.openstreetmap.josm.plugins.ods.bag.entity.DemolishedBuilding;
import org.openstreetmap.josm.plugins.ods.bag.entity.storage.BagDemolishedBuildingStore;
import org.openstreetmap.josm.plugins.ods.context.OdsContext;
import org.openstreetmap.josm.plugins.ods.entities.opendata.OdLayerManager;

public class BagDemolishedBuildingPrimitiveBuilder extends BagEntityPrimitiveBuilder<DemolishedBuilding> {

    public BagDemolishedBuildingPrimitiveBuilder() {
        super();
    }
    
    @Override
    public void run(OdsContext context) {
        BagDemolishedBuildingStore buildingStore = context.getComponent(BagDemolishedBuildingStore.class);
        OdLayerManager layerManager = context.getComponent(OdLayerManager.class);
        buildingStore.forEach(building -> {
            if (building.getPrimitive() == null && building.getCompleteness() == Complete) {
                 createPrimitive(building, layerManager);
            }
        });
    }

    @Override
    public void createPrimitive(DemolishedBuilding building, OdLayerManager layerManager) {
        // Ignore buildings with no matching OSM building.
        if (building.getMapping() == null) {
//            return;
        }
        super.createPrimitive(building, layerManager);
    }

    @Override
    protected void buildTags(DemolishedBuilding building, Map<String, String> tags) {
        tags.put("source", "BAG");
        tags.put("source:date", building.getSourceDate());
        tags.put("ref:bag", BagEntityPrimitiveBuilder.formatBagId(building.getBuildingId()));
        tags.put("building", "yes");
        tags.put(ODS.KEY.STATUS, BuildingStatus.REMOVED.toString());
    }
}
