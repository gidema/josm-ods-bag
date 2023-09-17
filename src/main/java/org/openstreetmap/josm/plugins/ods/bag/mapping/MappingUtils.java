package org.openstreetmap.josm.plugins.ods.bag.mapping;

import static org.openstreetmap.josm.plugins.ods.mapping.MatchStatus.COMPARABLE;
import static org.openstreetmap.josm.plugins.ods.mapping.MatchStatus.MATCH;
import static org.openstreetmap.josm.plugins.ods.mapping.MatchStatus.NO_MATCH;

import org.locationtech.jts.geom.Point;
import org.openstreetmap.josm.plugins.ods.entities.OdEntity;
import org.openstreetmap.josm.plugins.ods.entities.OsmEntity;
import org.openstreetmap.josm.plugins.ods.mapping.MatchStatus;

public class MappingUtils {

    public static MatchStatus compareGeometry(OsmEntity osmEntity, OdEntity odEntity) {
        return MatchStatus.combine(compareAreas(osmEntity, odEntity), compareCentroids(osmEntity, odEntity));
    }
    
    public static MatchStatus compareAreas(OsmEntity osmEntity, OdEntity odEntity) {
        double osmArea = osmEntity.getGeometry().getArea();
        double odArea = odEntity.getGeometry().getArea();
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

    public static MatchStatus compareCentroids(OsmEntity osmEntity, OdEntity odEntity) {
        Point osmCentroid = osmEntity.getGeometry().getCentroid();
        Point odCentroid = odEntity.getGeometry().getCentroid();
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
