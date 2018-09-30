package org.openstreetmap.josm.plugins.ods.bag.gt.parsing;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.openstreetmap.josm.plugins.ods.bag.entity.BagOdAddress;
import org.openstreetmap.josm.plugins.ods.crs.CRSException;
import org.openstreetmap.josm.plugins.ods.crs.CRSUtil;
import org.openstreetmap.josm.plugins.ods.domains.buildings.OdAddress;
import org.openstreetmap.josm.plugins.ods.entities.OdEntity;
import org.openstreetmap.josm.plugins.ods.entities.opendata.FeatureUtil;
import org.openstreetmap.josm.plugins.ods.io.DownloadResponse;
import org.openstreetmap.josm.plugins.ods.parsing.FeatureParser;

import com.vividsolutions.jts.geom.Geometry;

public abstract class BagFeatureParser implements FeatureParser {

    public BagFeatureParser() {
        super();
    }

    @Override
    public void parse(SimpleFeatureCollection downloadedFeatures, DownloadResponse response) {
        try (
                FeatureIterator<SimpleFeature> it = downloadedFeatures.features();
                ) {
            while (it.hasNext()) {
                parse(it.next(), response);
            }
        }
    }

    public abstract void parse(SimpleFeature feature, DownloadResponse response);

    protected void parse(SimpleFeature feature, OdEntity entity, DownloadResponse response) {
        entity.setDownloadResponse(response);
        entity.setPrimaryId(feature.getID());
        LocalDate date = response.getRequest().getDownloadTime().toLocalDate();
        if (date != null) {
            entity.setSourceDate(DateTimeFormatter.ISO_LOCAL_DATE.format(date));
        }
        entity.setSource("BAG");
        try {
            CoordinateReferenceSystem crs = feature.getType()
                    .getCoordinateReferenceSystem();
            Geometry gtGeometry = getGeometry(feature);
            CRSUtil crsUtil = CRSUtil.getInstance();
            entity.setGeometry(crsUtil.toOsm(gtGeometry, crs));
        } catch (CRSException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @SuppressWarnings("static-method")
    protected Geometry getGeometry(SimpleFeature feature) {
        return (Geometry) feature.getDefaultGeometry();
    }

    public static OdAddress parseAddress(SimpleFeature feature) {
        BagOdAddress address = new BagOdAddress();
        address.setHouseNumber(FeatureUtil.getInteger(feature, "huisnummer"));
        String houseLetter = FeatureUtil.getString(feature, "huisletter");
        if (houseLetter != null) {
            address.setHuisletter(houseLetter);
            address.setHouseLetter(houseLetter.charAt(0));
        }
        String houseNumberExtra = FeatureUtil.getString(feature, "toevoeging");
        address.setHuisnummerToevoeging(houseNumberExtra);
        address.setHouseNumberExtra(houseNumberExtra);
        address.setStreetName(FeatureUtil.getString(feature, "openbare_ruimte"));
        address.setCityName(FeatureUtil.getString(feature, "woonplaats"));
        String postcode = FeatureUtil.getString(feature, "postcode");
        if (postcode != null) {
            address.setPostcode(FeatureUtil.getString(feature, "postcode"));
        }
        return address;
    }
}
