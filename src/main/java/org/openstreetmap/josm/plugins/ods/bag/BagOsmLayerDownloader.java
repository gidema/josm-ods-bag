package org.openstreetmap.josm.plugins.ods.bag;

import org.openstreetmap.josm.plugins.ods.OdsModule;
import org.openstreetmap.josm.plugins.ods.bag.osm.build.BagOsmAddressNodeBuilder;
import org.openstreetmap.josm.plugins.ods.bag.osm.build.BagOsmBuildingBuilder;
import org.openstreetmap.josm.plugins.ods.entities.EntityStore;
import org.openstreetmap.josm.plugins.ods.entities.actual.AddressNode;
import org.openstreetmap.josm.plugins.ods.entities.actual.Building;
import org.openstreetmap.josm.plugins.ods.entities.osm.OsmLayerDownloader;

public class BagOsmLayerDownloader extends OsmLayerDownloader {

    public BagOsmLayerDownloader(OdsModule module) {
        super(module.getInternalDataLayer());
        EntityStore<Building> buildingStore = module.getDataManager().getOsmEntityStore(Building.class);
        EntityStore<AddressNode> addressNodeStore = module.getDataManager().getOsmEntityStore(AddressNode.class);
        addEntityBuilder(new BagOsmBuildingBuilder(module.getGeoUtil(), buildingStore));
        addEntityBuilder(new BagOsmAddressNodeBuilder(module.getGeoUtil(), addressNodeStore));
    }
}
