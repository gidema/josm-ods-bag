package org.openstreetmap.josm.plugins.ods.bag.tools4osm.factories;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javax.xml.namespace.QName;

import org.openstreetmap.josm.plugins.ods.bag.entity.NLAddress;
import org.openstreetmap.josm.plugins.ods.bag.entity.NlHouseNumber;
import org.openstreetmap.josm.plugins.ods.bag.entity.impl.BagMissingAddress;
import org.openstreetmap.josm.plugins.ods.bag.entity.impl.NlAddressImpl;
import org.openstreetmap.josm.plugins.ods.bag.entity.impl.NlHouseNumberImpl;
import org.openstreetmap.josm.plugins.ods.bag.entity.storage.BagWithdrawnAddressNodeStore;
import org.openstreetmap.josm.plugins.ods.bag.tools4osm.BagTools4Osm;
import org.openstreetmap.josm.plugins.ods.context.OdsContext;
import org.openstreetmap.josm.plugins.ods.entities.EntityStatus;
import org.openstreetmap.josm.plugins.ods.entities.OdEntityFactory;
import org.openstreetmap.josm.plugins.ods.entities.opendata.FeatureUtil;
import org.openstreetmap.josm.plugins.ods.io.DownloadResponse;
import org.openstreetmap.josm.plugins.ods.saxparser.opengis.wfs.WfsFeature;

public class BagWithdrawnAddressFactory implements OdEntityFactory {
    private static QName featureTypeName = new QName(BagTools4Osm.NS_TOOSL4OSM, "Address_Withdrawn");
    private final BagWithdrawnAddressNodeStore addressNodeStore;

    public BagWithdrawnAddressFactory(OdsContext context) {
        this.addressNodeStore = context.getComponent(BagWithdrawnAddressNodeStore.class);
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
        addressNode.setStatus(EntityStatus.REMOVED);
        addressNode.setSecondary(FeatureUtil.getString(feature, "nevenadres").equals("true"));
        this.addressNodeStore.add(addressNode);
    }
    public NlAddressImpl createAddress(WfsFeature feature) {
        NlAddressImpl address = new NlAddressImpl();
        address.setHouseNumber(createHouseNumber(feature));
        address.setStreetName(FeatureUtil.getString(feature, "straat"));
        address.setCityName(FeatureUtil.getString(feature, "woonplaats"));
        address.setPostcode(FeatureUtil.getString(feature, "postcode"));
        return address;
    }
    
    private NlHouseNumber createHouseNumber(WfsFeature feature) {
        Integer number = Integer.valueOf(FeatureUtil.getString(feature, "huisnummer"));
        Character houseLetter = FeatureUtil.getCharacter(feature, "huisletter");
        String houseNumberExtra = FeatureUtil.getString(feature, "toevoeging");
        return new NlHouseNumberImpl(number, houseLetter, houseNumberExtra);
    }
}
