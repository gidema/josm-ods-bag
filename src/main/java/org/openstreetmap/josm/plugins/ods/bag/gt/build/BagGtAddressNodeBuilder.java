package org.openstreetmap.josm.plugins.ods.bag.gt.build;

import org.opengis.feature.simple.SimpleFeature;
import org.openstreetmap.josm.plugins.ods.bag.entity.BagAddress;
import org.openstreetmap.josm.plugins.ods.bag.entity.BagAddressNode;
import org.openstreetmap.josm.plugins.ods.crs.CRSUtil;
import org.openstreetmap.josm.plugins.ods.entities.actual.AddressNode;
import org.openstreetmap.josm.plugins.ods.entities.opendata.FeatureUtil;
import org.openstreetmap.josm.plugins.ods.io.DownloadResponse;
import org.openstreetmap.josm.plugins.ods.metadata.MetaData;

public class BagGtAddressNodeBuilder extends BagGtEntityBuilder<AddressNode, BagAddressNode> {
    
    public BagGtAddressNodeBuilder(CRSUtil crsUtil) {
        super(crsUtil);
    }

    @Override
    protected BagAddressNode newInstance() {
        return new BagAddressNode();
    }

    @Override
    public BagAddressNode build(SimpleFeature feature, MetaData metaData, DownloadResponse response) {
        BagAddressNode addressNode = super.build(feature, metaData, response);
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
        addressNode.setAddress(address);
        addressNode.setStatus(FeatureUtil.getString(feature, "status"));
        addressNode.setGebruiksdoel(FeatureUtil.getString(feature, "gebruiksdoel"));
        addressNode.setArea(FeatureUtil.getDouble(feature, "oppervlakte"));
        addressNode.setBuildingRef(FeatureUtil.getLong(feature, "pandidentificatie"));
        return addressNode;
    }
}
