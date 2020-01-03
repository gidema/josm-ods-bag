package org.openstreetmap.josm.plugins.ods.bag.factories;

import java.util.LinkedList;
import java.util.List;

import org.opengis.feature.simple.SimpleFeature;
import org.openstreetmap.josm.plugins.ods.bag.entity.NL_Address;
import org.openstreetmap.josm.plugins.ods.bag.entity.NL_HouseNumber;
import org.openstreetmap.josm.plugins.ods.bag.entity.NL_HouseNumberImpl;
import org.openstreetmap.josm.plugins.ods.domains.buildings.OdAddress;
import org.openstreetmap.josm.plugins.ods.entities.EntityModifier;
import org.openstreetmap.josm.plugins.ods.entities.opendata.FeatureUtil;
import org.openstreetmap.josm.plugins.ods.od.OdAddressFactory;

public class NL_GenericAddressFactory implements OdAddressFactory {
    private List<EntityModifier<NL_Address>> modifiers = new LinkedList<>();
    
    @SuppressWarnings("unchecked")
    @Override
    public void addModifier(EntityModifier<?> modifier) {
        if (modifier.getTargetType().equals(NL_Address.class)) {
            modifiers.add((EntityModifier<NL_Address>) modifier);
        }
    }

    @Override
    public OdAddress create(SimpleFeature feature) {
        NL_Address address = new NL_Address();
        address.setHouseNumber(createHouseNumber(feature));
        address.setStreetName(FeatureUtil.getString(feature, "openbare_ruimte"));
        address.setCityName(FeatureUtil.getString(feature, "woonplaats"));
        address.setPostcode(FeatureUtil.getString(feature, "postcode"));
        modifiers.forEach(m -> {
            if (m.isApplicable(address)) {
                m.modify(address);
            }
        });
        return address;
    }
    
    public NL_HouseNumber createHouseNumber(SimpleFeature feature) {
        Integer number = Integer.valueOf(FeatureUtil.getString(feature, "huisnummer"));
        Character houseLetter = FeatureUtil.getCharacter(feature, "huisletter");
        String houseNumberExtra = FeatureUtil.getString(feature, "toevoeging");
        return new NL_HouseNumberImpl(null, number, houseLetter, houseNumberExtra);
    }
}
