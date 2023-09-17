package org.openstreetmap.josm.plugins.ods.bag.entity.storage;

import org.openstreetmap.josm.plugins.ods.bag.entity.DemolishedBuilding;

/**
 * Store demolished building entities created from features.
 * This store has indexes on the referenceId and a geoIndex.
 * The primary index is on the building Id
 *
 * @author Gertjan Idema <mail@gertjanidema.nl>
 *
 */
public class BagDemolishedBuildingStore extends BagEntityStore<DemolishedBuilding> {

    public BagDemolishedBuildingStore() {
        super(DemolishedBuilding::getBuildingId);
    }
}
