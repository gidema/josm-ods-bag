package org.openstreetmap.josm.plugins.ods.bag.entity.osm;

import org.openstreetmap.josm.plugins.ods.entities.storage.Index;
import org.openstreetmap.josm.plugins.ods.entities.storage.IndexImpl;
import org.openstreetmap.josm.plugins.ods.entities.storage.OsmEntityStore;

/**
 * Store building entities created from osm primitives.
 * This store has indexes on the referenceId, the primitiveId and a geoIndex.
 * The primary index is on the primitive Id
 *
 * @author Gertjan Idema <mail@gertjanidema.nl>
 *
 */
public class OsmBuildingStore extends OsmEntityStore<OsmBuilding> {
    private final Index<OsmBuilding> buildingIdIndex = new IndexImpl<>(OsmBuilding.class, OsmBuilding::getBuildingId);

    public OsmBuildingStore() {
        super();
    }

    public Index<OsmBuilding> getBuildingIdIndex() {
        return buildingIdIndex;
    }

    @Override
    public void onAdd(OsmBuilding entity) {
        buildingIdIndex.insert(entity);
    }

    @Override
    public void onRemove(OsmBuilding entity) {
        buildingIdIndex.remove(entity);
    }

    @Override
    public void beforeClear() {
        buildingIdIndex.clear();
    }

    
}
