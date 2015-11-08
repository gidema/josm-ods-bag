package org.openstreetmap.josm.plugins.ods.bag;

import org.openstreetmap.josm.plugins.ods.OdsModule;
import org.openstreetmap.josm.plugins.ods.bag.osm.build.BagOsmAddressNodeBuilder;
import org.openstreetmap.josm.plugins.ods.bag.osm.build.BagOsmBuildingBuilder;
import org.openstreetmap.josm.plugins.ods.entities.osm.OsmLayerDownloader;

public class BagOsmLayerDownloader extends OsmLayerDownloader {

    public BagOsmLayerDownloader(OdsModule module) {
        super(module);
    }
}
