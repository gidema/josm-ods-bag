package org.openstreetmap.josm.plugins.ods.bag.gt.build;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

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
    private final static DateFormat sourceDateFormat = new SimpleDateFormat("YYYY-MM-dd");
    private final CRSUtil crsUtil;
//    protected MetaData metaData;
    
    public BagGtEntityBuilder(CRSUtil crsUtil) {
        super();
        this.crsUtil = crsUtil;
    }

    //    @Override
//    public void setContext(Context ctx) {
//        this.entitySource = (EntitySource) ctx.get("entitySource");
//    }
//    
//    @Override
//    public void setMetaData(MetaData metaData) {
//        this.metaData = metaData;
//    }
//    
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
            Date date = (Date) metaData.get("source.date");
            if (date != null) {
                entity.setSourceDate(sourceDateFormat.format(date));
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
