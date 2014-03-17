package org.openstreetmap.josm.plugins.ods.bag.gt;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.opengis.feature.simple.SimpleFeature;
import org.openstreetmap.josm.plugins.ods.bag.BagEntity;
import org.openstreetmap.josm.plugins.ods.entities.EntityBuilder;
import org.openstreetmap.josm.plugins.ods.entities.external.FeatureUtil;
import org.openstreetmap.josm.plugins.ods.metadata.MetaData;

public abstract class BagEntityBuilder<T extends BagEntity> implements EntityBuilder<SimpleFeature, T> {
    private final static String SOURCE_BAG = "BAG";
    private final static DateFormat DATE_FORMAT = 
        new SimpleDateFormat("yyyy-MM-dd");
    
    @Override
    public final T build(SimpleFeature feature, MetaData metaData) {
        T entity = createEntity();
        entity.setSource(SOURCE_BAG);
        parseMetaData(entity, metaData);
        parseData(entity, feature);
        return entity;
    }
    
    protected abstract T createEntity();
    
    protected void parseMetaData(T entity, MetaData metaData) {
        try {
            Date sourceDate = (Date) metaData.get("bag.source.date");
            if (sourceDate != null) {
                entity.setSourceDate(DATE_FORMAT.format(sourceDate));
            }
        } catch (ClassCastException e) {
            throw new RuntimeException("Invalid value for source Date");
        }
    }

    protected void parseData(T entity, SimpleFeature feature) {
        entity.setIdentificatie(FeatureUtil.getLong(feature, "identificatie"));

    }
    
}
