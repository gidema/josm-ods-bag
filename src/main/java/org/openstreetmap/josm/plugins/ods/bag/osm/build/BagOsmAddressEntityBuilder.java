package org.openstreetmap.josm.plugins.ods.bag.osm.build;

import java.util.Map;

import org.openstreetmap.josm.plugins.ods.bag.entity.NL_HouseNumber;
import org.openstreetmap.josm.plugins.ods.bag.entity.NL_HouseNumberImpl;
import org.openstreetmap.josm.plugins.ods.domains.buildings.OsmAddress;

/**
 * TODO Replace this with a factory solution
 * 
 * @author gertjan
 *
 */
public class BagOsmAddressEntityBuilder extends BagOsmEntityBuilder {

    public static void parseKeys(OsmAddress address,
            Map<String, String> tags) {
        String fullHouseNumber = tags.get("addr:housenumber");
        NL_HouseNumber houseNumber = new NL_HouseNumberImpl(fullHouseNumber);
        address.setHouseNumber(houseNumber);
        address.setStreetName(tags.get("addr:street"));
        address.setPostcode(tags.get("addr:postcode"));
        address.setCityName(tags.get("addr:city"));
        //address.setCountry(tags.get("addr:country"));
    }
}
