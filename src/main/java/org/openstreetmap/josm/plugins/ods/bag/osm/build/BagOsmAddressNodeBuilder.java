package org.openstreetmap.josm.plugins.ods.bag.osm.build;

import java.util.Map;

import org.openstreetmap.josm.data.osm.Node;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.data.osm.OsmPrimitiveType;
import org.openstreetmap.josm.plugins.ods.bag.entity.BagAddress;
import org.openstreetmap.josm.plugins.ods.bag.entity.BagAddressNode;
import org.openstreetmap.josm.plugins.ods.entities.builtenvironment.AddressNode;
import org.openstreetmap.josm.plugins.ods.entities.builtenvironment.OsmAddressNodeStore;
import org.openstreetmap.josm.plugins.ods.entities.internal.OsmEntityBuilder;
import org.openstreetmap.josm.plugins.ods.jts.GeoUtil;
import org.openstreetmap.josm.plugins.ods.osm.build.OsmAddressEntityBuilder;

import com.vividsolutions.jts.geom.Point;

public class BagOsmAddressNodeBuilder implements OsmEntityBuilder<AddressNode> {

    private GeoUtil geoUtil;
    private OsmAddressNodeStore addressNodeStore;
    
    public BagOsmAddressNodeBuilder(GeoUtil geoUtil, OsmAddressNodeStore store) {
        super();
        this.geoUtil = geoUtil;
        this.addressNodeStore = store;
    }

    @Override
    public void buildOsmEntity(OsmPrimitive primitive) {
        if (primitive.hasKey("addr:housenumber") &&
                (primitive.getDisplayType() == OsmPrimitiveType.NODE)) {
            normalizeKeys(primitive);
            BagAddress address = new BagAddress();
            BagAddressNode addressNode = new BagAddressNode(address);
            Map<String, String> tags = primitive.getKeys();
            OsmAddressEntityBuilder.parseKeys(address, tags);
            parseKeys(addressNode, tags);
            addressNode.setOtherTags(tags);
            addressNode.setGeometry(buildGeometry(primitive));
            addressNodeStore.add(addressNode);
        }
        return;
    }
    
    public static void normalizeKeys(OsmPrimitive primitive) {
        BagOsmEntityBuilder.normalizeKeys(primitive);
    }
    
    private static void parseKeys(BagAddressNode addressNode, Map<String, String> tags) {
        BagOsmEntityBuilder.parseKeys(addressNode, tags); 
    }
    
    private Point buildGeometry(OsmPrimitive primitive) {
        if (primitive.getDisplayType() == OsmPrimitiveType.NODE) {
            return geoUtil.toPoint((Node) primitive);
        }
        return null;
    }
}
