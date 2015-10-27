package org.openstreetmap.josm.plugins.ods.bag;

import org.openstreetmap.josm.plugins.ods.OdsModule;
import org.openstreetmap.josm.plugins.ods.PrimitiveBuilder;
import org.openstreetmap.josm.plugins.ods.bag.osm.build.BagAddressNodePrimitiveBuilder;
import org.openstreetmap.josm.plugins.ods.bag.osm.build.BagBuildingPrimitiveBuilder;
import org.openstreetmap.josm.plugins.ods.entities.EntityStore;
import org.openstreetmap.josm.plugins.ods.entities.actual.AddressNode;
import org.openstreetmap.josm.plugins.ods.entities.actual.Building;
import org.openstreetmap.josm.plugins.ods.entities.managers.DataManager;
import org.openstreetmap.josm.plugins.ods.entities.opendata.OpenDataLayerManager;
import org.openstreetmap.josm.plugins.ods.io.DownloadResponse;
import org.openstreetmap.josm.plugins.ods.osm.BuildingAligner;
import org.openstreetmap.josm.plugins.ods.osm.BuildingSegmentSimplifier;

public class BagPrimitiveBuilder {
    private DataManager dataManager;
    private PrimitiveBuilder<AddressNode> addressNodePrimitiveBuilder;
    private PrimitiveBuilder<Building> buildingPrimitiveBuilder;
    private BuildingSegmentSimplifier segmentSimplifier;
    private BuildingAligner buildingAligner;

    public BagPrimitiveBuilder(OdsModule module) {
        OpenDataLayerManager odLayerManager = module.getOpenDataLayerManager();
        dataManager = module.getDataManager();
        buildingPrimitiveBuilder = new BagBuildingPrimitiveBuilder(odLayerManager);
        addressNodePrimitiveBuilder = new BagAddressNodePrimitiveBuilder(odLayerManager);
        // TODO pass tolerance as a configurable parameter at a higher level.
        segmentSimplifier = new BuildingSegmentSimplifier(1e-5);
        buildingAligner = new BuildingAligner(module, 1e-5);
    }
    
    public void run(DownloadResponse response) {
        EntityStore<AddressNode> addressNodeStore = dataManager.getOpenDataEntityStore(AddressNode.class);
        addressNodeStore.stream()
            .filter(addressNode->addressNode.getPrimitive() == null)
            .filter(addressNode->!addressNode.isIncomplete())
            .forEach(addressNodePrimitiveBuilder::createPrimitive);
        EntityStore<Building> buildingStore = dataManager.getOpenDataEntityStore(Building.class);
        buildingStore.stream()
            .filter(building->building.getPrimitive() == null)
            .filter(building->!building.isIncomplete())
            .forEach(buildingPrimitiveBuilder::createPrimitive);
        buildingStore.stream().filter(building->building.getDownloadResponse() == response)
            .forEach(segmentSimplifier::simplify);
        buildingStore.forEach(buildingAligner::align);
    }
}
