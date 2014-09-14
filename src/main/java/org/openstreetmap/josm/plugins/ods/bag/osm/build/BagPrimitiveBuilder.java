package org.openstreetmap.josm.plugins.ods.bag.osm.build;

import java.util.List;

import org.openstreetmap.josm.data.osm.DataSet;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.plugins.ods.AbstractPrimitiveBuilder;
import org.openstreetmap.josm.plugins.ods.entities.Entity;

public abstract class BagPrimitiveBuilder<T extends Entity> extends AbstractPrimitiveBuilder<T> {
    
    public BagPrimitiveBuilder(DataSet targetDataSet) {
        super(targetDataSet);
    }

    @Override
    public void createPrimitives(T entity) {
        if (entity.getPrimitives() == null && entity.getGeometry() != null) {
            List<OsmPrimitive> primitives = build(entity.getGeometry());
            for (OsmPrimitive primitive : primitives) {
                buildTags(entity, primitive);
            }
            entity.setPrimitives(primitives);
        }
    }

    protected abstract void buildTags(T entity, OsmPrimitive primitive);
}
