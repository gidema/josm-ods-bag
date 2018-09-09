package org.openstreetmap.josm.plugins.ods.bag.os.storage;

import org.openstreetmap.josm.plugins.ods.bag.entity.BagOdLigplaats;
import org.openstreetmap.josm.plugins.ods.entities.GeoIndex;
import org.openstreetmap.josm.plugins.ods.entities.impl.GeoIndexImpl;
import org.openstreetmap.josm.plugins.ods.entities.storage.AbstractOdEntityStore;

/**
 * Store ligplaats entities created from geotools features.
 * This store has indexes on the id and a geoIndex.
 *
 * @author Gertjan Idema <mail@gertjanidema.nl>
 *
 */
public class OdLigplaatsStore extends AbstractOdEntityStore<BagOdLigplaats, Long> {
    private final GeoIndex<BagOdLigplaats> geoIndexImpl = new GeoIndexImpl<>(BagOdLigplaats.class, BagOdLigplaats::getGeometry);

    public OdLigplaatsStore() {
        super(BagOdLigplaats::getId);
        addIndex(geoIndexImpl);
    }

    @Override
    public GeoIndex<BagOdLigplaats> getGeoIndex() {
        return geoIndexImpl;
    }
}
