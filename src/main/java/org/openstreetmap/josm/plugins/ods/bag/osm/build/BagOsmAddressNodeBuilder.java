package org.openstreetmap.josm.plugins.ods.bag.osm.build;

import java.util.Map;

import org.openstreetmap.josm.data.osm.Node;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.data.osm.OsmPrimitiveType;
import org.openstreetmap.josm.plugins.ods.OdsModule;
import org.openstreetmap.josm.plugins.ods.bag.entity.BagOsmAddress;
import org.openstreetmap.josm.plugins.ods.domains.buildings.OsmAddress;
import org.openstreetmap.josm.plugins.ods.domains.buildings.OsmAddressNode;
import org.openstreetmap.josm.plugins.ods.domains.buildings.impl.BaseOsmAddressNode;
import org.openstreetmap.josm.plugins.ods.entities.osm.AbstractOsmEntityBuilder;

import com.vividsolutions.jts.geom.Point;

public class BagOsmAddressNodeBuilder extends AbstractOsmEntityBuilder<OsmAddressNode> {

    public BagOsmAddressNodeBuilder(OdsModule module) {
        super(module, OsmAddressNode.class);
    }

    @Override
    public Class<OsmAddressNode> getEntityClass() {
        return OsmAddressNode.class;
    }

    @Override
    public boolean canHandle(OsmPrimitive primitive) {
        return OsmAddressNode.IsAddressNode(primitive);
    }

    @Override
    public void buildOsmEntity(OsmPrimitive primitive) {
        if (canHandle(primitive)) {
            if (!getEntityStore().contains(primitive.getId())) {
                normalizeKeys(primitive);
                OsmAddress address = new BagOsmAddress();
                BaseOsmAddressNode addressNode = new BaseOsmAddressNode();
                addressNode.setPrimaryId(primitive.getUniqueId());
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
