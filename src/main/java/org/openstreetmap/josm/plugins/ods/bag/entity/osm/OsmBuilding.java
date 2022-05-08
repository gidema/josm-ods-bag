package org.openstreetmap.josm.plugins.ods.bag.entity.osm;

import java.util.List;
import java.util.Set;

import org.locationtech.jts.geom.Geometry;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.data.osm.OsmPrimitiveType;
import org.openstreetmap.josm.plugins.ods.bag.entity.Building;
import org.openstreetmap.josm.plugins.ods.domains.places.OsmCity;
import org.openstreetmap.josm.plugins.ods.entities.OsmEntity;
import org.openstreetmap.josm.plugins.ods.matching.OsmMatch;

public interface OsmBuilding extends OsmEntity, Building {
    public static boolean isBuilding(OsmPrimitive primitive) {
        boolean taggedAsBuilding = primitive.hasKey("building") || primitive.hasKey("building:part")
                || primitive.hasKey("no:building");
            boolean validGeometry = (primitive.getDisplayType() == OsmPrimitiveType.CLOSEDWAY
                || primitive.getDisplayType() == OsmPrimitiveType.MULTIPOLYGON
                || primitive.getDisplayType() == OsmPrimitiveType.RELATION);
            return taggedAsBuilding && validGeometry;
    }

    public Long getBuildingId();

    @Override
    public Geometry getGeometry();

    public OsmCity getCity();

    /**
     * Return the address information associated with this building.
     *
     * @return null if no address is associated with the building
     */
    public OsmAddress getAddress();

    /**
     * Return the address nodes associated with this building.
     *
     * @return empty collection if no address nodes are associated with this
     *         building.
     */
    public List<OsmAddressNode> getAddressNodes();

    public Set<OsmBuilding> getNeighbours();

    /**
     * Check is the full area of this building has been loaded. This is true if
     * the building is completely covered by the downloaded area.
     *
     * @return
     */

    public void setStartDate(String string);

    public void setMatch(OsmMatch<OsmBuilding> buildingMatch);
}
