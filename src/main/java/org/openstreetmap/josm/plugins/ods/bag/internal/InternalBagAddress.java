package org.openstreetmap.josm.plugins.ods.bag.internal;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.plugins.ods.bag.BagAddressDataImpl;

public class InternalBagAddress extends BagAddressDataImpl {
    private OsmPrimitive primitive;
    
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
            setHouseNumber(value);
            return true;
        }
        if ("addr:postcode".equals(key)) {
            setPostcode(value);
            return true;
        }
        if ("addr:street".equals(key)) {
            setStreetName(value);
            return true;
        }
        if ("addr:city".equals(key)) {
            setCityName(value);
            return true;
        }
        if ("addr:housename".equals(key)) {
            setHouseName(value);
            return true;
        }
        return false;
    }
}
