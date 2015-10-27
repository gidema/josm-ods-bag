package org.openstreetmap.josm.plugins.ods.bag;

import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.plugins.ods.OdsModule;
import org.openstreetmap.josm.plugins.ods.entities.actual.Building;
import org.openstreetmap.josm.plugins.ods.entities.opendata.OpenDataLayerDownloader;
import org.openstreetmap.josm.plugins.ods.entities.osm.OsmLayerDownloader;
import org.openstreetmap.josm.plugins.ods.io.DownloadResponse;
import org.openstreetmap.josm.plugins.ods.io.LayerDownloader;
import org.openstreetmap.josm.plugins.ods.io.MainDownloader;
import org.openstreetmap.josm.plugins.ods.matching.BuildingMatcher;
import org.openstreetmap.josm.plugins.ods.matching.Match;

public class BagDownloader extends MainDownloader {
    final private OdsModule module;
    final private OpenDataLayerDownloader openDataLayerDownloader;
    final private OsmLayerDownloader osmLayerDownloader;
    final private BuildingMatcher buildingMatcher;
    
    public BagDownloader(OdsModule module) {
        this.module = module;
        this.openDataLayerDownloader = new BagWfsLayerDownloader(module);
        this.osmLayerDownloader = new BagOsmLayerDownloader(module);
        this.buildingMatcher = new BuildingMatcher(module.getDataManager());
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
        updateOdsTags();
    }

    private void updateOdsTags() {
        for (Building building : module.getDataManager().getOpenDataEntityStore(Building.class)) {
            @SuppressWarnings("unchecked")
            Match<Building> match = (Match<Building>) building.getMatch();
            if (match == null) {
                OsmPrimitive osm = building.getPrimitive();
                if (osm != null) {
                    osm.put("ods:nomatch", "");
                }
            }
            else if (match.isSimple()) {
                OsmPrimitive osm = building.getPrimitive();
                if (osm != null) {
                    osm.put("ods:match", "");
                }
            }
        };
    }
}
