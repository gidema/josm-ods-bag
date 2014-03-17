package org.openstreetmap.josm.plugins.ods.bag.osm;

import org.openstreetmap.josm.data.osm.Node;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.data.osm.OsmPrimitiveType;
import org.openstreetmap.josm.plugins.ods.bag.BagAddress;
import org.openstreetmap.josm.plugins.ods.bag.BagAddressNode;
import org.openstreetmap.josm.plugins.ods.crs.InvalidGeometryException;
import org.openstreetmap.josm.plugins.ods.jts.GeoUtil;
import org.openstreetmap.josm.plugins.ods.metadata.MetaData;

import com.vividsolutions.jts.geom.Geometry;

public class BagAddressNodeBuilder extends BagEntityBuilder<BagAddressNode> {
    private static final GeoUtil geoUtil = GeoUtil.getInstance();
    private BagAddressBuilder addressBuilder = new BagAddressBuilder();

    @Override
    public BagAddressNode build(OsmPrimitive primitive, MetaData metaData) {
        BagAddressNode addressNode = super.build(primitive, metaData);
        BagAddress address = addressBuilder.getAddress();
        checkAssociatedStreet(address, primitive);
        addressNode.setAddress(address);
        return addressNode;
    }

    @Override
    protected BagAddressNode createEntity() {
        addressBuilder.reset();
        return new BagAddressNode();
    }

    @Override
    protected boolean parseKey(BagAddressNode addressNode, String key, String value) {
        if (super.parseKey(addressNode, key, value)) {
            return true;
        }
        if (addressBuilder.parseKey(key, value)) {
            return true;
        }
        return false;
    }

    @Override
    protected Geometry buildGeometry(OsmPrimitive primitive)
            throws InvalidGeometryException {
        if (primitive.getType() == OsmPrimitiveType.NODE) {
            return geoUtil.toPoint((Node)primitive);
        }
        return null;
    }
    
    /**
     * Check if this address is part of an associated street relation.
     * If so, update the relevant address attributes.
     * 
     * @param address
     * @param primitive
     */
    private static void checkAssociatedStreet(BagAddress address, OsmPrimitive primitive) {
        for (OsmPrimitive referrer :primitive.getReferrers()) {
            if (referrer.getType() == OsmPrimitiveType.RELATION &&
                "associatedStreet".equals(referrer.get("type"))) {
                if (address.getStreetName() == null) {
                    address.setStreetName(referrer.get("addr:street"));
                }
                if (address.getPostcode() == null) {
                    address.setPostcode(referrer.get("addr:postcode"));
                }
                if (address.getCityName() == null) {
                    address.setCityName(referrer.get("addr:city"));
                }
            }
        }
    }
}
