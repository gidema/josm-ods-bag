package org.openstreetmap.josm.plugins.ods.bag.entity.osm;

import java.util.Arrays;
import java.util.List;

import org.openstreetmap.josm.plugins.ods.entities.storage.AbstractGeoEntityStore;
import org.openstreetmap.josm.plugins.ods.entities.storage.GeoIndex;
import org.openstreetmap.josm.plugins.ods.entities.storage.GeoIndexImpl;
import org.openstreetmap.josm.plugins.ods.entities.storage.Index;
import org.openstreetmap.josm.plugins.ods.entities.storage.IndexImpl;
import org.openstreetmap.josm.plugins.ods.entities.storage.PrimaryIndex;
import org.openstreetmap.josm.plugins.ods.entities.storage.UniqueIndexImpl;

/**
 * Store bag mooring entities for houseboats created from osm primitives.
 * This store has indexes on the referenceId, the primitiveId and a geoIndex.
 * The primary index is on the primitive Id
 *
 * @author Gertjan Idema <mail@gertjanidema.nl>
 *
 */
public class OsmBagMooringStore extends AbstractGeoEntityStore<OsmBagMooring> {
    private final PrimaryIndex<OsmBagMooring> primitiveIndex = new UniqueIndexImpl<>(OsmBagMooring::getPrimitiveId);
    private final Index<OsmBagMooring> mooringIdIndex = new IndexImpl<>(OsmBagMooring.class, OsmBagMooring::getMooringId);
    private final GeoIndex<OsmBagMooring> geoIndex = new GeoIndexImpl<>(OsmBagMooringImpl.class, "geometry");
    private final List<Index<OsmBagMooring>> allIdexes = Arrays.asList(primitiveIndex, mooringIdIndex, geoIndex);

    public OsmBagMooringStore() {
        super();
    }

    @Override
    public PrimaryIndex<OsmBagMooring> getPrimaryIndex() {
        return primitiveIndex;
    }

    public Index<OsmBagMooring> getMooringIdIndex() {
        return mooringIdIndex;
    }

    @Override
    public GeoIndex<OsmBagMooring> getGeoIndex() {
        return geoIndex;
    }

    @Override
    public List<Index<OsmBagMooring>> getAllIndexes() {
        return allIdexes;
    }
}
