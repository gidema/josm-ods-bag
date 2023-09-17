package org.openstreetmap.josm.plugins.ods.bag.entity.osm;

import org.openstreetmap.josm.plugins.ods.domains.places.OsmCity;
import org.openstreetmap.josm.plugins.ods.entities.storage.Index;
import org.openstreetmap.josm.plugins.ods.entities.storage.IndexImpl;
import org.openstreetmap.josm.plugins.ods.entities.storage.OsmEntityStore;

public class OsmCityStore extends OsmEntityStore<OsmCity> {
    private final Index<OsmCity> cityIdIndex = new IndexImpl<>(OsmCity.class, OsmCity::getCityId);

    public OsmCityStore() {
        super();
    }

    public Index<OsmCity> getCityIdIndex() {
        return cityIdIndex;
    }

    @Override
    public void onAdd(OsmCity entity) {
        cityIdIndex.insert(entity);
    }

    @Override
    public void onRemove(OsmCity entity) {
        cityIdIndex.remove(entity);
    }

    @Override
    public void beforeClear() {
        cityIdIndex.clear();
    }


    
    
}
