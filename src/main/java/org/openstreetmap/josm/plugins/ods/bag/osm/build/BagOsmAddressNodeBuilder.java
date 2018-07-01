package org.openstreetmap.josm.plugins.ods.bag.osm.build;

import java.util.Map;

import org.openstreetmap.josm.data.osm.Node;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.data.osm.OsmPrimitiveType;
import org.openstreetmap.josm.plugins.ods.bag.entity.BagOsmAddress;
import org.openstreetmap.josm.plugins.ods.domains.buildings.OsmAddress;
import org.openstreetmap.josm.plugins.ods.domains.buildings.OsmAddressNode;
import org.openstreetmap.josm.plugins.ods.domains.buildings.impl.BaseOsmAddressNode;
import org.openstreetmap.josm.plugins.ods.domains.buildings.impl.OsmAddressNodeStore;
import org.openstreetmap.josm.plugins.ods.entities.osm.AbstractOsmEntityBuilder;
import org.openstreetmap.josm.plugins.ods.entities.osm.OsmLayerManager;
import org.openstreetmap.josm.plugins.ods.jts.GeoUtil;

import com.vividsolutions.jts.geom.Point;

public class BagOsmAddressNodeBuilder extends AbstractOsmEntityBuilder<OsmAddressNode> {
    private final OsmAddressNodeStore addressNodeStore;

    public BagOsmAddressNodeBuilder(OsmLayerManager layerManager,
            OsmAddressNodeStore addressNodeStore, GeoUtil geoUtil) {
        super(layerManager, addressNodeStore, geoUtil);
        this.addressNodeStore = addressNodeStore;
    }

    @Override
    public boolean canHandle(OsmPrimitive primitive) {
        return OsmAddressNode.IsAddressNode(primitive);
    }

    @Override
    public void buildOsmEntity(OsmPrimitive primitive) {
        if (canHandle(primitive)) {
            if (!addressNodeStore.contains(primitive.getId())) {
                normalizeKeys(primitive);
                OsmAddress address = new BagOsmAddress();
                BaseOsmAddressNode addressNode = new BaseOsmAddressNode();
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

    private static void parseKeys(BaseOsmAddressNode addressNode, Map<String, String> tags) {
        BagOsmEntityBuilder.parseKeys(addressNode, tags);
    }

    private Point buildGeometry(OsmPrimitive primitive) {
        if (primitive.getDisplayType() == OsmPrimitiveType.NODE) {
            return getGeoUtil().toPoint((Node) primitive);
        }
        return null;
    }
}
