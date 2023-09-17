package org.openstreetmap.josm.plugins.ods.bag.tools4osm.factories;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javax.xml.namespace.QName;

import org.openstreetmap.josm.plugins.ods.bag.entity.NLAddress;
import org.openstreetmap.josm.plugins.ods.bag.entity.NlHouseNumber;
import org.openstreetmap.josm.plugins.ods.bag.entity.impl.BagMissingAddress;
import org.openstreetmap.josm.plugins.ods.bag.entity.impl.NlAddressImpl;
import org.openstreetmap.josm.plugins.ods.bag.entity.impl.NlHouseNumberImpl;
import org.openstreetmap.josm.plugins.ods.bag.entity.storage.BagMissingAddressNodeStore;
import org.openstreetmap.josm.plugins.ods.bag.tools4osm.BagTools4Osm;
import org.openstreetmap.josm.plugins.ods.context.OdsContext;
import org.openstreetmap.josm.plugins.ods.entities.OdEntityFactory;
import org.openstreetmap.josm.plugins.ods.entities.opendata.FeatureUtil;
import org.openstreetmap.josm.plugins.ods.io.DownloadResponse;
import org.openstreetmap.josm.plugins.ods.saxparser.opengis.wfs.WfsFeature;

public class BagMissingAddressFactory implements OdEntityFactory {
    private static QName featureTypeName = new QName(BagTools4Osm.NS_TOOSL4OSM, "Address_Missing");
    private final BagMissingAddressNodeStore addressNodeStore;

    public BagMissingAddressFactory(OdsContext context) {
        this.addressNodeStore = context.getComponent(BagMissingAddressNodeStore.class);
    }
    
    @Override
    public boolean appliesTo(QName featureType) {
        return featureType.equals(featureTypeName);
    }

    @Override
    public void process(WfsFeature feature, DownloadResponse response) {
        BagMissingAddress addressNode = new BagMissingAddress();
        LocalDate date = response.getRequest().getDownloadTime().toLocalDate();
        if (date != null) {
            addressNode.setSourceDate(DateTimeFormatter.ISO_LOCAL_DATE.format(date));
        }
        String id = FeatureUtil.getString(feature, "nummeraanduiding");
        addressNode.setAddressId(Long.valueOf(id));
        addressNode.setSource("BAG");
        addressNode.setGeometry(feature.getGeometry());
        NLAddress address = createAddress(feature);
        addressNode.setAddress(address);
        addressNode.setSecondary(FeatureUtil.getString(feature, "nevenadres").equals("true"));
        addressNode.setBuildingUnitId(Long.valueOf(FeatureUtil.getString(feature, "verblijfsobjectidentificatie")));
        addressNode.setBuildingRef(Long.valueOf(FeatureUtil.getString(feature, "pandidentificatie")));
        this.addressNodeStore.add(addressNode);
    }

    public static NlAddressImpl createAddress(WfsFeature feature) {
        NlAddressImpl address = new NlAddressImpl();
        address.setHouseNumber(createHouseNumber(feature));
        address.setStreetName(FeatureUtil.getString(feature, "straat"));
        address.setCityName(FeatureUtil.getString(feature, "woonplaats"));
        address.setPostcode(FeatureUtil.getString(feature, "postcode"));
        return address;
    }
    
    private static NlHouseNumber createHouseNumber(WfsFeature feature) {
        Integer number = Integer.valueOf(FeatureUtil.getString(feature, "huisnummer"));
        Character houseLetter = FeatureUtil.getCharacter(feature, "huisletter");
        String houseNumberExtra = FeatureUtil.getString(feature, "huisnummertoevoeging");
        return new NlHouseNumberImpl(number, houseLetter, houseNumberExtra);
    }

}
