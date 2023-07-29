package org.openstreetmap.josm.plugins.ods.bag.osm.build;

import java.util.Map;

import org.locationtech.jts.geom.Geometry;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.data.osm.OsmPrimitiveType;
import org.openstreetmap.josm.data.osm.Way;
import org.openstreetmap.josm.plugins.ods.bag.entity.osm.BagOsmAddress;
import org.openstreetmap.josm.plugins.ods.bag.entity.osm.OsmBagStaticCaravanPlot;
import org.openstreetmap.josm.plugins.ods.bag.entity.osm.OsmBagStaticCaravanPlotImpl;
import org.openstreetmap.josm.plugins.ods.bag.entity.osm.OsmBagStaticCaravanPlotStore;
import org.openstreetmap.josm.plugins.ods.context.OdsContext;
import org.openstreetmap.josm.plugins.ods.entities.osm.AbstractOsmEntityBuilder;
import org.openstreetmap.josm.plugins.ods.entities.storage.AbstractGeoEntityStore;

/**
 * Static caravan plots (Dutch:Standplaats) can be tagged with landuse=static_caravan and/or
 * (place=plot + plot=static_caravan) in OSM. 
 * 
 * @author gertjan
 *
 */
public class BagOsmStaticCaravanPlotBuilder extends AbstractOsmEntityBuilder<OsmBagStaticCaravanPlot> {

    private final OdsContext context;

    public BagOsmStaticCaravanPlotBuilder(OdsContext context) {
        super(context, OsmBagStaticCaravanPlot.class);
        this.context = context;
    }

    private static boolean canHandle(OsmPrimitive primitive) {
        boolean taggedAsStaticCaravanPlot = primitive.hasKey("ref:bag") && (
            "static_caravan".equals(primitive.get("landuse")) || 
            ("plot".equals(primitive.get("place")) && "static_caravan".equals(primitive.get("plot"))));
        boolean validGeometry = (primitive.getDisplayType() == OsmPrimitiveType.CLOSEDWAY);
        return taggedAsStaticCaravanPlot && validGeometry;
    }

    @Override
    public void buildOsmEntity(OsmPrimitive primitive) {
        if (canHandle(primitive)) {
            if (!getEntityStore().contains(primitive.getId())) {
                normalizeTags(primitive);
                OsmBagStaticCaravanPlotImpl landuse = new OsmBagStaticCaravanPlotImpl();
                Map<String, String> tags = primitive.getKeys();
                parseKeys(landuse, tags);
                landuse.setOtherTags(tags);

                Geometry geometry = buildGeometry(primitive);
                landuse.setGeometry(geometry);
                register(primitive, landuse);
            }
        }
        return;
    }

    public static void normalizeTags(OsmPrimitive primitive) {
        BagOsmEntityBuilder.normalizeTags(primitive);
    }

    private static void parseKeys(OsmBagStaticCaravanPlotImpl landuse, Map<String, String> tags) {
        BagOsmEntityBuilder.parseKeys(landuse, tags);
        Long bagId = BagOsmEntityBuilder.getReferenceId(tags.remove("ref:bag"));
        landuse.setBagId(bagId);
        tags.remove("landuse");
        if (tags.containsKey("addr:housenumber")) {
            BagOsmAddress address = new BagOsmAddress();
            BagOsmAddressEntityBuilder.parseKeys(address, tags);
            landuse.setAddress(address);
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
    public AbstractGeoEntityStore<OsmBagStaticCaravanPlot> getEntityStore() {
        return context.getComponent(OsmBagStaticCaravanPlotStore.class);
    }
}
