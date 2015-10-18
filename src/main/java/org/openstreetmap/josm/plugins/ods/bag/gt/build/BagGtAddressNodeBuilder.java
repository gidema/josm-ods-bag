package org.openstreetmap.josm.plugins.ods.bag.gt.build;

import java.util.function.Consumer;

import org.opengis.feature.simple.SimpleFeature;
import org.openstreetmap.josm.plugins.ods.bag.entity.BagAddress;
import org.openstreetmap.josm.plugins.ods.bag.entity.BagAddressNode;
import org.openstreetmap.josm.plugins.ods.crs.CRSUtil;
import org.openstreetmap.josm.plugins.ods.entities.actual.AddressNode;
import org.openstreetmap.josm.plugins.ods.entities.external.FeatureUtil;

public class BagGtAddressNodeBuilder extends BagGtEntityBuilder<BagAddressNode> {
    private Consumer<AddressNode> addressNodeConsumer;
    
    public BagGtAddressNodeBuilder(CRSUtil crsUtil, Consumer<AddressNode> addressNodeConsumer) {
        super(crsUtil);
        this.addressNodeConsumer = addressNodeConsumer;
    }

    @Override
    public void buildGtEntity(SimpleFeature feature) {
        BagAddress address = new BagAddress();
        address.setHouseNumber(FeatureUtil.getInteger(feature, "huisnummer"));
        address.setHuisletter(FeatureUtil.getString(feature, "huisletter"));
        address.setHuisnummerToevoeging(FeatureUtil.getString(feature, "toevoeging"));
        address.setStreetName(FeatureUtil.getString(feature, "openbare_ruimte"));
        address.setCityName(FeatureUtil.getString(feature, "woonplaats"));
        String postcode = FeatureUtil.getString(feature, "postcode");
        if (postcode != null) {
            address.setPostcode(FeatureUtil.getString(feature, "postcode"));
        }
        BagAddressNode addressNode = new BagAddressNode();
        super.build(addressNode, feature);
        addressNode.setAddress(address);
        addressNode.setStatus(FeatureUtil.getString(feature, "status"));
        addressNode.setGebruiksdoel(FeatureUtil.getString(feature, "gebruiksdoel"));
        addressNode.setArea(FeatureUtil.getDouble(feature, "oppervlakte"));
        addressNode.setBuildingRef(FeatureUtil.getLong(feature, "pandidentificatie"));
        addressNodeConsumer.accept(addressNode);
    }
}
