package org.openstreetmap.josm.plugins.ods.bag.entity.storage;

import java.util.Arrays;
import java.util.List;

import org.openstreetmap.josm.plugins.ods.bag.entity.BagStaticCaravanPlot;
import org.openstreetmap.josm.plugins.ods.entities.storage.AbstractGeoEntityStore;
import org.openstreetmap.josm.plugins.ods.entities.storage.GeoIndex;
import org.openstreetmap.josm.plugins.ods.entities.storage.GeoIndexImpl;
import org.openstreetmap.josm.plugins.ods.entities.storage.Index;
import org.openstreetmap.josm.plugins.ods.entities.storage.PrimaryIndex;
import org.openstreetmap.josm.plugins.ods.entities.storage.UniqueIndexImpl;

/**
 * Store standplaats entities created from geotools features.
 * This store has indexes on the referenceId and a geoIndex.
 * The primary index is on the primitive Id
 *
 * @author Gertjan Idema <mail@gertjanidema.nl>
 *
 */
public class BagStaticCaravanPlotStore extends AbstractGeoEntityStore<BagStaticCaravanPlot> {
    private final PrimaryIndex<BagStaticCaravanPlot> primaryIndex = new UniqueIndexImpl<>(BagStaticCaravanPlot::getId);
    private final GeoIndex<BagStaticCaravanPlot> geoIndex = new GeoIndexImpl<>(BagStaticCaravanPlot.class, "geometry");
    private final List<Index<BagStaticCaravanPlot>> allIndexes = Arrays.asList(primaryIndex, geoIndex);

    public BagStaticCaravanPlotStore() {
        super();
    }

    @Override
    public PrimaryIndex<BagStaticCaravanPlot> getPrimaryIndex() {
        return primaryIndex;
    }

    public PrimaryIndex<BagStaticCaravanPlot> getStandplaaatsIdIndex() {
        return primaryIndex;
    }


    @Override
    public GeoIndex<BagStaticCaravanPlot> getGeoIndex() {
        return geoIndex;
    }

    @Override
    public List<Index<BagStaticCaravanPlot>> getAllIndexes() {
        return allIndexes;
    }
    
}
