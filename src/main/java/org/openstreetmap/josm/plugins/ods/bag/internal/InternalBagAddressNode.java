package org.openstreetmap.josm.plugins.ods.bag.internal;

import java.util.HashMap;
import java.util.Map;

import org.openstreetmap.josm.command.Command;
import org.openstreetmap.josm.command.MoveCommand;
import org.openstreetmap.josm.data.coor.LatLon;
import org.openstreetmap.josm.data.osm.Node;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.data.osm.OsmPrimitiveType;
import org.openstreetmap.josm.plugins.ods.crs.InvalidGeometryException;
import org.openstreetmap.josm.plugins.ods.entities.Entity;
import org.openstreetmap.josm.plugins.ods.entities.builtenvironment.Address;
import org.openstreetmap.josm.plugins.ods.entities.builtenvironment.AddressNode;
import org.openstreetmap.josm.plugins.ods.entities.builtenvironment.Block;
import org.openstreetmap.josm.plugins.ods.entities.builtenvironment.Building;
import org.openstreetmap.josm.plugins.ods.jts.GeoUtil;

import com.vividsolutions.jts.geom.Point;

public class InternalBagAddressNode extends InternalBagEntity implements
        AddressNode {
    private Building building;
    private InternalBagAddress address;

    private Map<String, String> addressKeys = new HashMap<>();

    public InternalBagAddressNode(OsmPrimitive primitive) {
        super(primitive);
        address = new InternalBagAddress(primitive);
    }

    @Override
    public boolean hasGeometry() {
        return true;
    }

    @Override
    public boolean hasReferenceId() {
        return false;
    }

    @Override
    public Address getAddress() {
        return address;
    }

    @Override
    public Class<? extends Entity> getType() {
        return AddressNode.class;
    }

    @Override
    public boolean isIncomplete() {
        return false;
    }

    @Override
    protected boolean parseKey(String key, String value) {
        if (super.parseKey(key, value)) {
            return true;
        }
        if ("addr:housenumber".equals(key)) {
            address = new InternalBagAddress(primitive);
            address.build();
            return true;
        }
        if ("address:street".equals(key) || "address:housename".equals(key)
                || "address:city".equals(key) || "address:postcode".equals(key)) {
            // Save other address keys in case address:housenumber is missing
            addressKeys.put(key, value);
            return true;
        }
        return false;
    }

    @Override
    public Block getBlock() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Object getBuildingRef() {
        if (building != null) {
            return building.getId();
        }
        return null;
    }

    @Override
    public void setBuilding(Building building) {
        this.building = building;
    }

    @Override
    public Building getBuilding() {
        return building;
    }

    @Override
    protected void buildGeometry() throws InvalidGeometryException {
        if (primitive.getDisplayType() == OsmPrimitiveType.NODE) {
            buildGeometry((Node) primitive);
        }
    }

    private void buildGeometry(Node node) throws IllegalArgumentException {
        GeoUtil geoUtil = GeoUtil.getInstance();
        geometry = geoUtil.toPoint(node);
    }

    @Override
    public Command updateGeometry(Point point) {
        this.geometry = point;
        OsmPrimitive osm = this.getPrimitive();
        if (osm.getType() == OsmPrimitiveType.NODE) {
            Node node = (Node)osm;
            LatLon latLon = GeoUtil.getInstance().toLatLon(point);
            node.setCoor(latLon);
            return new MoveCommand(node, latLon);
        }
        return null;
    }
    
    @Override
    public void setGeometry(Point geometry) {
        this.geometry = geometry;
    }
    
    @Override
    public Point getGeometry() {
        return (Point) geometry;
    }

    @Override
    public int compareTo(AddressNode an) {
        return getAddress().compareTo(an.getAddress());
    }
    
    
}
