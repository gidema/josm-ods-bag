package org.openstreetmap.josm.plugins.ods.bag.enrichment.osm;

import java.util.LinkedList;
import java.util.List;

import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.data.osm.OsmPrimitiveType;
import org.openstreetmap.josm.data.osm.Relation;
import org.openstreetmap.josm.data.osm.RelationMember;
import org.openstreetmap.josm.data.osm.Way;
import org.openstreetmap.josm.plugins.ods.bag.BagImportModule;
import org.openstreetmap.josm.plugins.ods.bag.entity.osm.OsmBuilding;
import org.openstreetmap.josm.plugins.ods.bag.entity.osm.OsmBuildingStore;
import org.openstreetmap.josm.plugins.ods.context.OdsContext;
import org.openstreetmap.josm.plugins.ods.osm.NodeDWithin;
import org.openstreetmap.josm.plugins.ods.osm.NodeDWithinLatLon;
import org.openstreetmap.josm.plugins.ods.osm.WayAligner;

public class OsmBuildingAligner {
    private final NodeDWithin dWithin;
    private boolean undoable;
    private final OsmBuildingStore buildingStore;

    public OsmBuildingAligner(OdsContext context) {
        this.buildingStore = context.getComponent(OsmBuildingStore.class);
        Double tolerance = context.getParameter(BagImportModule.BuildingAlignmentTolerance);
        this.dWithin = new NodeDWithinLatLon(tolerance);
    }

    public void align(OsmBuilding building) {
        for (OsmBuilding candidate : buildingStore.getGeoIndex().intersection(building.getGeometry())) {
            if (candidate == building) {
                continue;
            }
            if (building.getNeighbours().contains(candidate)) continue;
            building.getNeighbours().add(candidate);
            candidate.getNeighbours().add(building);
            align(building, candidate);
        }
    }

    public void align(OsmBuilding b1, OsmBuilding b2) {
        align(b1.getPrimitive(), b2.getPrimitive());
    }

    public void align(OsmPrimitive osm1, OsmPrimitive osm2) {
        if (osm1 == null || osm2 == null) return;
        Way outerWay1 = getOuterWay(osm1);
        Way outerWay2 = getOuterWay(osm2);
        if (outerWay1 != null && outerWay2 != null) {
            WayAligner wayAligner = new WayAligner(outerWay1, outerWay2, dWithin, undoable);
            wayAligner.run();
        }
    }

    private static Way getOuterWay(OsmPrimitive osm) {
        if (osm.getType() == OsmPrimitiveType.WAY) {
            Way way = (Way)osm;
            if (way.isClosed()) return way;
            return null;
        }
        if (osm.getType() == OsmPrimitiveType.RELATION) {
            List<Way> outerWays = new LinkedList<>();
            for (RelationMember member : ((Relation)osm).getMembers()) {
                if ("outer".equals(member.getRole()) && member.getDisplayType() == OsmPrimitiveType.CLOSEDWAY) {
                    outerWays.add(member.getWay());
                }
            }
            if (outerWays.size() == 1) {
                Way outerWay = outerWays.get(0);
                if (outerWay.isClosed()) {
                    return outerWay;
                }
            }
        }
        return null;
    }
}
