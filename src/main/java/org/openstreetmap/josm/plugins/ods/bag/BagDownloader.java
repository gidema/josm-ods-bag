package org.openstreetmap.josm.plugins.ods.bag;

import java.util.List;

import org.openstreetmap.josm.plugins.ods.entities.opendata.OpenDataLayerDownloader;
import org.openstreetmap.josm.plugins.ods.io.DownloadResponse;
import org.openstreetmap.josm.plugins.ods.io.LayerDownloader;
import org.openstreetmap.josm.plugins.ods.io.MainDownloader;
import org.openstreetmap.josm.plugins.ods.io.OsmLayerDownloader;
import org.openstreetmap.josm.plugins.ods.matching.OsmAnalyzer;
import org.openstreetmap.josm.plugins.ods.setup.ModuleSetup;

public class BagDownloader extends MainDownloader {
    private final OpenDataLayerDownloader openDataLayerDownloader;
    private final OsmLayerDownloader osmLayerDownloader;
    private OsmAnalyzer osmAnalyzer;

    public BagDownloader(ModuleSetup setup, List<Runnable> processors) {
        super(setup, processors);
        this.openDataLayerDownloader = setup.getOdLayerDownloader();
        this.osmLayerDownloader = setup.getOsmLayerDownloader();
    }

    @Override
    public void initialize() throws Exception {
        // TODO Use dependency injection for the next 2 lines
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
        osmAnalyzer.run();
    }
}
