package org.openstreetmap.josm.plugins.ods.bag.osm.build;

import java.util.Map;

import org.openstreetmap.josm.data.osm.Node;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.data.osm.OsmPrimitiveType;
import org.openstreetmap.josm.plugins.ods.OdsModule;
import org.openstreetmap.josm.plugins.ods.bag.entity.BagAddress;
import org.openstreetmap.josm.plugins.ods.bag.entity.BagAddressNode;
import org.openstreetmap.josm.plugins.ods.entities.actual.AddressNode;
import org.openstreetmap.josm.plugins.ods.entities.actual.MutableAddress;
import org.openstreetmap.josm.plugins.ods.entities.osm.AbstractOsmEntityBuilder;

import com.vividsolutions.jts.geom.Point;

public class BagOsmAddressNodeBuilder extends AbstractOsmEntityBuilder<AddressNode> {
    
    public BagOsmAddressNodeBuilder(OdsModule module) {
        super(module, AddressNode.class);
    }

    @Override
    public void buildOsmEntity(OsmPrimitive primitive) {
        if (primitive.hasKey("addr:housenumber") &&
                (primitive.getDisplayType() == OsmPrimitiveType.NODE)) {
            if (!getEntityStore().contains(primitive.getId())) {
                normalizeKeys(primitive);
                MutableAddress address = new BagAddress();
                BagAddressNode addressNode = new BagAddressNode();
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
    
    private static void parseKeys(BagAddressNode addressNode, Map<String, String> tags) {
        BagOsmEntityBuilder.parseKeys(addressNode, tags); 
    }
    
    private Point buildGeometry(OsmPrimitive primitive) {
        if (primitive.getDisplayType() == OsmPrimitiveType.NODE) {
            return getGeoUtil().toPoint((Node) primitive);
        }
        return null;
    }
}
