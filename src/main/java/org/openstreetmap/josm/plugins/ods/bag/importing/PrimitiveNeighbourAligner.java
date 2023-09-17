package org.openstreetmap.josm.plugins.ods.bag.importing;

import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.gui.MainApplication;
import org.openstreetmap.josm.gui.layer.OsmDataLayer;
import org.openstreetmap.josm.plugins.ods.bag.osm.OsmNeighbourFinder;
import org.openstreetmap.josm.plugins.ods.context.OdsContext;
import org.openstreetmap.josm.plugins.ods.context.OdsContextJob;
import org.openstreetmap.josm.plugins.ods.entities.osm.OsmLayerManager;
import org.openstreetmap.josm.plugins.ods.mapping.update.OdsImporter.ImportedPrimitives;

public class PrimitiveNeighbourAligner implements OdsContextJob {

    @Override
    public void run(OdsContext context) {
        ImportedPrimitives importedPrimitives = context.getComponent(ImportedPrimitives.class);
        // Save the current edit layer
        OsmDataLayer savedEditLayer =  MainApplication.getLayerManager().getEditLayer();
        try {
            OsmLayerManager layerManager = context.getComponent(OsmLayerManager.class);
            OsmDataLayer editLayer = layerManager.getOsmDataLayer();
            MainApplication.getLayerManager().setActiveLayer(editLayer);
            OsmNeighbourFinder neighbourFinder = new OsmNeighbourFinder(context);
            for (OsmPrimitive osm : importedPrimitives.get()) {
                neighbourFinder.findNeighbours(osm);
            }
        }
        finally {
            MainApplication.getLayerManager().setActiveLayer(savedEditLayer);
        }
    }

}
