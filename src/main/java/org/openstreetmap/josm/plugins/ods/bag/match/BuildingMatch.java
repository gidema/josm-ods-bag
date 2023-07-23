package org.openstreetmap.josm.plugins.ods.bag.match;

import static org.openstreetmap.josm.plugins.ods.bag.entity.BuildingStatus.CONSTRUCTION;
import static org.openstreetmap.josm.plugins.ods.bag.entity.BuildingStatus.IN_USE;
import static org.openstreetmap.josm.plugins.ods.bag.entity.BuildingStatus.IN_USE_NOT_MEASURED;
import static org.openstreetmap.josm.plugins.ods.bag.entity.BuildingStatus.PLANNED;
import static org.openstreetmap.josm.plugins.ods.matching.MatchStatus.COMPARABLE;
import static org.openstreetmap.josm.plugins.ods.matching.MatchStatus.MATCH;
import static org.openstreetmap.josm.plugins.ods.matching.MatchStatus.NO_MATCH;
import static org.openstreetmap.josm.plugins.ods.matching.MatchStatus.combine;

import java.util.Objects;

import org.openstreetmap.josm.plugins.ods.bag.entity.BagBuilding;
import org.openstreetmap.josm.plugins.ods.bag.entity.BuildingStatus;
import org.openstreetmap.josm.plugins.ods.bag.entity.osm.OsmBuilding;
import org.openstreetmap.josm.plugins.ods.matching.MatchImpl;
import org.openstreetmap.josm.plugins.ods.matching.MatchStatus;
import org.locationtech.jts.geom.Point;

public class BuildingMatch extends MatchImpl<OsmBuilding, BagBuilding> {
    /**
     * A double value indicating the match between the areas of the 2 buildings.
     *
     */
    private MatchStatus areaMatch;
    private MatchStatus centroidMatch;
    private MatchStatus startDateMatch;
    private MatchStatus statusMatch;

    public BuildingMatch(OsmBuilding osmBuilding, BagBuilding odBuilding) {
        super(osmBuilding, odBuilding);
        osmBuilding.setMatch(this);
        odBuilding.setMatch(this);
    }

    //    @Override
    //    public Class<Building> getEntityClass() {
    //        return Building.class;
    //    }

    @Override
    public void analyze() {
        areaMatch = compareAreas();
        centroidMatch = compareCentroids();
        startDateMatch = compareStartDates();
        statusMatch = compareStatuses();
    }

    private MatchStatus compareStartDates() {
        if (Objects.equals(getOsmEntity().getStartDate(), getOpenDataEntity().getStartDate())) {
            return MATCH;
        }
        return NO_MATCH;
    }

    private MatchStatus compareStatuses() {
        BuildingStatus osmStatus = getOsmEntity().getStatus();
        BuildingStatus odStatus = getOpenDataEntity().getStatus();
        if (osmStatus.equals(odStatus)) {
            return MATCH;
        }
        if (osmStatus.equals(IN_USE) && odStatus.equals(IN_USE_NOT_MEASURED)) {
            return MATCH;
        }
        if (odStatus.equals(PLANNED) && osmStatus.equals(CONSTRUCTION)) {
            return COMPARABLE;
        }
        if (odStatus.equals(CONSTRUCTION) &&
                (osmStatus.equals(IN_USE) || osmStatus.equals(IN_USE_NOT_MEASURED))) {
            return COMPARABLE;
        }
        return NO_MATCH;
    }

    private MatchStatus compareAreas() {
        double osmArea = getOsmEntity().getGeometry().getArea();
        double odArea = getOpenDataEntity().getGeometry().getArea();
        if (osmArea == 0.0 || odArea == 0.0) {
            areaMatch = NO_MATCH;
        }
        double match = (osmArea - odArea) / osmArea;
        if (match == 0.0) {
            return MATCH;
        }
        if (Math.abs(match) < 0.01) {
            return COMPARABLE;
        }
        return NO_MATCH;
    }

    private MatchStatus compareCentroids() {
        Point osmCentroid = getOsmEntity().getGeometry().getCentroid();
        Point odCentroid = getOpenDataEntity().getGeometry().getCentroid();
        double centroidDistance = osmCentroid.distance(odCentroid);
        if (centroidDistance == 0) {
            return MATCH;
        }
        if (centroidDistance < 1e-5) {
            return COMPARABLE;
        }
        return NO_MATCH;
    }

    @Override
    public MatchStatus getGeometryMatch() {
        return combine(areaMatch, centroidMatch);
    }

    @Override
    public MatchStatus getStatusMatch() {
        return statusMatch;
    }

    @Override
    public MatchStatus getAttributeMatch() {
        return startDateMatch;
    }
}