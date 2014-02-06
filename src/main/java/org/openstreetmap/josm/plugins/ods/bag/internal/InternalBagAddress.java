package org.openstreetmap.josm.plugins.ods.bag.internal;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.plugins.ods.entities.builtenvironment.Address;
import org.openstreetmap.josm.plugins.ods.entities.builtenvironment.City;
import org.openstreetmap.josm.plugins.ods.entities.builtenvironment.Street;

public class InternalBagAddress implements Address {
    private OsmPrimitive primitive;
    
    private String houseNumber;
    private String postcode;
    private String streetName;
    private String cityName;
    private String houseName;
    private Street street;
    
    public InternalBagAddress(OsmPrimitive primitive) {
        super();
        this.primitive = primitive;
    }

    public void build() {
        parseKeys();
    }
    
    private void parseKeys() {
        Map<String, String> keys = primitive.getKeys();
        Iterator<Entry<String, String>> it = 
            keys.entrySet().iterator();
        while (it.hasNext()) {
            Entry<String, String> entry =it.next();
            String key = entry.getKey();
            String value = entry.getValue();
            if (parseKey(key, value)) {
                it.remove();
            }
        }
    }
    
    private boolean parseKey(String key, String value) {
        if ("addr:housenumber".equals(key)) {
            houseNumber = value;
            return true;
        }
        if ("addr:postcode".equals(key)) {
            postcode = value;
            return true;
        }
        if ("addr:street".equals(key)) {
            streetName = value;
            return true;
        }
        if ("addr:city".equals(key)) {
            cityName = value;
            return true;
        }
        if ("addr:housename".equals(key)) {
            houseName = value;
            return true;
        }
        return false;
   }    

    @Override
    public City getCity() {
        return null;
    }

    @Override
    public String getStreetName() {
        return streetName;
    }

    @Override
    public Street getStreet() {
        return street;
    }

    @Override
    public String getPostcode() {
        return postcode;
    }

    @Override
    public String getHouseNumber() {
        return houseNumber;
    }

    @Override
    public String getHouseName() {
        return houseName;
    }

    @Override
    public String getPlaceName() {
        return cityName;
    }

    @Override
    public void setStreet(Street street) {
        this.street = street;
    }

    @Override
    public int compareTo(Address a) {
        int result = getPlaceName().compareTo(a.getPlaceName());
        if (result != 0) return result;
        result = getPostcode().compareTo(a.getPostcode());
        if (result != 0) return result;
        result = getStreetName().compareTo(a.getStreetName());
        if (result != 0) return result;
        return getHouseNumber().compareTo(a.getHouseNumber());
    }
}
