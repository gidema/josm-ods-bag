package org.openstreetmap.josm.plugins.ods.bag.match;

import static org.openstreetmap.josm.plugins.ods.bag.entity.BuildingStatus.IN_USE;
import static org.openstreetmap.josm.plugins.ods.bag.entity.PlotStatus.ASSIGNED;
import static org.openstreetmap.josm.plugins.ods.matching.MatchStatus.COMPARABLE;
import static org.openstreetmap.josm.plugins.ods.matching.MatchStatus.MATCH;
import static org.openstreetmap.josm.plugins.ods.matching.MatchStatus.NO_MATCH;
import static org.openstreetmap.josm.plugins.ods.matching.MatchStatus.combine;

import org.locationtech.jts.geom.Point;
import org.openstreetmap.josm.plugins.ods.bag.entity.BagStaticCaravanPlot;
import org.openstreetmap.josm.plugins.ods.bag.entity.BuildingStatus;
import org.openstreetmap.josm.plugins.ods.bag.entity.PlotStatus;
import org.openstreetmap.josm.plugins.ods.bag.entity.osm.OsmBuilding;
import org.openstreetmap.josm.plugins.ods.matching.MatchImpl;
import org.openstreetmap.josm.plugins.ods.matching.MatchStatus;

public class StaticCaravanPlot2BuildingMatch extends MatchImpl<OsmBuilding, BagStaticCaravanPlot> {
    /**
     * A double value indicating the match between the areas of the 2 buildings.
     *
     */
    private MatchStatus areaMatch;
    private MatchStatus centroidMatch;
    private MatchStatus statusMatch;

    public StaticCaravanPlot2BuildingMatch(OsmBuilding osmBuilding, BagStaticCaravanPlot odPlot) {
        super(osmBuilding, odPlot);
        osmBuilding.setMatch(this);
        odPlot.setMatch(this);
    }

    //    @Override
    //    public Class<Building> getEntityClass() {
    //        return Building.class;
    //    }

    @Override
    public void analyze() {
        areaMatch = compareAreas();
        centroidMatch = compareCentroids();
        statusMatch = compareStatuses();
    }

    private MatchStatus compareStatuses() {
        BuildingStatus osmStatus = getOsmEntity().getStatus();
        PlotStatus odStatus = getOpenDataEntity().getStatus();
        if (osmStatus.equals(IN_USE) && odStatus.equals(ASSIGNED)) {
            return MATCH;
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
        return MatchStatus.MATCH;
    }
}