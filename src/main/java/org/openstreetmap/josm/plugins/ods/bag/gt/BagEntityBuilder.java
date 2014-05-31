package org.openstreetmap.josm.plugins.ods.bag.gt;

import org.opengis.feature.simple.SimpleFeature;
import org.openstreetmap.josm.plugins.ods.bag.BagEntity;
import org.openstreetmap.josm.plugins.ods.entities.EntityBuilder;
import org.openstreetmap.josm.plugins.ods.geotools.FeatureUtil;
import org.openstreetmap.josm.plugins.ods.metadata.MetaData;
import org.threeten.bp.ZonedDateTime;
import org.threeten.bp.format.DateTimeFormatter;

public abstract class BagEntityBuilder<T extends BagEntity> implements EntityBuilder<SimpleFeature, T> {
    private final static String SOURCE_BAG = "BAG";
    private final static DateTimeFormatter DATETIMEFORMATTER = 
        DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
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
            ZonedDateTime sourceDate = (ZonedDateTime) metaData.get("downloadDateTime");
            if (sourceDate != null) {
                entity.setSourceDate(sourceDate.format(DATETIMEFORMATTER));
            }
        } catch (ClassCastException e) {
            throw new RuntimeException("Invalid value for source Date");
        }
    }

    protected void parseData(T entity, SimpleFeature feature) {
        entity.setIdentificatie(FeatureUtil.getLong(feature, "identificatie"));

    }
    
}
