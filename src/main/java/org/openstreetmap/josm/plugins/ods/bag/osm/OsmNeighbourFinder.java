package org.openstreetmap.josm.plugins.ods.bag.osm;

import java.util.function.Predicate;

import org.openstreetmap.josm.data.osm.BBox;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.data.osm.OsmPrimitiveType;
import org.openstreetmap.josm.data.osm.Relation;
import org.openstreetmap.josm.data.osm.Way;
import org.openstreetmap.josm.plugins.ods.bag.BagImportModule;
import org.openstreetmap.josm.plugins.ods.bag.enrichment.osm.OsmBuildingAligner;
import org.openstreetmap.josm.plugins.ods.context.OdsContext;

/**
 * Find neighbours for a OdBuilding using the Osm primitive.
 *
 * @author Gertjan Idema <mail@gertjanidema.nl>
 *
 */
public class OsmNeighbourFinder {
    private final Predicate<OsmPrimitive> isBuilding = (primitive) -> {
        return primitive.hasKey("building") || primitive.hasKey("building:part")
                || primitive.hasKey("no:building");
    };
    private final OsmBuildingAligner osmBuildingAligner;
    private final OdsContext context;

    public OsmNeighbourFinder(OdsContext context) {
        super();
        this.context = context;
        this.osmBuildingAligner = new OsmBuildingAligner(context);
    }

    public void findNeighbours(OsmPrimitive osm) {
        if (!isBuilding.test(osm)) {
            return;
        }
        if (osm.getDisplayType().equals(OsmPrimitiveType.CLOSEDWAY)) {
            findWayNeighbourBuildings((Way)osm);
        }
    }

    public void findWayNeighbourBuildings(Way way1) {
        // TODO get the tolerance from the context
        BBox bbox = extend(way1.getBBox(), context.getParameter(BagImportModule.BuildingAlignmentTolerance));
        for (Way way2 : way1.getDataSet().searchWays(bbox)) {
            if (way2.equals(way1)) {
                continue;
            }
            if (isBuilding.test(way2)) {
                osmBuildingAligner.align(way1, way2);
                //                PolygonIntersection pi = Geometry.polygonIntersection(way1.getNodes(), way2.getNodes());
                //                if (pi.equals(PolygonIntersection.CROSSING)) {
                //                    neighbourBuildings.add(way2);
                //                }
            }
            for (OsmPrimitive osm2 :way2.getReferrers()) {
                Relation relation = (Relation)osm2;
                if (isBuilding.test(relation)) {
                    osmBuildingAligner.align(way1, way1);
                    //                    neighbourBuildings.add(relation);
                }
            }
        }
    }

    private static BBox extend(BBox bbox, Double delta) {
        return new BBox(bbox.getTopLeftLon() - delta,
                bbox.getBottomRightLat() - delta,
                bbox.getBottomRightLon() + delta,
                bbox.getTopLeftLat() + delta);
    }
}
