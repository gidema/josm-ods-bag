package org.openstreetmap.josm.plugins.ods.bag.entity.storage;

import java.util.Collections;
import java.util.List;

import org.openstreetmap.josm.plugins.ods.bag.entity.impl.BuildingUnit_BuildingPair;
import org.openstreetmap.josm.plugins.ods.entities.storage.AbstractEntityStore;
import org.openstreetmap.josm.plugins.ods.entities.storage.Index;
import org.openstreetmap.josm.plugins.ods.entities.storage.PrimaryIndex;
import org.openstreetmap.josm.plugins.ods.entities.storage.UniqueIndexImpl;

/**
 * Store relations between BuildingUnits and Buildings.
 * This store has indexes on the referenceId and a geoIndex.
 * The primary index is on the primitive Id
 *
 * @author Gertjan Idema <mail@gertjanidema.nl>
 *
 */
public class BagBuildingUnit2BuildingPairStore extends AbstractEntityStore<BuildingUnit_BuildingPair> {
    private final PrimaryIndex<BuildingUnit_BuildingPair> primaryIndex = new UniqueIndexImpl<>(BuildingUnit_BuildingPair::getPrimaryId);
    private final List<Index<BuildingUnit_BuildingPair>> allIndexes = Collections.singletonList(primaryIndex);
    
    public BagBuildingUnit2BuildingPairStore() {
        super();
    }

    @Override
    public PrimaryIndex<BuildingUnit_BuildingPair> getPrimaryIndex() {
        return primaryIndex;
    }

    @Override
    public List<Index<BuildingUnit_BuildingPair>> getAllIndexes() {
        return allIndexes;
    }
}
