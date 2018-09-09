package org.openstreetmap.josm.plugins.ods.bag.gt.parsing;

import org.opengis.feature.simple.SimpleFeature;
import org.openstreetmap.josm.plugins.ods.bag.entity.BagOdAddressNode;
import org.openstreetmap.josm.plugins.ods.crs.CRSUtil;
import org.openstreetmap.josm.plugins.ods.domains.buildings.AddressNodeStatus;
import org.openstreetmap.josm.plugins.ods.domains.buildings.OdAddress;
import org.openstreetmap.josm.plugins.ods.domains.buildings.OdAddressNode;
import org.openstreetmap.josm.plugins.ods.domains.buildings.impl.OdAddressNodeStore;
import org.openstreetmap.josm.plugins.ods.entities.opendata.FeatureUtil;
import org.openstreetmap.josm.plugins.ods.io.DownloadResponse;

public class BagDuinoordAddressWithdrawnParser extends BagFeatureParser {
    private final OdAddressNodeStore addressNodeStore;

    public BagDuinoordAddressWithdrawnParser(CRSUtil crsUtil,
            OdAddressNodeStore addressNodeStore) {
        super(crsUtil);
        this.addressNodeStore = addressNodeStore;
    }

    /**
     * Parse the AddressNode feature.
     *
     * @param feature
     * @param response
     */
    @Override
    public void parse(SimpleFeature feature, DownloadResponse response) {
        OdAddressNode addressNode = parseAddressNode(feature, response);
        addressNodeStore.add(addressNode);
    }

    private OdAddressNode parseAddressNode(SimpleFeature feature, DownloadResponse response) {
        BagOdAddressNode addressNode = new BagOdAddressNode();
        super.parse(feature, addressNode, response);
        Long id = FeatureUtil.getLong(feature, "nummeraanduiding");
        addressNode.setPrimaryId(id);
        addressNode.setAddressNodeId(id);
        addressNode.setStatus(AddressNodeStatus.WITHDRAWN);
        OdAddress address = DuinoordAddressParser.parseAddress(feature);
        addressNode.setAddress(address);
        return addressNode;
    }
}
