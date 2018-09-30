package org.openstreetmap.josm.plugins.ods.bag.gt.parsing;

import org.opengis.feature.simple.SimpleFeature;
import org.openstreetmap.josm.plugins.ods.bag.entity.BagOdAddressNode;
import org.openstreetmap.josm.plugins.ods.bag.relations.BuildingUnitToAddressNodeRelation;
import org.openstreetmap.josm.plugins.ods.bag.relations.BuildingUnitToAddressNodeRelation.Tuple;
import org.openstreetmap.josm.plugins.ods.domains.buildings.OdAddress;
import org.openstreetmap.josm.plugins.ods.domains.buildings.OdAddressNode;
import org.openstreetmap.josm.plugins.ods.domains.buildings.impl.OdAddressNodeStore;
import org.openstreetmap.josm.plugins.ods.entities.opendata.FeatureUtil;
import org.openstreetmap.josm.plugins.ods.io.DownloadResponse;

public class BagDuinoordAddressNodeParser extends BagFeatureParser {

    //    private final OdBuildingUnitStore buildingUnitStore;
    private final OdAddressNodeStore addressNodeStore;
    private final BuildingUnitToAddressNodeRelation buildingUnitToAddressNodeRelation;

    public BagDuinoordAddressNodeParser(
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
        Tuple pair = parseBuildingUnit_AddressNodePair(feature);
        buildingUnitToAddressNodeRelation.add(pair);
    }

    private OdAddressNode parseAddressNode(SimpleFeature feature, DownloadResponse response) {
        BagOdAddressNode addressNode = new BagOdAddressNode();
        super.parse(feature, addressNode, response);
        addressNode.setPrimaryId(FeatureUtil.getLong(feature, "nummeraanduiding"));
        OdAddress address = parseAddress(feature);
        addressNode.setAddress(address);
        //        addressNode.setGeometry(buildingUnit.getGeometry());
        //        addressNode.setBuildinUnit(buildingUnit);
        //        addressNode.setStatus(buildingUnit.getStatus();
        return addressNode;
    }

    private static Tuple parseBuildingUnit_AddressNodePair(SimpleFeature feature) {
        Long buildingUnitId = FeatureUtil.getLong(feature, "verblijfsobject");
        Long addressNodeId = FeatureUtil.getLong(feature, "nummeraanduiding");
        return new Tuple(buildingUnitId, addressNodeId);
    }
}
