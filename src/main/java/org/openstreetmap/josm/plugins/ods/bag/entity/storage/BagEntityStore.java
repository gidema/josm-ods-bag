package org.openstreetmap.josm.plugins.ods.bag.entity.storage;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.openstreetmap.josm.plugins.ods.entities.OdEntity;
import org.openstreetmap.josm.plugins.ods.entities.storage.EntityStore;
import org.openstreetmap.josm.plugins.ods.entities.storage.GeoCapable;
import org.openstreetmap.josm.plugins.ods.entities.storage.GeoIndex;
import org.openstreetmap.josm.plugins.ods.entities.storage.GeoIndexImpl;

public abstract class BagEntityStore<T extends OdEntity> implements EntityStore<T>, GeoCapable<T> {
    private final Map<Long, T> index;
    private final Function<T, Long> idFunction;
    private final GeoIndex<T> geoIndex;
    private Geometry boundary;

    public BagEntityStore(Function<T, Long> idFunction) {
        super();
        this.index = new HashMap<>();
        this.geoIndex = new GeoIndexImpl<>();
        this.idFunction = idFunction;
        this.boundary = new GeometryFactory().buildGeometry(Collections.emptyList());
    }

    @Override
    public Iterator<T> iterator() {
        return index.values().iterator();
    }

    @Override
    public void add(T entity) {
        Long id = idFunction.apply(entity);
        if (index.put(id, entity) == null) {
            geoIndex.insert(entity);
            onAdd(entity);
        }
    }

    @Override
    public Geometry getBoundary() {
        return boundary;
    }

    @Override
    public void extendBoundary(Geometry bounds) {
        this.boundary = this.boundary.union(bounds);
    }

    @Override
    public GeoIndex<T> getGeoIndex() {
        return geoIndex;
    }

    public T get(Long id) {
        return index.get(id);
    }
    
    @Override
    public Stream<T> stream() {
        return index.values().stream();
    }

    @Override
    public void remove(T entity) {
        Long id = idFunction.apply(entity);
        index.remove(id);
        onRemove(entity);
    }

    @Override
    public void clear() {
        beforeClear();
        index.clear();
    }

    public void onAdd(@SuppressWarnings("unused") T entity) {
        // Override if necessary
    }
        
    public void onRemove(@SuppressWarnings("unused") T entity) {
        // Override if necessary
    }

    public void beforeClear() {
        // Override if necessary
    }    
}
