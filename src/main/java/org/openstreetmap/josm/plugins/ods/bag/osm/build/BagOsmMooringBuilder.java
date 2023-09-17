package org.openstreetmap.josm.plugins.ods.bag.osm.build;

import java.util.Map;

import org.locationtech.jts.geom.Geometry;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.data.osm.OsmPrimitiveType;
import org.openstreetmap.josm.data.osm.Way;
import org.openstreetmap.josm.plugins.ods.bag.entity.osm.BagOsmAddress;
import org.openstreetmap.josm.plugins.ods.bag.entity.osm.OsmBagMooring;
import org.openstreetmap.josm.plugins.ods.bag.entity.osm.OsmBagMooringImpl;
import org.openstreetmap.josm.plugins.ods.bag.entity.osm.OsmBagMooringStore;
import org.openstreetmap.josm.plugins.ods.context.OdsContext;
import org.openstreetmap.josm.plugins.ods.entities.osm.AbstractOsmEntityBuilder;

public class BagOsmMooringBuilder extends AbstractOsmEntityBuilder<OsmBagMooring> {

    private final OdsContext context;

    public BagOsmMooringBuilder(OdsContext context) {
        super(context, OsmBagMooring.class);
        this.context = context;
    }

    private static boolean canHandle(OsmPrimitive primitive) {
        boolean taggedAsMooring = primitive.hasKey("mooring") && primitive.hasKey("ref:bag");
        boolean validGeometry = (primitive.getDisplayType() == OsmPrimitiveType.CLOSEDWAY);
        return taggedAsMooring && validGeometry;
    }

    @Override
    public void buildOsmEntity(OsmPrimitive primitive) {
        if (canHandle(primitive)) {
            if (!getEntityStore().contains(primitive.getId())) {
                normalizeTags(primitive);
                OsmBagMooringImpl mooring = new OsmBagMooringImpl();
                Map<String, String> tags = primitive.getKeys();
                parseKeys(mooring, tags);
                mooring.setOtherTags(tags);

                Geometry geometry = buildGeometry(primitive);
                mooring.setGeometry(geometry);
                register(primitive, mooring);
            }
        }
        return;
    }

    public static void normalizeTags(OsmPrimitive primitive) {
        BagOsmEntityBuilder.normalizeTags(primitive);
    }

    private static void parseKeys(OsmBagMooringImpl mooring, Map<String, String> tags) {
        BagOsmEntityBuilder.parseKeys(mooring, tags);
        Long bagId = BagOsmEntityBuilder.getReferenceId(tags.remove("ref:bag"));
        mooring.setMooringId(bagId);
        tags.remove("mooring");
        if (tags.containsKey("addr:housenumber")) {
            BagOsmAddress address = new BagOsmAddress();
            BagOsmAddressEntityBuilder.parseKeys(address, tags);
            mooring.setAddress(address);
        }
        return;
    }

    private Geometry buildGeometry(OsmPrimitive primitive) {
        if (primitive.getDisplayType() == OsmPrimitiveType.CLOSEDWAY) {
            return buildGeometry((Way)primitive);
        }
        return null;
    }

    private Geometry buildGeometry(Way way) throws IllegalArgumentException {
        return getGeoUtil().toPolygon(way);
    }

    @Override
    public OsmBagMooringStore getEntityStore() {
        return context.getComponent(OsmBagMooringStore.class);
    }
}
