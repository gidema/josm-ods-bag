package org.openstreetmap.josm.plugins.ods.bag.entity.storage;

import java.util.Arrays;
import java.util.List;

import org.openstreetmap.josm.plugins.ods.bag.entity.BagStaticCaravanParcel;
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
public class BagStaticCaravanParcelStore extends AbstractGeoEntityStore<BagStaticCaravanParcel> {
    private final PrimaryIndex<BagStaticCaravanParcel> primaryIndex = new UniqueIndexImpl<>(BagStaticCaravanParcel::getStandplaatsId);
    private final GeoIndex<BagStaticCaravanParcel> geoIndex = new GeoIndexImpl<>(BagStaticCaravanParcel.class, "geometry");
    private final List<Index<BagStaticCaravanParcel>> allIndexes = Arrays.asList(primaryIndex, geoIndex);

    public BagStaticCaravanParcelStore() {
        super();
    }

    @Override
    public PrimaryIndex<BagStaticCaravanParcel> getPrimaryIndex() {
        return primaryIndex;
    }

    public PrimaryIndex<BagStaticCaravanParcel> getStandplaaatsIdIndex() {
        return primaryIndex;
    }


    @Override
    public GeoIndex<BagStaticCaravanParcel> getGeoIndex() {
        return geoIndex;
    }

    @Override
    public List<Index<BagStaticCaravanParcel>> getAllIndexes() {
        return allIndexes;
    }
    
}
