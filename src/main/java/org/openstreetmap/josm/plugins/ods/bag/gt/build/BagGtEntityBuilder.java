package org.openstreetmap.josm.plugins.ods.bag.gt.build;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.opengis.feature.simple.SimpleFeature;
import org.openstreetmap.josm.plugins.ods.crs.CRSException;
import org.openstreetmap.josm.plugins.ods.crs.CRSUtil;
import org.openstreetmap.josm.plugins.ods.entities.Entity;
import org.openstreetmap.josm.plugins.ods.entities.opendata.FeatureUtil;
import org.openstreetmap.josm.plugins.ods.entities.opendata.GeotoolsEntityBuilder;
import org.openstreetmap.josm.plugins.ods.io.DownloadResponse;
import org.openstreetmap.josm.plugins.ods.metadata.MetaData;
import org.openstreetmap.josm.plugins.ods.metadata.MetaDataException;

public abstract class BagGtEntityBuilder<T extends Entity, T2 extends T> implements GeotoolsEntityBuilder<T> {
    private final CRSUtil crsUtil;
    
    public BagGtEntityBuilder(CRSUtil crsUtil) {
        super();
        this.crsUtil = crsUtil;
    }

    @Override
    public Object getReferenceId(SimpleFeature feature) {
        return FeatureUtil.getLong(feature, "identificatie");
    }

    @Override
    public T2 build(SimpleFeature feature, MetaData metaData, DownloadResponse response) {
        T2 entity = newInstance();
        entity.setDownloadResponse(response);
        entity.setReferenceId(getReferenceId(feature));
        entity.setPrimaryId(feature.getID());
        try {
            LocalDate date = (LocalDate) metaData.get("source.date");
            if (date != null) {
                entity.setSourceDate(DateTimeFormatter.ISO_LOCAL_DATE.format(date));
            }
            entity.setSource("BAG");
            entity.setGeometry(crsUtil.transform(feature));
        } catch (MetaDataException | CRSException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
        return entity;
    }

    protected abstract T2 newInstance();
}
