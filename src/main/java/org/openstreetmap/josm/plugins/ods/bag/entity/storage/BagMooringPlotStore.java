package org.openstreetmap.josm.plugins.ods.bag.entity.storage;

import java.util.Arrays;
import java.util.List;

import org.openstreetmap.josm.plugins.ods.bag.entity.BagMooringPlot;
import org.openstreetmap.josm.plugins.ods.entities.storage.AbstractGeoEntityStore;
import org.openstreetmap.josm.plugins.ods.entities.storage.GeoIndex;
import org.openstreetmap.josm.plugins.ods.entities.storage.GeoIndexImpl;
import org.openstreetmap.josm.plugins.ods.entities.storage.Index;
import org.openstreetmap.josm.plugins.ods.entities.storage.PrimaryIndex;
import org.openstreetmap.josm.plugins.ods.entities.storage.UniqueIndexImpl;

/**
 * Store ligplaats entities created from geotools features.
 * This store has indexes on the referenceId and a geoIndex.
 * The primary index is on the primitive Id
 *
 * @author Gertjan Idema <mail@gertjanidema.nl>
 *
 */
public class BagMooringPlotStore extends AbstractGeoEntityStore<BagMooringPlot> {
    private final PrimaryIndex<BagMooringPlot> primaryIndex = new UniqueIndexImpl<>(BagMooringPlot::getId);
    private final GeoIndex<BagMooringPlot> geoIndex = new GeoIndexImpl<>(BagMooringPlot.class, "geometry");
    private final List<Index<BagMooringPlot>> allIndexes = Arrays.asList(primaryIndex, geoIndex);

    public BagMooringPlotStore() {
        super();
    }

    @Override
    public PrimaryIndex<BagMooringPlot> getPrimaryIndex() {
        return primaryIndex;
    }

    public PrimaryIndex<BagMooringPlot> getMooringIdIndex() {
        return primaryIndex;
    }

    @Override
    public GeoIndex<BagMooringPlot> getGeoIndex() {
        return geoIndex;
    }

    @Override
    public List<Index<BagMooringPlot>> getAllIndexes() {
        return allIndexes;
    }
}
