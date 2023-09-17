package org.openstreetmap.josm.plugins.ods.bag.entity.osm;

import org.openstreetmap.josm.plugins.ods.entities.storage.Index;
import org.openstreetmap.josm.plugins.ods.entities.storage.IndexImpl;
import org.openstreetmap.josm.plugins.ods.entities.storage.OsmEntityStore;

/**
 * Store bag entities for static caravan plots created from osm primitives.
 * This store has indexes on the primitiveId and a geoIndex.
 * The primary index is on the primitive Id
 *
 * @author Gertjan Idema <mail@gertjanidema.nl>
 *
 */
public class OsmBagStaticCaravanPlotStore extends OsmEntityStore<OsmBagStaticCaravanPlot> {
    private final Index<OsmBagStaticCaravanPlot> bagIdIndex = new IndexImpl<>(OsmBagStaticCaravanPlot.class, OsmBagStaticCaravanPlot::getBagId);

    public OsmBagStaticCaravanPlotStore() {
        super();
    }

    public Index<OsmBagStaticCaravanPlot> getBagIdIndex() {
        return bagIdIndex;
    }

    @Override
    public void onAdd(OsmBagStaticCaravanPlot entity) {
        bagIdIndex.insert(entity);
    }

    @Override
    public void onRemove(OsmBagStaticCaravanPlot entity) {
        bagIdIndex.remove(entity);
    }

    @Override
    public void beforeClear() {
        bagIdIndex.clear();
    }
    
    
}
