package org.openstreetmap.josm.plugins.ods.bag.gt.build;

import org.locationtech.jts.geom.Geometry;
import org.opengis.feature.simple.SimpleFeature;
import org.openstreetmap.josm.plugins.ods.bag.entity.BagOdAddressNode;
import org.openstreetmap.josm.plugins.ods.bag.factories.NL_AddressFactoryFactory;
import org.openstreetmap.josm.plugins.ods.crs.CRSUtil;
import org.openstreetmap.josm.plugins.ods.domains.buildings.OdAddress;
import org.openstreetmap.josm.plugins.ods.domains.buildings.OdAddressNode;
import org.openstreetmap.josm.plugins.ods.entities.EntityStatus;
import org.openstreetmap.josm.plugins.ods.entities.opendata.FeatureUtil;
import org.openstreetmap.josm.plugins.ods.io.DownloadResponse;
import org.openstreetmap.josm.plugins.ods.od.OdAddressFactory;

public class BagGtAddressNodeBuilder extends BagGtEntityBuilder<OdAddressNode> {
    private OdAddressFactory addressFactory = NL_AddressFactoryFactory.create();

    public BagGtAddressNodeBuilder(CRSUtil crsUtil) {
        super(crsUtil);
    }

    @Override
    public OdAddressNode build(SimpleFeature feature, DownloadResponse response) {
        BagOdAddressNode addressNode = new BagOdAddressNode();
        super.parse(feature, addressNode, response);
        OdAddress address = addressFactory.create(feature);
        addressNode.setAddress(address);
        addressNode.setStatus(parseStatus(FeatureUtil.getString(feature, "status")));
        addressNode.setGebruiksdoel(FeatureUtil.getString(feature, "gebruiksdoel"));
        addressNode.setArea(FeatureUtil.getBigInteger(feature, "oppervlakte").doubleValue());
        addressNode.setBuildingRef(Long.valueOf(FeatureUtil.getString(feature, "pandidentificatie")));
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
