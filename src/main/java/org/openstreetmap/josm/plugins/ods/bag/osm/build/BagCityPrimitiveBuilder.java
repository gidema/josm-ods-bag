package org.openstreetmap.josm.plugins.ods.bag.osm.build;

import org.openstreetmap.josm.data.osm.DataSet;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.plugins.ods.entities.builtenvironment.City;

public class BagCityPrimitiveBuilder extends BagPrimitiveBuilder<City> {

    public BagCityPrimitiveBuilder(DataSet targetDataSet) {
        super(targetDataSet);
    }

    @Override
    protected void buildTags(City city, OsmPrimitive primitive) {
        primitive.put("source", "BAG");
        primitive.put("source:date", city.getSourceDate());
        primitive.put("boundary",  "administrative");
        primitive.put("admin_level", "8");
        primitive.put("type", "boundary");
        primitive.put("name", city.getName());
        primitive.put("ref:gemeentecode", city.getReferenceId().toString());
    }
}
