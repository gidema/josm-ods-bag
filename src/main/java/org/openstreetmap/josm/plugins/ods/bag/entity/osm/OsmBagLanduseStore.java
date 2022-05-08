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
 * Store bag landuse entities for static caravan site created from osm primitives.
 * This store has indexes on the primitiveId and a geoIndex.
 * The primary index is on the primitive Id
 *
 * @author Gertjan Idema <mail@gertjanidema.nl>
 *
 */
public class OsmBagLanduseStore extends AbstractGeoEntityStore<OsmBagLanduse> {
    private final PrimaryIndex<OsmBagLanduse> primitiveIndex = new UniqueIndexImpl<>(OsmBagLanduse::getPrimitiveId);
    private final Index<OsmBagLanduse> bagIdIndex = new IndexImpl<>(OsmBagLanduse.class, OsmBagLanduse::getBagId);
    private final GeoIndex<OsmBagLanduse> geoIndex = new GeoIndexImpl<>(OsmBagLanduseImpl.class, "geometry");
    private final List<Index<OsmBagLanduse>> allIdexes = Arrays.asList(primitiveIndex, bagIdIndex, geoIndex);

    public OsmBagLanduseStore() {
        super();
    }

    @Override
    public PrimaryIndex<OsmBagLanduse> getPrimaryIndex() {
        return primitiveIndex;
    }

    public Index<OsmBagLanduse> getBagIdIndex() {
        return bagIdIndex;
    }

    @Override
    public GeoIndex<OsmBagLanduse> getGeoIndex() {
        return geoIndex;
    }

    @Override
    public List<Index<OsmBagLanduse>> getAllIndexes() {
        return allIdexes;
    }
}
