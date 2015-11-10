package org.openstreetmap.josm.plugins.ods.bag.osm.build;

import java.util.Map;

import org.openstreetmap.josm.plugins.ods.entities.actual.Address;

public class AddressPrimitiveBuilder {
    public static void buildTags(Address address, Map<String, String> tags) {
        if (address.getStreetName() != null) {
          tags.put("addr:street", address.getStreetName());
        }
        if (address.getFullHouseNumber() != null) {
            tags.put("addr:housenumber", address.getFullHouseNumber());
        }
        if (address.getPostcode() != null) {
          tags.put("addr:postcode", address.getPostcode());
        }
        if (address.getCityName() != null) {
          tags.put("addr:city", address.getCityName());
        }
    }
}
