package org.openstreetmap.josm.plugins.ods.bag.osm;

import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.plugins.ods.bag.BagAddress;

public class BagAddressPrimitiveBuilder {
    // TODO nothing BAG-specific here. Move to a higher level?
    public static void buildTags(BagAddress address, OsmPrimitive primitive) {
        primitive.put("addr:housenumber", address.getHouseNumber());
        primitive.put("addr:street", address.getStreetName());
        primitive.put("addr:postcode", address.getPostcode());
        primitive.put("addr:city", address.getCityName());
    }
}
