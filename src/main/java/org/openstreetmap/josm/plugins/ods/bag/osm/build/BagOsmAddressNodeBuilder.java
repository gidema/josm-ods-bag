package org.openstreetmap.josm.plugins.ods.bag.osm.build;

import java.util.Map;

import org.locationtech.jts.geom.Point;
import org.openstreetmap.josm.data.osm.Node;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.data.osm.OsmPrimitiveType;
import org.openstreetmap.josm.plugins.ods.bag.entity.osm.OsmNlAddressNode;
import org.openstreetmap.josm.plugins.ods.bag.entity.osm.BagOsmAddress;
import org.openstreetmap.josm.plugins.ods.bag.entity.osm.OsmAddress;
import org.openstreetmap.josm.plugins.ods.bag.entity.osm.OsmAddressNode;
import org.openstreetmap.josm.plugins.ods.bag.entity.osm.OsmAddressNodeStore;
import org.openstreetmap.josm.plugins.ods.context.OdsContext;
import org.openstreetmap.josm.plugins.ods.entities.osm.AbstractOsmEntityBuilder;

public class BagOsmAddressNodeBuilder extends AbstractOsmEntityBuilder<OsmAddressNode> {

    private final OdsContext context;

    public BagOsmAddressNodeBuilder(OdsContext context) {
        super(context, OsmAddressNode.class);
        this.context = context;
    }

    @Override
    public Class<OsmAddressNode> getEntityClass() {
        return OsmAddressNode.class;
    }

    private static boolean canHandle(OsmPrimitive primitive) {
        boolean validTagging = primitive.hasKey("addr:housenumber");
        boolean validGeometry = primitive.getDisplayType() == OsmPrimitiveType.NODE;
        return validTagging && validGeometry;
    }

    @Override
    public void buildOsmEntity(OsmPrimitive primitive) {
        if (canHandle(primitive)) {
            if (!getEntityStore().contains(primitive.getId())) {
                normalizeKeys(primitive);
                OsmAddress address = new BagOsmAddress();
                OsmNlAddressNode addressNode = new OsmNlAddressNode();
                addressNode.setPrimitive(primitive);
                addressNode.setAddress(address);
                Map<String, String> tags = primitive.getKeys();
                BagOsmAddressEntityBuilder.parseKeys(address, tags);
                parseKeys(addressNode, tags);
                addressNode.setOtherTags(tags);
                addressNode.setGeometry(buildGeometry(primitive));
                register(primitive, addressNode);
            }
        }
        return;
    }

    public static void normalizeKeys(OsmPrimitive primitive) {
        BagOsmEntityBuilder.normalizeTags(primitive);
    }

    private static void parseKeys(OsmNlAddressNode addressNode, Map<String, String> tags) {
        BagOsmEntityBuilder.parseKeys(addressNode, tags);
        addressNode.setAddressableId(BagOsmEntityBuilder.getReferenceId(tags.remove("ref:bag")));

    }

    private Point buildGeometry(OsmPrimitive primitive) {
        if (primitive.getDisplayType() == OsmPrimitiveType.NODE) {
            return getGeoUtil().toPoint((Node) primitive);
        }
        return null;
    }

    @Override
    public OsmAddressNodeStore getEntityStore() {
        return context.getComponent(OsmAddressNodeStore.class);
    }
}
