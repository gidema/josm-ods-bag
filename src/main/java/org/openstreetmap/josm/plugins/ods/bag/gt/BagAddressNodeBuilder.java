package org.openstreetmap.josm.plugins.ods.bag.gt;

import java.util.Map;

import org.opengis.feature.simple.SimpleFeature;
import org.openstreetmap.josm.plugins.ods.bag.BagAddress;
import org.openstreetmap.josm.plugins.ods.bag.BagAddressNode;
import org.openstreetmap.josm.plugins.ods.crs.CRSException;
import org.openstreetmap.josm.plugins.ods.crs.CRSUtil;

public class BagAddressNodeBuilder extends BagEntityBuilder<BagAddressNode> {
    
    @Override
    protected BagAddressNode createEntity() {
        BagAddressNode addressNode = new BagAddressNode();
        addressNode.setInternal(false);
        return addressNode;
    }

    @Override
    protected void parseData(BagAddressNode addressNode, SimpleFeature feature) {
        super.parseData(addressNode, feature);
        BagAddress address = BagAddressBuilder.build(feature);
        addressNode.setAddress(address);
        Map<String, String> otherTags = addressNode.getOtherTags();
        otherTags.put("bag:status", feature.getProperty("status").getValue().toString());
        otherTags.put("bag:gebruiksdoel", feature.getProperty("gebruiksdoel").getValue().toString());
        otherTags.put("bag:oppervlakte", feature.getProperty("oppervlakte").getValue().toString());
        addressNode.setBuildingRef(((Double)feature.getProperty("pandidentificatie").getValue()).longValue());
        try {
            addressNode.setGeometry(CRSUtil.getInstance().transform(feature));
        } catch (CRSException e) {
//            Issue issue = new ImportIssue(feature.getID(), e);
            throw new RuntimeException(e);
        }
    }   
}
