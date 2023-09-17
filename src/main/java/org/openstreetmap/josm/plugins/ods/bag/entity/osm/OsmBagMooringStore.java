package org.openstreetmap.josm.plugins.ods.bag.entity.osm;

import org.openstreetmap.josm.plugins.ods.entities.storage.Index;
import org.openstreetmap.josm.plugins.ods.entities.storage.IndexImpl;
import org.openstreetmap.josm.plugins.ods.entities.storage.OsmEntityStore;

/**
 * Store bag mooring entities for houseboats created from osm primitives.
 * This store has indexes on the referenceId, the primitiveId and a geoIndex.
 * The primary index is on the primitive Id
 *
 * @author Gertjan Idema <mail@gertjanidema.nl>
 *
 */
public class OsmBagMooringStore extends OsmEntityStore<OsmBagMooring> {
    private final Index<OsmBagMooring> mooringIdIndex = new IndexImpl<>(OsmBagMooring.class, OsmBagMooring::getMooringId);

    public OsmBagMooringStore() {
        super();
    }

    public Index<OsmBagMooring> getMooringIdIndex() {
        return mooringIdIndex;
    }

    @Override
    public void onAdd(OsmBagMooring entity) {
        mooringIdIndex.insert(entity);
    }

    @Override
    public void onRemove(OsmBagMooring entity) {
        mooringIdIndex.remove(entity);
    }

    @Override
    public void beforeClear() {
        mooringIdIndex.clear();
    }

//    @Override
//    public GeoIndex<OsmBagMooring> getGeoIndex() {
//        return geoIndex;
//    }

}
