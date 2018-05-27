package org.openstreetmap.josm.plugins.ods.bag;

import static org.openstreetmap.josm.plugins.ods.entities.Entity.Completeness.Complete;

import org.openstreetmap.josm.plugins.ods.OdsModule;
import org.openstreetmap.josm.plugins.ods.bag.osm.build.BagAddressNodeEntityPrimitiveBuilder;
import org.openstreetmap.josm.plugins.ods.bag.osm.build.BagBuildingEntityPrimitiveBuilder;
import org.openstreetmap.josm.plugins.ods.domains.buildings.OdAddressNode;
import org.openstreetmap.josm.plugins.ods.domains.buildings.OdBuilding;
import org.openstreetmap.josm.plugins.ods.entities.EntityPrimitiveBuilder;
import org.openstreetmap.josm.plugins.ods.entities.EntityStore;
import org.openstreetmap.josm.plugins.ods.entities.opendata.OdLayerManager;
import org.openstreetmap.josm.plugins.ods.io.DownloadResponse;
import org.openstreetmap.josm.plugins.ods.od.OdBuildingAligner;

public class BagPrimitiveBuilder {
    private final OdsModule module;
    private final EntityPrimitiveBuilder<OdAddressNode> addressNodePrimitiveBuilder;
    private final EntityPrimitiveBuilder<OdBuilding> buildingPrimitiveBuilder;
    private final OdBuildingAligner odBuildingAligner;

    public BagPrimitiveBuilder(OdsModule module) {
        this.module = module;
        OdLayerManager odLayerManager = module.getOpenDataLayerManager();
        buildingPrimitiveBuilder = new BagBuildingEntityPrimitiveBuilder(odLayerManager);
        addressNodePrimitiveBuilder = new BagAddressNodeEntityPrimitiveBuilder(odLayerManager);
        // TODO pass tolerance as a configurable parameter at a higher level.
        odBuildingAligner = new OdBuildingAligner(module, odLayerManager.getEntityStore(OdBuilding.class));
    }

    public void run(DownloadResponse response) {
        EntityStore<OdAddressNode> addressNodeStore = module.getOpenDataLayerManager()
                .getEntityStore(OdAddressNode.class);
        EntityStore<OdBuilding> buildingStore = module.getOpenDataLayerManager()
                .getEntityStore(OdBuilding.class);
        addressNodeStore.stream()
        .filter(addressNode->addressNode.getPrimitive() == null)
        .filter(addressNode->addressNode.getCompleteness() == Complete)
        .forEach(addressNodePrimitiveBuilder::createPrimitive);
        buildingStore.stream()
        .filter(building->building.getPrimitive() == null)
        .filter(building->building.getCompleteness() == Complete)
        .forEach(buildingPrimitiveBuilder::createPrimitive);
        //        buildingStore.stream().filter(building->building.getDownloadResponse() == response)
        //            .forEach(segmentSimplifier::simplify);
        buildingStore.forEach(odBuildingAligner::align);
    }
}
