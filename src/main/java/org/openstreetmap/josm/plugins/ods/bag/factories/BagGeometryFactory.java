package org.openstreetmap.josm.plugins.ods.bag.factories;

import org.locationtech.jts.geom.Geometry;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.type.Name;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.openstreetmap.josm.plugins.ods.crs.CRSException;
import org.openstreetmap.josm.plugins.ods.crs.CRSUtil;
import org.openstreetmap.josm.plugins.ods.crs.CRSUtilProj4j;
import org.openstreetmap.josm.plugins.ods.geotools.GtEntityFactory;
import org.openstreetmap.josm.plugins.ods.io.DownloadResponse;

public class BagGeometryFactory implements GtEntityFactory<Geometry> {
    CRSUtil crsUtil = CRSUtilProj4j.getInstance();
    
    @Override
    public boolean isApplicable(Name featureType, Class<?> entityType) {
        return entityType.equals(Geometry.class);
    }

    @Override
    public Class<Geometry> getTargetType() {
        return Geometry.class;
    }

    @Override
    public Geometry create(SimpleFeature feature, DownloadResponse response) {
        CoordinateReferenceSystem crs = feature.getType()
                .getCoordinateReferenceSystem();
        Geometry gtGeometry = (Geometry) feature.getDefaultGeometry();
        try {
            return crsUtil.toOsm(gtGeometry, crs);
        } catch (CRSException e) {
            throw new RuntimeException(e);
        }
    }
}
