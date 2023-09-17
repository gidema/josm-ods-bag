package org.openstreetmap.josm.plugins.ods.bag.mapping;

import static org.openstreetmap.josm.plugins.ods.bag.entity.BuildingStatus.IN_USE;
import static org.openstreetmap.josm.plugins.ods.bag.entity.PlotStatus.ASSIGNED;
import static org.openstreetmap.josm.plugins.ods.mapping.MatchStatus.COMPARABLE;
import static org.openstreetmap.josm.plugins.ods.mapping.MatchStatus.MATCH;
import static org.openstreetmap.josm.plugins.ods.mapping.MatchStatus.NO_MATCH;

import org.locationtech.jts.geom.Point;
import org.openstreetmap.josm.plugins.ods.bag.entity.BagStaticCaravanPlot;
import org.openstreetmap.josm.plugins.ods.bag.entity.BuildingStatus;
import org.openstreetmap.josm.plugins.ods.bag.entity.PlotStatus;
import org.openstreetmap.josm.plugins.ods.bag.entity.osm.OsmBuilding;
import org.openstreetmap.josm.plugins.ods.mapping.AbstractMapping;
import org.openstreetmap.josm.plugins.ods.mapping.MatchStatus;

public class StaticCaravanPlot2BuildingMapping extends AbstractMapping<OsmBuilding, BagStaticCaravanPlot> {
    /**
     * A double value indicating the match between the areas of the 2 buildings.
     *
     */
    public StaticCaravanPlot2BuildingMapping(OsmBuilding osmBuilding, BagStaticCaravanPlot odPlot) {
        super(osmBuilding, odPlot);
    }

    //    @Override
    //    public Class<Building> getEntityClass() {
    //        return Building.class;
    //    }

    @Override
    public void analyze() {
        var odEntity = getOpenDataEntity();
        if (this.isTwoWay() && this.isSimple() && 
                odEntity.getStatus() != PlotStatus.WITHDRAWN) {
            var areaMatch = compareAreas();
            var centroidMatch = compareCentroids();
            odEntity.setGeometryMatch(MatchStatus.combine(areaMatch, centroidMatch));
            var statusMatch = compareStatuses();
            odEntity.setAttributeMatch(statusMatch);
            odEntity.setStatusMatch(statusMatch);
        }
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
            return NO_MATCH;
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
}