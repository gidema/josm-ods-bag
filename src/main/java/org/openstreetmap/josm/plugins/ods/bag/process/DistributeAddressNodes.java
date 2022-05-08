package org.openstreetmap.josm.plugins.ods.bag.process;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.LineSegment;
import org.locationtech.jts.geom.Point;
import org.openstreetmap.josm.plugins.ods.bag.entity.BagBuilding;
import org.openstreetmap.josm.plugins.ods.bag.entity.BagBuildingUnit;
import org.openstreetmap.josm.plugins.ods.bag.entity.NLAddress;
import org.openstreetmap.josm.plugins.ods.bag.entity.OdAddressNode;
import org.openstreetmap.josm.plugins.ods.bag.entity.impl.AddressNodeGroup;
import org.openstreetmap.josm.plugins.ods.bag.entity.storage.BagBuildingStore;
import org.openstreetmap.josm.plugins.ods.context.OdsContext;
import org.openstreetmap.josm.plugins.ods.context.OdsContextJob;
import org.openstreetmap.josm.plugins.ods.jts.GeoUtil;

/**
 * This enricher finds overlapping nodes in the data and distibutes them, so
 * they are no longer overlapping. The MatchAddressToBuildingTask must run
 * before this class, so when can distribute over the line pointing to the
 * center of the building.
 *
 * @author Gertjan Idema <mail@gertjanidema.nl>
 *
 */
public class DistributeAddressNodes implements OdsContextJob {
    private final AddressNodeComparator comparator = new AddressNodeComparator();

    public DistributeAddressNodes() {
        super();
    }

    
    @Override
    public void run(OdsContext context) {
        BagBuildingStore buildingStore = context.getComponent(BagBuildingStore.class);
        GeoUtil geoUtil = context.getComponent(GeoUtil.class);
        buildingStore.forEach(building -> {
            for (AddressNodeGroup group : buildGroups(building).values()) {
                if (group.getAddressNodes().size() > 1) {
                    distribute(group, geoUtil);
                }
            }
        });
    }

    /**
     * Analyze all new address nodes and group them by Geometry (Point)
     *
     * @param newEntities
     */
    private static Map<Point, AddressNodeGroup> buildGroups(BagBuilding building) {
        Map<Point, AddressNodeGroup> groups = new HashMap<>();
        Iterator<BagBuildingUnit> it = building.getBuildingUnits().values().iterator();
        while (it.hasNext()) {
            BagBuildingUnit buildingUnit = it.next();
            if (buildingUnit.getBuilding() == null) {
                continue;
            }
            AddressNodeGroup group = groups.get(buildingUnit.getGeometry());
            OdAddressNode mainAddressNode = buildingUnit.getMainAddressNode();
            if (group == null) {
                group = new AddressNodeGroup(mainAddressNode);
                groups.put(mainAddressNode.getGeometry(), group);
            } else {
                group.addAddressNode(mainAddressNode);
            }
        }
        return groups;
    }

    private void distribute(AddressNodeGroup group, GeoUtil geoUtil) {
        List<OdAddressNode> nodes = group.getAddressNodes();
        Collections.sort(nodes, comparator.reversed());
        if (group.getBuilding().getGeometry().isEmpty()) {
            // Happens rarely,
            // for now return to prevent null pointer Exception
            return;
        }
        Point center = group.getBuilding().getGeometry().getCentroid();
        LineSegment ls = new LineSegment(group.getGeometry().getCoordinate(),
                center.getCoordinate());
        double angle = ls.angle();
        double dx = Math.cos(angle) * 2e-7;
        double dy = Math.sin(angle) * 2e-7;
        double x = group.getGeometry().getX();
        double y = group.getGeometry().getY();
        for (OdAddressNode node : nodes) {
            Point point = geoUtil.toPoint(new Coordinate(x, y));
            node.setGeometry(point);
            x = x + dx;
            y = y + dy;
        }
    }

    private class AddressNodeComparator implements Comparator<OdAddressNode> {
        public AddressNodeComparator() {
            // TODO Auto-generated constructor stub
        }

        @Override
        public int compare(OdAddressNode an1, OdAddressNode an2) {
            if (an1 == null || an2 == null) {
                return 0;
            }
            return compare(an1.getAddress(), an2.getAddress());
        }

        public int compare(NLAddress a1, NLAddress a2) {
            int result = Objects.compare(a2.getCityName(), a1.getCityName(), String.CASE_INSENSITIVE_ORDER);
            if (result == 0 && a2.getPostcode() != null && a1.getPostcode() != null) {
                result = Objects.compare(a2.getPostcode(), a1.getPostcode(), String.CASE_INSENSITIVE_ORDER);
            }
            if (result == 0) {
                result = Objects.compare(a2.getStreetName(), a1.getStreetName(), String.CASE_INSENSITIVE_ORDER);
            }
            if (result == 0) {
                result = a1.getHouseNumber().compareTo(a2.getHouseNumber());
            }
            return result;
        }
    }
}
