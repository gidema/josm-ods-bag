package org.openstreetmap.josm.plugins.ods.bag.osm.build;

import java.util.Map;

import org.openstreetmap.josm.plugins.ods.DataLayer;
import org.openstreetmap.josm.plugins.ods.entities.builtenvironment.City;

public class BagCityPrimitiveBuilder extends BagPrimitiveBuilder<City> {

    public BagCityPrimitiveBuilder(DataLayer dataLayer) {
        super(dataLayer);
    }

    @Override
    protected void buildTags(City city, Map<String, String> tags) {
        tags.put("source", "BAG");
        tags.put("source:date", city.getSourceDate());
        tags.put("boundary",  "administrative");
        tags.put("admin_level", "10");
        tags.put("type", "boundary");
        tags.put("name", city.getName());
        tags.put("ref:woonplaatscode", city.getReferenceId().toString());
    }
}
