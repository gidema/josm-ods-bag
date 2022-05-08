package org.openstreetmap.josm.plugins.ods.bag.osm.build;

import java.util.Map;

import org.openstreetmap.josm.plugins.ods.bag.entity.BagWoonplaats;
import org.openstreetmap.josm.plugins.ods.context.OdsContext;

public class BagCityPrimitiveBuilder extends BagEntityPrimitiveBuilder<BagWoonplaats> {

    public BagCityPrimitiveBuilder() {
        super();
    }

    @Override
    public void run(OdsContext context) {
        // TODO Auto-generated method stub
    }

    @Override
    protected void buildTags(BagWoonplaats city, Map<String, String> tags) {
        tags.put("source", "BAG");
        tags.put("source:date", city.getSourceDate());
        tags.put("boundary",  "administrative");
        tags.put("admin_level", "10");
        tags.put("type", "boundary");
        tags.put("name", city.getName());
        tags.put("ref:woonplaatscode", city.getWoonplaatsId().toString());
    }
}
