package org.openstreetmap.josm.plugins.ods.pdok.bag.wfs.v1_1;

import org.opengis.feature.simple.SimpleFeature;
import org.openstreetmap.josm.plugins.ods.bag.entity.NL_HouseNumber;
import org.openstreetmap.josm.plugins.ods.bag.entity.NL_HouseNumberImpl;
import org.openstreetmap.josm.plugins.ods.entities.opendata.FeatureUtil;

public class NL_HouseNumberFactory {
    public NL_HouseNumber create(SimpleFeature feature) {
        Integer number = Integer.valueOf(FeatureUtil.getString(feature, "huisnummer"));
        Character houseLetter = FeatureUtil.getCharacter(feature, "huisletter");
        String houseNumberExtra = FeatureUtil.getString(feature, "toevoeging");
        return new NL_HouseNumberImpl(null, number, houseLetter, houseNumberExtra);
    }
}
