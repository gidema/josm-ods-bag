package org.openstreetmap.josm.plugins.ods.bag.gt.parsing;

import org.opengis.feature.simple.SimpleFeature;
import org.openstreetmap.josm.plugins.ods.bag.entity.BagOdAddressNode;
import org.openstreetmap.josm.plugins.ods.bag.relations.BuildingUnitToAddressNodeRelation;
import org.openstreetmap.josm.plugins.ods.domains.buildings.OdAddress;
import org.openstreetmap.josm.plugins.ods.domains.buildings.OdAddressNode;
import org.openstreetmap.josm.plugins.ods.domains.buildings.impl.OdAddressNodeStore;
import org.openstreetmap.josm.plugins.ods.entities.opendata.FeatureUtil;
import org.openstreetmap.josm.plugins.ods.io.DownloadResponse;

public class BagDuinoordAddressMissingParser extends BagFeatureParser {
    private final OdAddressNodeStore addressNodeStore;
    private final BuildingUnitToAddressNodeRelation buildingUnitToAddressNodeRelation;

    public BagDuinoordAddressMissingParser(
            OdAddressNodeStore addressNodeStore,
            BuildingUnitToAddressNodeRelation buildingUnitToAddressNodeRelation) {
        super();
        this.addressNodeStore = addressNodeStore;
        this.buildingUnitToAddressNodeRelation = buildingUnitToAddressNodeRelation;
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
        BuildingUnitToAddressNodeRelation.Tuple tuple = parseBu_An_Tuple(feature);
        buildingUnitToAddressNodeRelation.add(tuple);
    }

    private OdAddressNode parseAddressNode(SimpleFeature feature, DownloadResponse response) {
        BagOdAddressNode addressNode = new BagOdAddressNode();
        super.parse(feature, addressNode, response);
        Long id = FeatureUtil.getLong(feature, "nummeraanduiding");
        addressNode.setPrimaryId(id);
        addressNode.setAddressNodeId(id);
        OdAddress address = DuinoordAddressParser.parseAddress(feature);
        addressNode.setAddress(address);
        return addressNode;
    }

    private static BuildingUnitToAddressNodeRelation.Tuple parseBu_An_Tuple(SimpleFeature feature) {
        Long addressNodeId = FeatureUtil.getLong(feature, "nummeraanduiding");
        Long buildingId = FeatureUtil.getLong(feature, "verblijfsobjectidentificatie");
        return new BuildingUnitToAddressNodeRelation.Tuple(addressNodeId, buildingId);
    }

}
