package org.openstreetmap.josm.plugins.ods.bag.osm.build;

import java.util.Map;

import org.openstreetmap.josm.data.osm.Node;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.data.osm.OsmPrimitiveType;
import org.openstreetmap.josm.plugins.ods.bag.entity.BagAddress;
import org.openstreetmap.josm.plugins.ods.bag.entity.BagAddressNode;
import org.openstreetmap.josm.plugins.ods.entities.EntityStore;
import org.openstreetmap.josm.plugins.ods.entities.actual.AddressNode;
import org.openstreetmap.josm.plugins.ods.entities.actual.MutableAddress;
import org.openstreetmap.josm.plugins.ods.entities.osm.OsmEntityBuilder;
import org.openstreetmap.josm.plugins.ods.jts.GeoUtil;

import com.vividsolutions.jts.geom.Point;

public class BagOsmAddressNodeBuilder implements OsmEntityBuilder<AddressNode> {

    private GeoUtil geoUtil;
    private EntityStore<AddressNode> addressNodeStore;
    
    public BagOsmAddressNodeBuilder(GeoUtil geoUtil, EntityStore<AddressNode> addressNodeStore) {
        super();
        this.geoUtil = geoUtil;
        this.addressNodeStore = addressNodeStore;
    }

    @Override
    public void buildOsmEntity(OsmPrimitive primitive) {
        if (primitive.hasKey("addr:housenumber") &&
                (primitive.getDisplayType() == OsmPrimitiveType.NODE)) {
            if (!addressNodeStore.contains(primitive.getId())) {
                normalizeKeys(primitive);
                MutableAddress address = new BagAddress();
                BagAddressNode addressNode = new BagAddressNode();
                addressNode.setAddress(address);
                Map<String, String> tags = primitive.getKeys();
                BagOsmAddressEntityBuilder.parseKeys(address, tags);
                parseKeys(addressNode, tags);
                addressNode.setOtherTags(tags);
                addressNode.setGeometry(buildGeometry(primitive));
                addressNodeStore.add(addressNode);
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
            return geoUtil.toPoint((Node) primitive);
        }
        return null;
    }
}
