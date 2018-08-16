package org.openstreetmap.josm.plugins.ods.bag.osm.build;

import java.util.Map;

import org.openstreetmap.josm.plugins.ods.domains.places.OdCity;
import org.openstreetmap.josm.plugins.ods.entities.opendata.OdLayerManager;
import org.openstreetmap.josm.plugins.ods.entities.storage.OdEntityStore;

public class BagCityPrimitiveBuilder extends BagEntityPrimitiveBuilder<OdCity> {

    public BagCityPrimitiveBuilder(OdLayerManager dataLayer, OdEntityStore<OdCity, Long> cityStore) {
        super(dataLayer, cityStore);
    }

    @Override
    protected void buildTags(OdCity city, Map<String, String> tags) {
        tags.put("source", "BAG");
        tags.put("source:date", city.getSourceDate());
        tags.put("boundary",  "administrative");
        tags.put("admin_level", "10");
        tags.put("type", "boundary");
        tags.put("name", city.getName());
        tags.put("ref:woonplaatscode", city.getCityId().toString());
    }
}
