package org.openstreetmap.josm.plugins.ods.bag.gt.parsing;

import org.opengis.feature.simple.SimpleFeature;
import org.openstreetmap.josm.plugins.ods.bag.entity.BagOdAddressNode;
import org.openstreetmap.josm.plugins.ods.crs.CRSUtil;
import org.openstreetmap.josm.plugins.ods.domains.buildings.OdAddress;
import org.openstreetmap.josm.plugins.ods.domains.buildings.OdAddressNode;
import org.openstreetmap.josm.plugins.ods.domains.buildings.impl.OdAddressNodeStore;
import org.openstreetmap.josm.plugins.ods.domains.buildings.relations.AddressNodeToBuildingRelation;
import org.openstreetmap.josm.plugins.ods.entities.opendata.FeatureUtil;
import org.openstreetmap.josm.plugins.ods.io.DownloadResponse;

public class BagDuinoordAddressMissingParser extends BagFeatureParser {

    //    private final OdBuildingUnitStore buildingUnitStore;
    private final OdAddressNodeStore addressNodeStore;
    private final AddressNodeToBuildingRelation addressNodeToBuildingRelation;

    public BagDuinoordAddressMissingParser(CRSUtil crsUtil,
            //            OdBuildingUnitStore buildingUnitStore,
            OdAddressNodeStore addressNodeStore,
            AddressNodeToBuildingRelation addressNodeToBuildingRelation) {
        super(crsUtil);
        this.addressNodeStore = addressNodeStore;
        this.addressNodeToBuildingRelation = addressNodeToBuildingRelation;
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
        AddressNodeToBuildingRelation.Tuple tuple = parseTuple(feature);
        addressNodeToBuildingRelation.add(tuple);
    }

    private OdAddressNode parseAddressNode(SimpleFeature feature, DownloadResponse response) {
        BagOdAddressNode addressNode = new BagOdAddressNode();
        super.parse(feature, addressNode, response);
        Long id = FeatureUtil.getLong(feature, "nummeraanduiding");
        addressNode.setPrimaryId(id);
        addressNode.setAddressNodeId(id);
        OdAddress address = DuinoordAddressParser.parseAddress(feature);
        addressNode.setAddress(address);
        //        addressNode.setGeometry(buildingUnit.getGeometry());
        //        addressNode.setBuildinUnit(buildingUnit);
        //        addressNode.setStatus(buildingUnit.getStatus();
        return addressNode;
    }

    private static AddressNodeToBuildingRelation.Tuple parseTuple(SimpleFeature feature) {
        Long addressNodeId = FeatureUtil.getLong(feature, "nummeraanduiding");
        Long buildingId = FeatureUtil.getLong(feature, "pandidentificatie");
        return new AddressNodeToBuildingRelation.Tuple(addressNodeId, buildingId);
    }

}
