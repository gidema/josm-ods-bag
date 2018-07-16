package org.openstreetmap.josm.plugins.ods.bag.gt.build;

import org.opengis.feature.simple.SimpleFeature;
import org.openstreetmap.josm.plugins.ods.crs.CRSUtil;
import org.openstreetmap.josm.plugins.ods.domains.buildings.OdBuildingUnit;
import org.openstreetmap.josm.plugins.ods.domains.buildings.impl.DefaultOdBuildingUnit;
import org.openstreetmap.josm.plugins.ods.entities.EntityStatus;
import org.openstreetmap.josm.plugins.ods.entities.opendata.FeatureUtil;
import org.openstreetmap.josm.plugins.ods.io.DownloadResponse;

import com.vividsolutions.jts.geom.Geometry;

public class BagPdokBuildingUnitBuilder extends BagGtEntityBuilder<OdBuildingUnit> {

    public BagPdokBuildingUnitBuilder(CRSUtil crsUtil) {
        super(crsUtil);
    }

    @Override
    public OdBuildingUnit build(SimpleFeature feature, DownloadResponse response) {
        OdBuildingUnit buildingUnit = new DefaultOdBuildingUnit();
        super.parse(feature, buildingUnit, response);
        buildingUnit.setBuildingUnitId(FeatureUtil.getLong(feature, "identificatie"));
        String status = FeatureUtil.getString(feature, "status");
        buildingUnit.setStatus(parseStatus(status));
        return buildingUnit;
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
