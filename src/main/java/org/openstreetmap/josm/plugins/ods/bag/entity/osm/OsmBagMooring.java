package org.openstreetmap.josm.plugins.ods.bag.entity.osm;

import org.openstreetmap.josm.plugins.ods.matching.OsmMatch;

public interface OsmBagMooring extends OsmAddressableObject {
    public Long getMooringId();

    /**
     * Check is the full area of this building has been loaded. This is true if
     * the building is completely covered by the downloaded area.
     *
     * @return
     */

    public void setMatch(OsmMatch<OsmBagMooring> mooringMatch);
}
