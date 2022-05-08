package org.openstreetmap.josm.plugins.ods.bag.entity.storage;

import java.util.Arrays;
import java.util.List;

import org.openstreetmap.josm.plugins.ods.bag.entity.BagBuilding;
import org.openstreetmap.josm.plugins.ods.entities.storage.AbstractGeoEntityStore;
import org.openstreetmap.josm.plugins.ods.entities.storage.GeoIndex;
import org.openstreetmap.josm.plugins.ods.entities.storage.Index;
import org.openstreetmap.josm.plugins.ods.entities.storage.IndexImpl;
import org.openstreetmap.josm.plugins.ods.entities.storage.PrimaryIndex;
import org.openstreetmap.josm.plugins.ods.entities.storage.UniqueIndexImpl;

/**
 * Store building entities created from features.
 * This store has indexes on the referenceId and a geoIndex.
 * The primary index is on the building Id
 *
 * @author Gertjan Idema <mail@gertjanidema.nl>
 *
 */
public class BagMissingBuildingStore extends AbstractGeoEntityStore<BagBuilding> {
    private final PrimaryIndex<BagBuilding> primaryIndex = new UniqueIndexImpl<>(BagBuilding::getBuildingId);
    private final IndexImpl<BagBuilding> idIndex = new IndexImpl<>(BagBuilding.class, BagBuilding::getBuildingId);
    private final List<Index<BagBuilding>> allIndexes = Arrays.asList(
            primaryIndex, idIndex);

    public BagMissingBuildingStore() {
        super();
    }

    @Override
    public PrimaryIndex<BagBuilding> getPrimaryIndex() {
        return primaryIndex;
    }

    @Override
    public List<Index<BagBuilding>> getAllIndexes() {
        return allIndexes;
    }

    @Override
    public GeoIndex<BagBuilding> getGeoIndex() {
        throw new UnsupportedOperationException("For permormance reasons, the geo index is not supported for Missing Buildings");
    }
}
