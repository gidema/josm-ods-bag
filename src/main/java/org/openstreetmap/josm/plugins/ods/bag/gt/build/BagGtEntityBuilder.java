package org.openstreetmap.josm.plugins.ods.bag.gt.build;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.opengis.feature.simple.SimpleFeature;
import org.openstreetmap.josm.plugins.ods.crs.CRSException;
import org.openstreetmap.josm.plugins.ods.crs.CRSUtil;
import org.openstreetmap.josm.plugins.ods.entities.AbstractEntity;
import org.openstreetmap.josm.plugins.ods.entities.external.FeatureUtil;
import org.openstreetmap.josm.plugins.ods.entities.external.GeotoolsEntityBuilder;
import org.openstreetmap.josm.plugins.ods.metadata.MetaData;
import org.openstreetmap.josm.plugins.ods.metadata.MetaDataException;

public abstract class BagGtEntityBuilder<T extends AbstractEntity> implements GeotoolsEntityBuilder<T>{
    private final static DateFormat sourceDateFormat = new SimpleDateFormat("YYYY-MM-dd");
    private final CRSUtil crsUtil;
    protected MetaData metaData;
    
    public BagGtEntityBuilder(CRSUtil crsUtil) {
        super();
        this.crsUtil = crsUtil;
    }

    @Override
    public void setMetaData(MetaData metaData) {
        this.metaData = metaData;
    }
    
    public void build(T entity, SimpleFeature feature) {
        entity.setReferenceId(FeatureUtil.getLong(feature, "identificatie"));
        try {
            Date date = (Date) metaData.get("bag.source.date");
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
