package org.openstreetmap.josm.plugins.ods.bag.gt.build;

import org.opengis.feature.simple.SimpleFeature;
import org.openstreetmap.josm.plugins.ods.bag.entity.BagOdAddress;
import org.openstreetmap.josm.plugins.ods.bag.entity.BagOdAddressNode;
import org.openstreetmap.josm.plugins.ods.crs.CRSUtil;
import org.openstreetmap.josm.plugins.ods.domains.buildings.OdAddressNode;
import org.openstreetmap.josm.plugins.ods.entities.EntityStatus;
import org.openstreetmap.josm.plugins.ods.entities.opendata.FeatureUtil;
import org.openstreetmap.josm.plugins.ods.io.DownloadResponse;
import org.openstreetmap.josm.plugins.ods.pdok.bag.wfs.v1_1.NL_HouseNumberFactory;
import org.locationtech.jts.geom.Geometry;

public class BagGtAddressNodeBuilder extends BagGtEntityBuilder<OdAddressNode> {
    private NL_HouseNumberFactory hnrFactory = new NL_HouseNumberFactory();
    public BagGtAddressNodeBuilder(CRSUtil crsUtil) {
        super(crsUtil);
    }

    @Override
    public OdAddressNode build(SimpleFeature feature, DownloadResponse response) {
        BagOdAddressNode addressNode = new BagOdAddressNode();
        super.parse(feature, addressNode, response);
        BagOdAddress address = new BagOdAddress();
        address.setHouseNumber(hnrFactory.create(feature));
        address.setStreetName(FeatureUtil.getString(feature, "openbare_ruimte"));
        address.setCityName(FeatureUtil.getString(feature, "woonplaats"));
        String postcode = FeatureUtil.getString(feature, "postcode");
        if (postcode != null) {
            address.setPostcode(FeatureUtil.getString(feature, "postcode"));
        }
        addressNode.setAddress(address);
        addressNode.setStatus(parseStatus(FeatureUtil.getString(feature, "status")));
        addressNode.setGebruiksdoel(FeatureUtil.getString(feature, "gebruiksdoel"));
        addressNode.setArea(FeatureUtil.getBigDecimal(feature, "oppervlakte").doubleValue());
        addressNode.setBuildingRef(FeatureUtil.getLong(feature, "pandidentificatie"));
        return addressNode;
    }

    @Override
    protected Geometry getGeometry(SimpleFeature feature) {
        return (Geometry) feature.getAttribute("geometrie");
    }

    private static EntityStatus parseStatus(String status) {
        switch (status) {
        case "Verblijfsobject gevormd":
            return EntityStatus.PLANNED;
        case "Verblijfsobject in gebruik":
        case "Verblijfsobject buiten gebruik":
        case "Verblijfsobject in gebruik (niet ingemeten)":
            return EntityStatus.IN_USE;
        case "Verblijfsobject ingetrokken":
        case "Niet gerealiseerd verblijfsobject":
            return EntityStatus.REMOVED;
        default:
            return EntityStatus.IN_USE;
        }
    }
}
