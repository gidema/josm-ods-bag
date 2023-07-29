package org.openstreetmap.josm.plugins.ods.bag.entity.osm;

import java.util.Arrays;
import java.util.List;

import org.openstreetmap.josm.plugins.ods.entities.storage.AbstractGeoEntityStore;
import org.openstreetmap.josm.plugins.ods.entities.storage.GeoIndex;
import org.openstreetmap.josm.plugins.ods.entities.storage.GeoIndexImpl;
import org.openstreetmap.josm.plugins.ods.entities.storage.Index;
import org.openstreetmap.josm.plugins.ods.entities.storage.IndexImpl;
import org.openstreetmap.josm.plugins.ods.entities.storage.PrimaryIndex;
import org.openstreetmap.josm.plugins.ods.entities.storage.UniqueIndexImpl;

/**
 * Store bag entities for static caravan plots created from osm primitives.
 * This store has indexes on the primitiveId and a geoIndex.
 * The primary index is on the primitive Id
 *
 * @author Gertjan Idema <mail@gertjanidema.nl>
 *
 */
public class OsmBagStaticCaravanPlotStore extends AbstractGeoEntityStore<OsmBagStaticCaravanPlot> {
    private final PrimaryIndex<OsmBagStaticCaravanPlot> primitiveIndex = new UniqueIndexImpl<>(OsmBagStaticCaravanPlot::getPrimitiveId);
    private final Index<OsmBagStaticCaravanPlot> bagIdIndex = new IndexImpl<>(OsmBagStaticCaravanPlot.class, OsmBagStaticCaravanPlot::getBagId);
    private final GeoIndex<OsmBagStaticCaravanPlot> geoIndex = new GeoIndexImpl<>(OsmBagStaticCaravanPlotImpl.class, "geometry");
    private final List<Index<OsmBagStaticCaravanPlot>> allIdexes = Arrays.asList(primitiveIndex, bagIdIndex, geoIndex);

    public OsmBagStaticCaravanPlotStore() {
        super();
    }

    @Override
    public PrimaryIndex<OsmBagStaticCaravanPlot> getPrimaryIndex() {
        return primitiveIndex;
    }

    public Index<OsmBagStaticCaravanPlot> getBagIdIndex() {
        return bagIdIndex;
    }

    @Override
    public GeoIndex<OsmBagStaticCaravanPlot> getGeoIndex() {
        return geoIndex;
    }

    @Override
    public List<Index<OsmBagStaticCaravanPlot>> getAllIndexes() {
        return allIdexes;
    }
}
