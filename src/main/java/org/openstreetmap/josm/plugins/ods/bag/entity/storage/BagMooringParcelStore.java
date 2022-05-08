package org.openstreetmap.josm.plugins.ods.bag.entity.storage;

import java.util.Arrays;
import java.util.List;

import org.openstreetmap.josm.plugins.ods.bag.entity.BagMooringParcel;
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
public class BagMooringParcelStore extends AbstractGeoEntityStore<BagMooringParcel> {
    private final PrimaryIndex<BagMooringParcel> primaryIndex = new UniqueIndexImpl<>(BagMooringParcel::getLigplaatsId);
    private final GeoIndex<BagMooringParcel> geoIndex = new GeoIndexImpl<>(BagMooringParcel.class, "geometry");
    private final List<Index<BagMooringParcel>> allIndexes = Arrays.asList(primaryIndex, geoIndex);

    public BagMooringParcelStore() {
        super();
    }

    @Override
    public PrimaryIndex<BagMooringParcel> getPrimaryIndex() {
        return primaryIndex;
    }

    public PrimaryIndex<BagMooringParcel> getMooringIdIndex() {
        return primaryIndex;
    }

    @Override
    public GeoIndex<BagMooringParcel> getGeoIndex() {
        return geoIndex;
    }

    @Override
    public List<Index<BagMooringParcel>> getAllIndexes() {
        return allIndexes;
    }
}
