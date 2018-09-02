package org.openstreetmap.josm.plugins.ods.bag.osm.build;

import static org.openstreetmap.josm.plugins.ods.entities.Entity.Completeness.Complete;

import java.util.HashMap;
import java.util.Map;

import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.plugins.ods.domains.buildings.OdAddress;
import org.openstreetmap.josm.plugins.ods.entities.EntityPrimitiveBuilder;
import org.openstreetmap.josm.plugins.ods.entities.OdEntity;
import org.openstreetmap.josm.plugins.ods.entities.opendata.OdLayerManager;
import org.openstreetmap.josm.plugins.ods.entities.storage.OdEntityStore;
import org.openstreetmap.josm.plugins.ods.osm.DefaultPrimitiveFactory;
import org.openstreetmap.josm.plugins.ods.osm.OsmPrimitiveFactory;

public abstract class BagEntityPrimitiveBuilder<T extends OdEntity>
implements EntityPrimitiveBuilder<T> {
    private final OdLayerManager layerManager;
    private final OsmPrimitiveFactory primitiveFactory;
    private final OdEntityStore<T, ?> entityStore;

    public BagEntityPrimitiveBuilder(OdLayerManager layerManager, OdEntityStore<T, ?> entityStore) {
        this.layerManager = layerManager;
        this.primitiveFactory = new DefaultPrimitiveFactory(layerManager);
        this.entityStore = entityStore;
    }

    @Override
    public void run() {
        entityStore.stream()
        .filter(entity->entity.getPrimitive() == null)
        .filter(entity->entity.getCompleteness() == Complete)
        .forEach(this::createPrimitive);
    }

    @Override
    public void createPrimitive(T entity) {
        if (entity.getPrimitive() == null && entity.getGeometry() != null) {
            Map<String, String> tags = new HashMap<>();
            buildTags(entity, tags);
            OsmPrimitive primitive = primitiveFactory.create(entity.getGeometry(), tags);
            entity.setPrimitive(primitive);
            layerManager.register(primitive, entity);
        }
    }

    protected abstract void buildTags(T entity, Map<String, String> tags);

    public static void createAddressTags(OdAddress address, Map<String, String> tags) {
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
