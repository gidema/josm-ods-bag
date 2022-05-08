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
 * Store building entities created from osm primitives.
 * This store has indexes on the referenceId, the primitiveId and a geoIndex.
 * The primary index is on the primitive Id
 *
 * @author Gertjan Idema <mail@gertjanidema.nl>
 *
 */
public class OsmBuildingStore extends AbstractGeoEntityStore<OsmBuilding> {
    private final PrimaryIndex<OsmBuilding> primitiveIndex = new UniqueIndexImpl<>(OsmBuilding::getPrimitiveId);
    private final Index<OsmBuilding> buildingIdIndex = new IndexImpl<>(OsmBuilding.class, OsmBuilding::getBuildingId);
    private final GeoIndex<OsmBuilding> geoIndex = new GeoIndexImpl<>(OsmBuilding.class, "geometry");
    private final List<Index<OsmBuilding>> allIndexes = Arrays.asList(primitiveIndex, buildingIdIndex, geoIndex);

    public OsmBuildingStore() {
        super();
    }

    @Override
    public PrimaryIndex<OsmBuilding> getPrimaryIndex() {
        return primitiveIndex;
    }

    public Index<OsmBuilding> getBuildingIdIndex() {
        return buildingIdIndex;
    }

    @Override
    public GeoIndex<OsmBuilding> getGeoIndex() {
        return geoIndex;
    }

    @Override
    public List<Index<OsmBuilding>> getAllIndexes() {
        return allIndexes;
    }
}
