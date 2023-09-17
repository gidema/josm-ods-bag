package org.openstreetmap.josm.plugins.ods.bag.entity.storage;

import org.openstreetmap.josm.plugins.ods.bag.entity.BagMooringPlot;
import org.openstreetmap.josm.plugins.ods.entities.storage.GeoIndex;
import org.openstreetmap.josm.plugins.ods.entities.storage.GeoIndexImpl;

/**
 * Store ligplaats entities created from geotools features.
 * This store has indexes on the referenceId and a geoIndex.
 * The primary index is on the primitive Id
 *
 * @author Gertjan Idema <mail@gertjanidema.nl>
 *
 */
public class BagMooringPlotStore extends BagEntityStore<BagMooringPlot> {
    private final GeoIndex<BagMooringPlot> geoIndex = new GeoIndexImpl<>();

    public BagMooringPlotStore() {
        super(BagMooringPlot::getId);
    }
}
