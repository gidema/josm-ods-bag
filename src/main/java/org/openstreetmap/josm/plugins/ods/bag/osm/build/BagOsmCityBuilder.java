package org.openstreetmap.josm.plugins.ods.bag.osm.build;

import java.util.Map;

import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.data.osm.OsmPrimitiveType;
import org.openstreetmap.josm.plugins.ods.bag.entity.osm.BagOsmCity;
import org.openstreetmap.josm.plugins.ods.bag.entity.osm.OsmCityStore;
import org.openstreetmap.josm.plugins.ods.domains.places.OsmCity;
import org.openstreetmap.josm.plugins.ods.entities.osm.OsmEntityBuilder;
import org.openstreetmap.josm.plugins.ods.jts.GeoUtil;

public class BagOsmCityBuilder implements OsmEntityBuilder<OsmCity> {

    @SuppressWarnings("unused")
    private final GeoUtil geoUtil;
    private final OsmCityStore cityStore;

    public BagOsmCityBuilder(GeoUtil geoUtil, OsmCityStore store) {
        super();
        this.geoUtil = geoUtil;
        this.cityStore = store;
    }

    @Override
    public Class<OsmCity> getEntityClass() {
        return OsmCity.class;
    }

    private boolean canHandle(OsmPrimitive primitive) {
        boolean validTagging = "administrative".equals(primitive.get("boundary"))
            && "10".equals(primitive.get("admin_level"));
        boolean validGeometry = primitive.getType().equals(OsmPrimitiveType.RELATION)
            || primitive.getType().equals(OsmPrimitiveType.CLOSEDWAY);
        return validTagging && validGeometry;
    }

    @Override
    public void buildOsmEntity(OsmPrimitive primitive) {
        if (canHandle(primitive)) {
            normalizeKeys(primitive);
            BagOsmCity city = new BagOsmCity();
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

    private static void parseKeys(BagOsmCity city, Map<String, String> tags) {
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
