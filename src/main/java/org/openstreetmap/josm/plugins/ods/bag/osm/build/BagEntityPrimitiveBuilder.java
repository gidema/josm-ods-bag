package org.openstreetmap.josm.plugins.ods.bag.osm.build;

import java.util.HashMap;
import java.util.Map;

import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.plugins.ods.LayerManager;
import org.openstreetmap.josm.plugins.ods.entities.Entity;
import org.openstreetmap.josm.plugins.ods.entities.EntityPrimitiveBuilder;
import org.openstreetmap.josm.plugins.ods.entities.actual.Address;
import org.openstreetmap.josm.plugins.ods.osm.DefaultPrimitiveBuilder;
import org.openstreetmap.josm.plugins.ods.osm.OsmPrimitiveFactory;

public abstract class BagEntityPrimitiveBuilder<T extends Entity>
        implements EntityPrimitiveBuilder<T> {
    private final LayerManager layerManager;
    private final OsmPrimitiveFactory primitiveBuilder;

    public BagEntityPrimitiveBuilder(LayerManager layerManager) {
        this.layerManager = layerManager;
        this.primitiveBuilder = new DefaultPrimitiveBuilder(layerManager);
    }

    @Override
    public void createPrimitive(T entity) {
        if (entity.getPrimitive() == null && entity.getGeometry() != null) {
            Map<String, String> tags = new HashMap<>();
            buildTags(entity, tags);
            OsmPrimitive primitive = primitiveBuilder.create(entity.getGeometry(), tags);
            entity.setPrimitive(primitive);
            layerManager.register(primitive, entity);
        }
    }

    protected abstract void buildTags(T entity, Map<String, String> tags);
    
    public static void createAddressTags(Address address, Map<String, String> tags) {
        if (address.getStreetName() != null) {
          tags.put("addr:street", address.getStreetName());
        }
        if (address.getFullHouseNumber() != null) {
            tags.put("addr:housenumber", address.getFullHouseNumber());
        }
        if (address.getPostcode() != null) {
          tags.put("addr:postcode", address.getPostcode());
        }
        if (address.getCityName() != null) {
          tags.put("addr:city", address.getCityName());
        }
    }

}
