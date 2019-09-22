package org.openstreetmap.josm.plugins.ods.bag.gt;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

import org.openstreetmap.josm.plugins.ods.domains.buildings.OdAddress;
import org.openstreetmap.josm.plugins.ods.domains.buildings.OdAddressNode;
import org.openstreetmap.josm.plugins.ods.domains.buildings.OdBuilding;
import org.openstreetmap.josm.plugins.ods.domains.buildings.impl.AddressNodeGroup;
import org.openstreetmap.josm.plugins.ods.jts.GeoUtil;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.LineSegment;
import com.vividsolutions.jts.geom.Point;

/**
 * This enricher finds overlapping nodes in the data and distibutes them, so
 * they are no longer overlapping. The MatchAddressToBuildingTask must run
 * before this class, so when can distribute over the line pointing to the
 * center of the building.
 *
 * @author Gertjan Idema <mail@gertjanidema.nl>
 *
 */
public class DistributeAddressNodes implements Consumer<OdBuilding> {
    private final GeoUtil geoUtil;
    private final AddressNodeComparator comparator = new AddressNodeComparator();

    public DistributeAddressNodes(GeoUtil geoUtil) {
        super();
        this.geoUtil = geoUtil;
    }

    @Override
    public void accept(OdBuilding building) {
        for (AddressNodeGroup group : buildGroups(building).values()) {
            if (group.getAddressNodes().size() > 1) {
                distribute(group);
            }
        }
    }

    /**
     * Analyze all new address nodes and group them by Geometry (Point)
     *
     * @param newEntities
     */
    private static Map<Point, AddressNodeGroup> buildGroups(OdBuilding building) {
        Map<Point, AddressNodeGroup> groups = new HashMap<>();
        Iterator<OdAddressNode> it = building.getAddressNodes().iterator();
        while (it.hasNext()) {
            OdAddressNode addressNode = it.next();
            AddressNodeGroup group = groups.get(addressNode.getGeometry());
            if (group == null) {
                group = new AddressNodeGroup(addressNode);
                groups.put(addressNode.getGeometry(), group);
            } else {
                group.addAddressNode(addressNode);
            }
        }
        return groups;
    }

    private void distribute(AddressNodeGroup group) {
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

        public int compare(OdAddress a1, OdAddress a2) {
            int result = Objects.compare(a2.getCityName(), a1.getCityName(), String.CASE_INSENSITIVE_ORDER);
            if (result == 0 && a2.getPostcode() != null && a1.getPostcode() != null) {
                result = Objects.compare(a2.getPostcode(), a1.getPostcode(), String.CASE_INSENSITIVE_ORDER);
            }
            if (result == 0) {
                result = Objects.compare(a2.getStreetName(), a1.getStreetName(), String.CASE_INSENSITIVE_ORDER);
            }
            if (result == 0) {
                result = Objects.compare(a2.getFullHouseNumber(), a1.getFullHouseNumber(), String.CASE_INSENSITIVE_ORDER);
            }
            return result;
        }
    }
}
