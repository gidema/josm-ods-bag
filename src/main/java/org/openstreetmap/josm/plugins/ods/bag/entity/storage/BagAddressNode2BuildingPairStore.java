package org.openstreetmap.josm.plugins.ods.bag.entity.storage;

import java.util.Collections;
import java.util.List;

import org.openstreetmap.josm.plugins.ods.bag.entity.impl.BagAddressNode_BuildingPair;
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
public class BagAddressNode2BuildingPairStore extends AbstractEntityStore<BagAddressNode_BuildingPair> {
    private final PrimaryIndex<BagAddressNode_BuildingPair> primaryIndex = new UniqueIndexImpl<>(BagAddressNode_BuildingPair::getPrimaryId);
    private final List<Index<BagAddressNode_BuildingPair>> allIndexes = Collections.singletonList(primaryIndex);
    
    public BagAddressNode2BuildingPairStore() {
        super();
    }

    @Override
    public PrimaryIndex<BagAddressNode_BuildingPair> getPrimaryIndex() {
        return primaryIndex;
    }

    @Override
    public List<Index<BagAddressNode_BuildingPair>> getAllIndexes() {
        return allIndexes;
    }
}
