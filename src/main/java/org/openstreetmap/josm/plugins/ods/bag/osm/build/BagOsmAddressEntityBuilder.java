package org.openstreetmap.josm.plugins.ods.bag.osm.build;

import java.util.Map;

import org.openstreetmap.josm.plugins.ods.domains.buildings.OsmAddress;

public class BagOsmAddressEntityBuilder extends BagOsmEntityBuilder {

    public static void parseKeys(OsmAddress address,
            Map<String, String> tags) {
        address.setFullHouseNumber(tags.get("addr:housenumber"));
        address.setStreetName(tags.get("addr:street"));
        address.setPostcode(tags.get("addr:postcode"));
        address.setCityName(tags.get("addr:city"));
        //address.setCountry(tags.get("addr:country"));
    }
}
