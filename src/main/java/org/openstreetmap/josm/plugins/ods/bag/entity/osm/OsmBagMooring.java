package org.openstreetmap.josm.plugins.ods.bag.entity.osm;

import org.locationtech.jts.geom.Geometry;
import org.openstreetmap.josm.plugins.ods.entities.OsmEntity;
import org.openstreetmap.josm.plugins.ods.matching.OsmMatch;

public interface OsmBagMooring extends OsmEntity {

    public Long getMooringId();

    @Override
    public Geometry getGeometry();

    /**
     * Return the address information associated with this building.
     *
     * @return null if no address is associated with the building
     */
    public OsmAddress getAddress();

    /**
     * Check is the full area of this building has been loaded. This is true if
     * the building is completely covered by the downloaded area.
     *
     * @return
     */

    public void setMatch(OsmMatch<OsmBagMooring> mooringMatch);
}
