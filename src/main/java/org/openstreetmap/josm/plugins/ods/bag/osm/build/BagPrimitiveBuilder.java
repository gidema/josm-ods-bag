package org.openstreetmap.josm.plugins.ods.bag.osm.build;

import java.util.HashMap;
import java.util.Map;

import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.plugins.ods.AbstractPrimitiveBuilder;
import org.openstreetmap.josm.plugins.ods.LayerManager;
import org.openstreetmap.josm.plugins.ods.entities.Entity;

public abstract class BagPrimitiveBuilder<T extends Entity> extends AbstractPrimitiveBuilder<T> {
    
    public BagPrimitiveBuilder(LayerManager dataLayer) {
        super(dataLayer);
    }

    @Override
    public void createPrimitive(T entity) {
        if (entity.getPrimitive() == null && entity.getGeometry() != null) {
        	Map<String, String> tags = new HashMap<>();
        	buildTags(entity, tags);
            OsmPrimitive primitive = build(entity.getGeometry(), tags);
            entity.setPrimitive(primitive);
        }
    }

    protected abstract void buildTags(T entity, Map<String, String> tags);
}
