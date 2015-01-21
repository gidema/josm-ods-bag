package org.openstreetmap.josm.plugins.ods.bag.entity;

import org.apache.commons.lang.ObjectUtils;
import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.plugins.ods.entities.builtenvironment.Address;
import org.openstreetmap.josm.plugins.ods.entities.builtenvironment.AddressImpl;
import org.openstreetmap.josm.tools.I18n;

public class BagAddress extends AddressImpl {
    private String huisletter;
    private String huisnummertoevoeging;

    public void setHuisletter(String huisletter) {
        this.huisletter = huisletter;
    }

    public String getHuisLetter() {
        return huisletter;
    }

    public void setHuisnummerToevoeging(String toevoeging) {
        this.huisnummertoevoeging = toevoeging;
    }

    public String getHuisNummerToevoeging() {
        return huisnummertoevoeging;
    }
    
    @Override
    public String formatHouseNumber() {
        StringBuilder sb = new StringBuilder(10);
        sb.append(getHouseNumber());
        if (getHuisLetter() != null) {
            sb.append(getHuisLetter());
        }
        if (getHuisNummerToevoeging() != null) {
            sb.append('-').append(getHuisNummerToevoeging());
        }
        return sb.toString();
    }

    @Override
    public void parseHouseNumber() {
        String number = getFullHouseNumber().trim();
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
            setHouseNumber(Integer.parseInt(number.substring(0, e)));
        }
        s = e;
        if (s >= number.length()) return;
        if (Character.isLetter(number.charAt(s))) {
            setHuisletter(Character.toString(number.charAt(s)));
            s++;
        }
        if (s >= number.length()) return;
        // Skip any space, - or _ character
        char c = number.charAt(s);
        while (s < number.length() && (c==' ' | c=='-' | c=='_')) {
            s++;
            c = number.charAt(s);
        }
        setHuisnummerToevoeging(number.substring(s));
    }

    @Override
    public int compareTo(Address a) {
        int result = ObjectUtils.compare(getCityName(), a.getCityName());
        if (result != 0) return result;
        result = ObjectUtils.compare(getPostcode(), a.getPostcode());
        if (result != 0) return result;
        result = ObjectUtils.compare(getStreetName(), a.getStreetName());
        if (result != 0) return result;
        return getFullHouseNumber().compareTo(a.getFullHouseNumber());
    }
}
