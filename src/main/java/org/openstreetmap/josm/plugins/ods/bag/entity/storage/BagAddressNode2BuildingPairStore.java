package org.openstreetmap.josm.plugins.ods.bag.entity.storage;

import java.util.Iterator;
import java.util.stream.Stream;

import org.openstreetmap.josm.plugins.ods.bag.entity.impl.BagAddressNode_BuildingPair;
import org.openstreetmap.josm.plugins.ods.entities.storage.EntityStore;
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
public class BagAddressNode2BuildingPairStore implements EntityStore<BagAddressNode_BuildingPair> {
    private final PrimaryIndex<BagAddressNode_BuildingPair> index = new UniqueIndexImpl<>(BagAddressNode_BuildingPair::getPrimaryId);
    
    public BagAddressNode2BuildingPairStore() {
        super();
    }

    @Override
    public Iterator<BagAddressNode_BuildingPair> iterator() {
        return index.iterator();
    }

    @Override
    public void add(BagAddressNode_BuildingPair entity) {
        index.insert(entity);
    }

    @Override
    public Stream<BagAddressNode_BuildingPair> stream() {
        return index.stream();
    }

    @Override
    public void remove(BagAddressNode_BuildingPair entity) {
        index.remove(entity);
    }

    @Override
    public void clear() {
        index.clear();
    }

}
