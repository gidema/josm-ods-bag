package org.openstreetmap.josm.plugins.ods.bag;

import org.openstreetmap.josm.plugins.ods.OdsModule;
import org.openstreetmap.josm.plugins.ods.entities.opendata.OpenDataLayerDownloader;
import org.openstreetmap.josm.plugins.ods.entities.osm.OsmLayerDownloader;
import org.openstreetmap.josm.plugins.ods.io.DownloadResponse;
import org.openstreetmap.josm.plugins.ods.io.LayerDownloader;
import org.openstreetmap.josm.plugins.ods.io.MainDownloader;
import org.openstreetmap.josm.plugins.ods.matching.AddressNodeMatcher;
import org.openstreetmap.josm.plugins.ods.matching.BuildingMatcher;

public class BagDownloader extends MainDownloader {
    final private OpenDataLayerDownloader openDataLayerDownloader;
    final private OsmLayerDownloader osmLayerDownloader;
    final private BuildingMatcher buildingMatcher;
    final private AddressNodeMatcher addressNodeMatcher;
    
    public BagDownloader(OdsModule module) {
        this.openDataLayerDownloader = new BagWfsLayerDownloader(module);
        this.osmLayerDownloader = new OsmLayerDownloader(module);
        this.buildingMatcher = new BuildingMatcher(module);
        this.addressNodeMatcher = new AddressNodeMatcher(module);
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
    }
}
