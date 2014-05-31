package org.openstreetmap.josm.plugins.ods.bag.osm;

import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.plugins.ods.bag.BagAddress;
import org.openstreetmap.josm.tools.I18n;

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
        parseHouseNumber(address);
        return address;
    }
    
    /**
     * Parse a housenumber and try to split it up in the three
     * BAG address parts (huisnummer, huisletter and huisnummertoevoeging)
     * @param address
     */
    private static void parseHouseNumber(BagAddress address) {
        String number = address.getHouseNumber().trim();
        // Parse the numeric part
        int s=0; // start index
        int e=s; // end index
        while (e<number.length() && Character.isDigit(number.charAt(e))) {
            e++;
        }
        if (e < 1) {
            // TODO This housenumber has no numeric part. How do we report this?
            Main.info(I18n.tr("Housenumber without a numeric part: {0}", number));
            //this.setHuisnummer(null);
        }
        else {
            address.setHuisnummer(Integer.parseInt(number.substring(0, e)));
        }
        s = e;
        if (s >= number.length()) return;
        if (Character.isLetter(number.charAt(s))) {
            address.setHuisletter(Character.toString(number.charAt(s)));
            s++;
        }
        if (s >= number.length()) return;
        // Skip any space, - or _ character
        char c = number.charAt(s);
        while (s < number.length() && (c==' ' | c=='-' | c=='_')) {
            s++;
            c = number.charAt(s);
        }
        address.setHuisnummerToevoeging(number.substring(s));
    }

}
