package org.openstreetmap.josm.plugins.ods.bag.gt.parsing;

import org.opengis.feature.simple.SimpleFeature;
import org.openstreetmap.josm.plugins.ods.bag.entity.BagOdAddress;
import org.openstreetmap.josm.plugins.ods.domains.buildings.OdAddress;
import org.openstreetmap.josm.plugins.ods.entities.opendata.FeatureUtil;

public class DuinoordAddressParser {
    public static OdAddress parseAddress(SimpleFeature feature) {
        BagOdAddress address = new BagOdAddress();
        address.setHouseNumber(FeatureUtil.getInteger(feature, "huisnummer"));
        String houseLetter = FeatureUtil.getString(feature, "huisletter");
        if (houseLetter != null) {
            address.setHuisletter(houseLetter);
            address.setHouseLetter(houseLetter.charAt(0));
        }
        String houseNumberExtra = FeatureUtil.getString(feature, "huisnummertoevoeging");
        address.setHuisnummerToevoeging(houseNumberExtra);
        address.setHouseNumberExtra(houseNumberExtra);
        address.setStreetName(FeatureUtil.getString(feature, "straat"));
        address.setCityName(FeatureUtil.getString(feature, "woonplaats"));
        address.setPostcode(FeatureUtil.getString(feature, "postcode"));
        return address;
    }

}
