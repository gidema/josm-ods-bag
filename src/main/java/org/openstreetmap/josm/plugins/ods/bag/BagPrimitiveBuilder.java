package org.openstreetmap.josm.plugins.ods.bag;

import org.openstreetmap.josm.plugins.ods.OdsModule;
import org.openstreetmap.josm.plugins.ods.PrimitiveBuilder;
import org.openstreetmap.josm.plugins.ods.bag.osm.build.BagBuildingPrimitiveBuilder;
import org.openstreetmap.josm.plugins.ods.entities.EntitySource;
import org.openstreetmap.josm.plugins.ods.entities.actual.AddressNode;
import org.openstreetmap.josm.plugins.ods.entities.actual.Building;
import org.openstreetmap.josm.plugins.ods.entities.actual.impl.foreign.OpenDataAddressNodeStore;
import org.openstreetmap.josm.plugins.ods.entities.actual.impl.foreign.OpenDataBuildingStore;
import org.openstreetmap.josm.plugins.ods.entities.external.ExternalDataLayer;
import org.openstreetmap.josm.plugins.ods.entities.managers.DataManager;
import org.openstreetmap.josm.plugins.ods.osm.BuildingAligner;
import org.openstreetmap.josm.plugins.ods.osm.BuildingSegmentSimplifier;

public class BagPrimitiveBuilder {
    private DataManager dataManager;
    private EntitySource entitySource;
    private PrimitiveBuilder<AddressNode> addressNodePrimitiveBuilder;
    private PrimitiveBuilder<Building> buildingPrimitiveBuilder;
    private BuildingSegmentSimplifier segmentSimplifier;
    private BuildingAligner buildingAligner;

    public BagPrimitiveBuilder(OdsModule importModule) {
        ExternalDataLayer externalDataLayer = importModule.getExternalDataLayer();
        buildingPrimitiveBuilder = new BagBuildingPrimitiveBuilder(externalDataLayer);
    }
    public void run() {
        OpenDataAddressNodeStore addressNodeStore = dataManager.getAddressNodeManager().getForeignAddressNodes();
        OpenDataBuildingStore buildingStore = dataManager.getBuildingManager().getOpenDataBuildings();
        addressNodeStore.stream().filter(a->a.getEntitySource() == entitySource)
            .forEach(addressNodePrimitiveBuilder::createPrimitive);
        buildingStore.stream().filter(a->a.getEntitySource() == entitySource)
            .forEach(buildingPrimitiveBuilder::createPrimitive);
        buildingStore.stream().filter(a->a.getEntitySource() == entitySource)
            .forEach(segmentSimplifier);
        buildingStore.forEach(buildingAligner);
    }
}
