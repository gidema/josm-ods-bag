package org.openstreetmap.josm.plugins.ods.bag.osm.build;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openstreetmap.josm.plugins.ods.bag.entity.NlHouseNumber;
import org.openstreetmap.josm.plugins.ods.bag.entity.impl.NlHouseNumberImpl;
import org.openstreetmap.josm.plugins.ods.bag.entity.osm.OsmAddress;

/**
 * TODO Replace this with a factory solution
 * 
 * @author gertjan
 *
 */
public class BagOsmAddressEntityBuilder extends BagOsmEntityBuilder {
    protected final static Pattern houseNumberPattern = Pattern.compile("(\\D*)(\\d+)([a-zA-Z]?)((-?)(.*$))");

    public static void parseKeys(OsmAddress address,
            Map<String, String> tags) {
        String fullHouseNumber = tags.get("addr:housenumber");
        NlHouseNumber houseNumber = parseHouseNumber(fullHouseNumber);
        address.setHouseNumber(houseNumber);
        address.setStreetName(tags.get("addr:street"));
        address.setPostcode(tags.get("addr:postcode"));
        address.setCityName(tags.get("addr:city"));
        //address.setCountry(tags.get("addr:country"));
    }
    
    private static NlHouseNumber parseHouseNumber(String fullHouseNumber) {
        Matcher matcher = houseNumberPattern.matcher(fullHouseNumber);
        if (matcher.matches()) {
            String prefix = matcher.group(1).isEmpty() ? null : matcher.group(1);
            Integer houseNumber = Integer.valueOf(matcher.group(2));
            Character houseLetter = matcher.group(3).isEmpty() ? null : matcher.group(3).charAt(0);
            String houseNumberExtra = matcher.group(6).isEmpty() ? null : matcher.group(6);
            return new NlHouseNumberImpl(prefix, houseNumber, houseLetter, houseNumberExtra);
        }
        throw new RuntimeException("Unrecognized houseNumber: " + fullHouseNumber);
    }
}
