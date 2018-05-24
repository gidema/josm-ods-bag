package org.openstreetmap.josm.plugins.ods.bag.osm.build;

import java.util.Map;

import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.data.osm.OsmPrimitiveType;
import org.openstreetmap.josm.plugins.ods.bag.entity.BagCity;
import org.openstreetmap.josm.plugins.ods.entities.actual.City;
import org.openstreetmap.josm.plugins.ods.entities.actual.impl.osm.OsmCityStore;
import org.openstreetmap.josm.plugins.ods.entities.osm.OsmEntityBuilder;
import org.openstreetmap.josm.plugins.ods.jts.GeoUtil;

public class BagOsmCityBuilder implements OsmEntityBuilder<City> {

    @SuppressWarnings("unused")
    private final GeoUtil geoUtil;
    private final OsmCityStore cityStore;

    public BagOsmCityBuilder(GeoUtil geoUtil, OsmCityStore store) {
        super();
        this.geoUtil = geoUtil;
        this.cityStore = store;
    }

    @Override
    public Class<City> getEntityClass() {
        return City.class;
    }

    @Override
    public boolean canHandle(OsmPrimitive primitive) {
        return City.isCity(primitive);
    }

    @Override
    public void buildOsmEntity(OsmPrimitive primitive) {
        if (primitive.getType() == OsmPrimitiveType.RELATION &&
                "administrative".equals(primitive.get("boundary")) &&
                "8".equals(primitive.get("admin_level"))) {
            normalizeKeys(primitive);
            BagCity city = new BagCity();
            Map<String, String> tags = primitive.getKeys();
            parseKeys(city, tags);
            city.setOtherTags(tags);
            cityStore.add(city);
        }
        return;
    }

    public static void normalizeKeys(OsmPrimitive primitive) {
        if ("multipolygon".equals(primitive.get("type"))) {
            primitive.put("type", "boundary");
        }
    }

    private static void parseKeys(BagCity city, Map<String, String> tags) {
        tags.remove("boundary");
        tags.remove("admin_level");
        tags.remove("type");
        city.setName(tags.remove("name"));
        try {
            city.setIdentificatie(Long.parseLong((tags.get("ref:gemeentecode"))));
            tags.remove("ref:gemeentecode");
        }
        catch (NumberFormatException e) {
            // Do nothing, but keep the invalid tag
        }
    }
}
