package org.openstreetmap.josm.plugins.ods.bag;

import static org.openstreetmap.josm.plugins.ods.entities.Entity.Completeness.Complete;

import org.openstreetmap.josm.plugins.ods.Context;
import org.openstreetmap.josm.plugins.ods.bag.osm.build.BagAddressNodeEntityPrimitiveBuilder;
import org.openstreetmap.josm.plugins.ods.bag.osm.build.BagBuildingEntityPrimitiveBuilder;
import org.openstreetmap.josm.plugins.ods.domains.buildings.OdAddressNode;
import org.openstreetmap.josm.plugins.ods.domains.buildings.OdBuilding;
import org.openstreetmap.josm.plugins.ods.domains.buildings.impl.OdBuildingStore;
import org.openstreetmap.josm.plugins.ods.domains.buildings.impl.OdAddressNodeStore;
import org.openstreetmap.josm.plugins.ods.entities.EntityPrimitiveBuilder;
import org.openstreetmap.josm.plugins.ods.entities.opendata.OdLayerManager;
import org.openstreetmap.josm.plugins.ods.io.DownloadResponse;
import org.openstreetmap.josm.plugins.ods.od.OdBuildingAligner;

public class BagPrimitiveBuilder {
    private final EntityPrimitiveBuilder<OdAddressNode> addressNodePrimitiveBuilder;
    private final EntityPrimitiveBuilder<OdBuilding> buildingPrimitiveBuilder;
    private final OdBuildingAligner odBuildingAligner;
    private final OdBuildingStore odBuildingStore;
    private final OdAddressNodeStore odAddressNodeStore;

    private final OdLayerManager odLayerManager;

    public BagPrimitiveBuilder(Context context) {
        odLayerManager = context.get(OdLayerManager.class);
        buildingPrimitiveBuilder = new BagBuildingEntityPrimitiveBuilder(odLayerManager);
        addressNodePrimitiveBuilder = new BagAddressNodeEntityPrimitiveBuilder(odLayerManager);
        // TODO pass tolerance as a configurable parameter at a higher level.
        odBuildingStore = context.get(OdBuildingStore.class);
        odAddressNodeStore = context.get(OdAddressNodeStore.class);
        odBuildingAligner = new OdBuildingAligner(odBuildingStore);
    }

    public void run(DownloadResponse response) {
        odAddressNodeStore.stream()
        .filter(addressNode->addressNode.getPrimitive() == null)
        .filter(addressNode->addressNode.getCompleteness() == Complete)
        .forEach(addressNodePrimitiveBuilder::createPrimitive);
        odBuildingStore.stream()
        .filter(building->building.getPrimitive() == null)
        .filter(building->building.getCompleteness() == Complete)
        .forEach(buildingPrimitiveBuilder::createPrimitive);
        //        buildingStore.stream().filter(building->building.getDownloadResponse() == response)
        //            .forEach(segmentSimplifier::simplify);
        odBuildingStore.forEach(odBuildingAligner::align);
    }
}
