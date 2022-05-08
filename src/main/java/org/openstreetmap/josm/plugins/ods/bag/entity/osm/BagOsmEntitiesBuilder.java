package org.openstreetmap.josm.plugins.ods.bag.entity.osm;

import java.util.Collection;

import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.gui.layer.OsmDataLayer;
import org.openstreetmap.josm.plugins.ods.context.OdsContext;
import org.openstreetmap.josm.plugins.ods.context.OdsContextJob;
import org.openstreetmap.josm.plugins.ods.entities.osm.OsmEntityBuilder;
import org.openstreetmap.josm.plugins.ods.entities.osm.OsmEntityBuilders;
import org.openstreetmap.josm.plugins.ods.entities.osm.OsmLayerManager;

public class BagOsmEntitiesBuilder implements OdsContextJob {

    public BagOsmEntitiesBuilder() {
        super();
    }

    /**
     * Build ODS entities from OSM primitives.
     * Check all primitives in the OSM layer
     *
     */
    public void run(OdsContext context) {
        OsmLayerManager layerManager = context.getComponent(OsmLayerManager.class);
        OsmDataLayer dataLayer = layerManager.getOsmDataLayer();
        if (dataLayer == null) return;
        OsmEntityBuilders entityBuilders = context.getComponent(OsmEntityBuilders.class);
        build(entityBuilders, dataLayer.getDataSet().allPrimitives());
    }

    /**
     * Build Ods entities from the provided OSM primitives
     *
     * @param osmPrimitives
     */
    public void build(OsmEntityBuilders entityBuilders, Collection<? extends OsmPrimitive> osmPrimitives) {
        for (OsmPrimitive primitive : osmPrimitives) {
            for (OsmEntityBuilder<?> builder : entityBuilders) {
                builder.buildOsmEntity(primitive);
            }
        }
    }
}
