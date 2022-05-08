package org.openstreetmap.josm.plugins.ods.bag.entity.osm;

import java.util.Arrays;
import java.util.List;

import org.openstreetmap.josm.plugins.ods.domains.places.OsmCity;
import org.openstreetmap.josm.plugins.ods.entities.storage.AbstractGeoEntityStore;
import org.openstreetmap.josm.plugins.ods.entities.storage.GeoIndex;
import org.openstreetmap.josm.plugins.ods.entities.storage.GeoIndexImpl;
import org.openstreetmap.josm.plugins.ods.entities.storage.Index;
import org.openstreetmap.josm.plugins.ods.entities.storage.IndexImpl;
import org.openstreetmap.josm.plugins.ods.entities.storage.PrimaryIndex;
import org.openstreetmap.josm.plugins.ods.entities.storage.UniqueIndexImpl;

public class OsmCityStore extends AbstractGeoEntityStore<OsmCity> {
    private final PrimaryIndex<OsmCity> primitiveIndex = new UniqueIndexImpl<>(OsmCity::getPrimitiveId);
    private final Index<OsmCity> cityIdIndex = new IndexImpl<>(OsmCity.class, OsmCity::getCityId);
    private final GeoIndex<OsmCity> geoIndex = new GeoIndexImpl<>(OsmCity.class, "geometry");
    private final List<Index<OsmCity>> allIndexes = Arrays.asList(primitiveIndex, cityIdIndex, geoIndex);

    public OsmCityStore() {
        super();
    }

    @Override
    public PrimaryIndex<OsmCity> getPrimaryIndex() {
        return primitiveIndex;
    }

    public Index<OsmCity> getCityIdIndex() {
        return cityIdIndex;
    }

    @Override
    public GeoIndex<OsmCity> getGeoIndex() {
        return geoIndex;
    }

    @Override
    public List<Index<OsmCity>> getAllIndexes() {
        return allIndexes;
    }
    
    
}
