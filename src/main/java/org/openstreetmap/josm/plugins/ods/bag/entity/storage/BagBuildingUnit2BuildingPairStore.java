package org.openstreetmap.josm.plugins.ods.bag.entity.storage;

import java.util.Iterator;
import java.util.stream.Stream;

import org.openstreetmap.josm.plugins.ods.bag.entity.impl.BuildingUnit_BuildingPair;
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
public class BagBuildingUnit2BuildingPairStore implements EntityStore<BuildingUnit_BuildingPair> {
    private final PrimaryIndex<BuildingUnit_BuildingPair> index = new UniqueIndexImpl<>(BuildingUnit_BuildingPair::getPrimaryId);
    
    public BagBuildingUnit2BuildingPairStore() {
        super();
    }

    @Override
    public Iterator<BuildingUnit_BuildingPair> iterator() {
        return index.iterator();
    }

    @Override
    public void add(BuildingUnit_BuildingPair entity) {
        index.insert(entity);
    }

    @Override
    public Stream<BuildingUnit_BuildingPair> stream() {
        return index.stream();
    }

    @Override
    public void remove(BuildingUnit_BuildingPair entity) {
        index.remove(entity);
    }

    @Override
    public void clear() {
        index.clear();
    }
}
