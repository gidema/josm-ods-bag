package org.openstreetmap.josm.plugins.ods.bag.entity.storage;

import org.openstreetmap.josm.plugins.ods.bag.entity.BagStaticCaravanPlot;
import org.openstreetmap.josm.plugins.ods.entities.storage.GeoIndex;
import org.openstreetmap.josm.plugins.ods.entities.storage.GeoIndexImpl;

/**
 * Store standplaats entities created from geotools features.
 * This store has indexes on the referenceId and a geoIndex.
 * The primary index is on the primitive Id
 *
 * @author Gertjan Idema <mail@gertjanidema.nl>
 *
 */
public class BagStaticCaravanPlotStore extends BagEntityStore<BagStaticCaravanPlot> {
    private final GeoIndex<BagStaticCaravanPlot> geoIndex = new GeoIndexImpl<>();

    public BagStaticCaravanPlotStore() {
        super(BagStaticCaravanPlot::getId);
    }
}
