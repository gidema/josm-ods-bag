package org.openstreetmap.josm.plugins.ods.bag.gt;

import org.opengis.feature.simple.SimpleFeature;
import org.openstreetmap.josm.plugins.ods.bag.BagAddress;
import org.openstreetmap.josm.plugins.ods.entities.external.FeatureUtil;

public class BagAddressBuilder {
    public static BagAddress build(SimpleFeature feature) {
        BagAddress address = new BagAddress();
//        identificatie = FeatureUtil.getLong(feature, "identificatie");
        address.setHuisnummer(FeatureUtil.getInteger(feature, "huisnummer"));
        address.setHuisletter(FeatureUtil.getString(feature, "huisletter"));
        address.setHuisnummerToevoeging(FeatureUtil.getString(feature, "toevoeging"));
//        status = FeatureUtil.getString(feature, "status");
        address.setStreetName(FeatureUtil.getString(feature, "openbare_ruimte"));
        address.setCityName(FeatureUtil.getString(feature, "woonplaats"));
        address.setPostcode(FeatureUtil.getString(feature, "postcode"));
        return address;
    }
}
