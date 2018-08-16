package org.openstreetmap.josm.plugins.ods.bag.setup;

import java.util.ArrayList;
import java.util.List;

import org.openstreetmap.josm.plugins.ods.bag.osm.build.BagOsmAddressNodeBuilder;
import org.openstreetmap.josm.plugins.ods.bag.osm.build.BagOsmBuildingBuilder;
import org.openstreetmap.josm.plugins.ods.bag.setup.BagModuleSetup.EntityStores;
import org.openstreetmap.josm.plugins.ods.binding.OsmAddressNodeToBuildingBinder;
import org.openstreetmap.josm.plugins.ods.entities.osm.OsmEntitiesBuilder;
import org.openstreetmap.josm.plugins.ods.entities.osm.OsmEntityBuilder;
import org.openstreetmap.josm.plugins.ods.entities.osm.OsmLayerManager;
import org.openstreetmap.josm.plugins.ods.io.OsmLayerDownloader;
import org.openstreetmap.josm.plugins.ods.jts.GeoUtil;

public class BagOsmSetup {
    private final GeoUtil geoUtil;
    private final EntityStores stores;
    private final OsmLayerManager osmLayerManager;
    private OsmLayerDownloader osmLayerDownloader;

    public BagOsmSetup(OsmLayerManager osmLayerManager, EntityStores stores) {
        super();
        this.geoUtil = new GeoUtil();
        this.stores = stores;
        this.osmLayerManager = osmLayerManager;
        setup();
    }

    private void setup() {
        OsmEntitiesBuilder entitiesBuilder = setupEntitiesBuilder();

        List<Runnable> processors = setupProcessors();
        this.osmLayerDownloader = new OsmLayerDownloader(osmLayerManager, entitiesBuilder, processors);
    }

    public OsmLayerDownloader getOsmLayerDownloader() {
        return osmLayerDownloader;
    }

    private OsmEntitiesBuilder setupEntitiesBuilder() {
        List<OsmEntityBuilder<?>> entityBuilders = new ArrayList<>(2);
        entityBuilders.add(new BagOsmBuildingBuilder(osmLayerManager, stores.osmBuilding, geoUtil));
        entityBuilders.add(new BagOsmAddressNodeBuilder(osmLayerManager, stores.osmAddressNode, geoUtil));
        OsmEntitiesBuilder entitiesBuilder = new OsmEntitiesBuilder(entityBuilders, osmLayerManager);
        return entitiesBuilder;
    }

    private List<Runnable> setupProcessors() {
        List<Runnable> processors = new ArrayList<>(4);
        processors.add(new OsmAddressNodeToBuildingBinder(stores.osmBuilding, stores.osmAddressNode));
        return processors;
    }
}
