package org.openstreetmap.josm.plugins.ods.bag.entity.osm;

import org.locationtech.jts.geom.Geometry;
import org.openstreetmap.josm.plugins.ods.entities.OsmEntity;
import org.openstreetmap.josm.plugins.ods.matching.OsmMatch;

public interface OsmBagLanduse extends OsmEntity {

    public Long getBagId();

    @Override
    public Geometry getGeometry();

    /**
     * Return the address information associated with this static_caravan landuse.
     *
     * @return null if no address is associated with the site
     */
    public OsmAddress getAddress();

    public void setMatch(OsmMatch<OsmBagLanduse> bagLanduseMatch);
}
