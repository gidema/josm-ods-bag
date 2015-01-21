package org.openstreetmap.josm.plugins.ods.bag.gt.build;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.opengis.feature.simple.SimpleFeature;
import org.openstreetmap.josm.plugins.ods.Context;
import org.openstreetmap.josm.plugins.ods.crs.CRSException;
import org.openstreetmap.josm.plugins.ods.crs.CRSUtil;
import org.openstreetmap.josm.plugins.ods.entities.AbstractEntity;
import org.openstreetmap.josm.plugins.ods.entities.EntitySource;
import org.openstreetmap.josm.plugins.ods.entities.external.FeatureUtil;
import org.openstreetmap.josm.plugins.ods.entities.external.GeotoolsEntityBuilder;
import org.openstreetmap.josm.plugins.ods.metadata.MetaData;
import org.openstreetmap.josm.plugins.ods.metadata.MetaDataException;

public abstract class BagGtEntityBuilder<T extends AbstractEntity> implements GeotoolsEntityBuilder<T>{
    private final static DateFormat sourceDateFormat = new SimpleDateFormat("YYYY-MM-dd");
    private final CRSUtil crsUtil;
    private EntitySource entitySource;
    protected MetaData metaData;
    
    public BagGtEntityBuilder(CRSUtil crsUtil) {
        super();
        this.crsUtil = crsUtil;
    }

    @Override
    public void setContext(Context ctx) {
        this.entitySource = (EntitySource) ctx.get("entitySource");
    }
    
    @Override
    public void setMetaData(MetaData metaData) {
        this.metaData = metaData;
    }
    
    @Override
    public Object getReferenceId(SimpleFeature feature) {
        return FeatureUtil.getLong(feature, "identificatie");
    }

    public void build(T entity, SimpleFeature feature) {
        entity.setEntitySource(entitySource);
        entity.setReferenceId(getReferenceId(feature));
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
        }
    }
}
