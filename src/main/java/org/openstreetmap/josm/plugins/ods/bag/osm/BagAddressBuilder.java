package org.openstreetmap.josm.plugins.ods.bag.osm;

import org.openstreetmap.josm.plugins.ods.bag.BagAddress;

public class BagAddressBuilder {
    private String street;
    private String houseNumber;
    private String postcode;
    private String city;
    private String country;
    private boolean changed;
    
    public void reset() {
        street = null;
        houseNumber = null;
        postcode = null;
        city = null;
        country = null;
        changed = false;
    }

    public boolean parseKey(String key, String value) {
        if ("addr:housenumber".equals(key)) {
            houseNumber = value;
            changed = true;
            return true;
        }
        if ("addr:postcode".equals(key)) {
            postcode = value;
            changed = true;
            return true;
        }
        if ("addr:street".equals(key)) {
            street = value;
            changed = true;
            return true;
        }
        if ("addr:city".equals(key)) {
            city = value;
            changed = true;
            return true;
        }
        if ("addr:country".equals(key)) {
            country = value;
            changed = true;
            return true;
        }
        return false;
    }
    
    public boolean active() {
        return changed;
    }
    
    public BagAddress getAddress() {
        BagAddress address = new BagAddress();
        address.setStreetName(street);
        address.setHouseNumber(houseNumber);
        address.setPostcode(postcode);
        address.setCityName(city);
        return address;
    }
}
