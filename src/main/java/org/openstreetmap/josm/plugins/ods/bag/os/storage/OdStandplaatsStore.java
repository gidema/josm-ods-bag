package org.openstreetmap.josm.plugins.ods.bag.os.storage;

import org.openstreetmap.josm.plugins.ods.bag.entity.BagOdStandplaats;
import org.openstreetmap.josm.plugins.ods.entities.GeoIndex;
import org.openstreetmap.josm.plugins.ods.entities.impl.GeoIndexImpl;
import org.openstreetmap.josm.plugins.ods.entities.storage.AbstractOdEntityStore;

/**
 * Store standplaats entities created from geotools features.
 * This store has indexes on the referenceId and a geoIndex.
 * The primary index is on the primitive Id
 *
 * @author Gertjan Idema <mail@gertjanidema.nl>
 *
 */
public class OdStandplaatsStore extends AbstractOdEntityStore<BagOdStandplaats, Long> {
    private final GeoIndex<BagOdStandplaats> geoIndexImpl = new GeoIndexImpl<>(BagOdStandplaats.class, BagOdStandplaats::getGeometry);

    public OdStandplaatsStore() {
        super(BagOdStandplaats::getId);
        addIndex(geoIndexImpl);
    }

    @Override
    public GeoIndex<BagOdStandplaats> getGeoIndex() {
        return geoIndexImpl;
    }
}
