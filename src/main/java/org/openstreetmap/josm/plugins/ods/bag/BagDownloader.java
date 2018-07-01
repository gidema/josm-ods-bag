package org.openstreetmap.josm.plugins.ods.bag;

import org.openstreetmap.josm.plugins.ods.Context;
import org.openstreetmap.josm.plugins.ods.domains.buildings.impl.OdAddressNodeStore;
import org.openstreetmap.josm.plugins.ods.domains.buildings.impl.OdBuildingStore;
import org.openstreetmap.josm.plugins.ods.domains.buildings.impl.OsmAddressNodeStore;
import org.openstreetmap.josm.plugins.ods.domains.buildings.impl.OsmBuildingStore;
import org.openstreetmap.josm.plugins.ods.domains.buildings.matching.BuildingMatcher;
import org.openstreetmap.josm.plugins.ods.entities.opendata.OpenDataLayerDownloader;
import org.openstreetmap.josm.plugins.ods.io.DownloadResponse;
import org.openstreetmap.josm.plugins.ods.io.LayerDownloader;
import org.openstreetmap.josm.plugins.ods.io.MainDownloader;
import org.openstreetmap.josm.plugins.ods.io.OsmLayerDownloader;
import org.openstreetmap.josm.plugins.ods.matching.AddressNodeMatcher;
import org.openstreetmap.josm.plugins.ods.matching.OsmAnalyzer;

public class BagDownloader extends MainDownloader {
    private final Context context;
    private OpenDataLayerDownloader openDataLayerDownloader;
    private OsmLayerDownloader osmLayerDownloader;
    private BuildingMatcher buildingMatcher;
    private AddressNodeMatcher addressNodeMatcher;
    private OsmAnalyzer osmAnalyzer;

    public BagDownloader(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    public void initialize() throws Exception {
        this.openDataLayerDownloader = new BagWfsLayerDownloader(context);
        this.osmLayerDownloader = new OsmLayerDownloader(context);
        this.osmAnalyzer = context.get(OsmAnalyzer.class);
        // TODO Use dependency injection for the next 2 lines
        OsmBuildingStore osmBuildingStore = context.get(OsmBuildingStore.class);
        OdBuildingStore odBuildingStore = context.get(OdBuildingStore.class);
        OsmAddressNodeStore osmAddressNodeStore = context.get(OsmAddressNodeStore.class);
        OdAddressNodeStore odAddressNodeStore = context.get(OdAddressNodeStore.class);
        this.buildingMatcher = new BuildingMatcher(osmBuildingStore, odBuildingStore);
        this.addressNodeMatcher = new AddressNodeMatcher(osmAddressNodeStore, odAddressNodeStore);

    }

    @Override
    protected LayerDownloader getOsmLayerDownloader() {
        return osmLayerDownloader;
    }

    @Override
    public LayerDownloader getOpenDataLayerDownloader() {
        return openDataLayerDownloader;
    }

    @Override
    protected void process(DownloadResponse response) {
        super.process(response);
        buildingMatcher.run();
        addressNodeMatcher.run();
        osmAnalyzer.run();
    }
}
