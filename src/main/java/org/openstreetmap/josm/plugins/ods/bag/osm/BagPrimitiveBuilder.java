package org.openstreetmap.josm.plugins.ods.bag.osm;

import org.openstreetmap.josm.data.osm.DataSet;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.plugins.ods.bag.BagEntity;
import org.openstreetmap.josm.plugins.ods.osm.AbtractPrimitiveBuilder;

public class BagPrimitiveBuilder<T extends BagEntity> extends AbtractPrimitiveBuilder<T> {
    public BagPrimitiveBuilder(DataSet targetDataSet) {
        super(targetDataSet);
    }

    @Override
    public void buildTags(T entity, OsmPrimitive primitive) {
        primitive.put("source", entity.getSource());
        primitive.put("source:date", entity.getSourceDate());
    }
}
