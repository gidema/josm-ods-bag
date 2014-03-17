package org.openstreetmap.josm.plugins.ods.bag;

import org.openstreetmap.josm.command.Command;
import org.openstreetmap.josm.plugins.ods.entities.Entity;
import org.openstreetmap.josm.plugins.ods.entities.builtenvironment.Address;
import org.openstreetmap.josm.plugins.ods.entities.builtenvironment.AddressNode;
import org.openstreetmap.josm.plugins.ods.entities.builtenvironment.Building;

import com.vividsolutions.jts.geom.Point;

public class BagAddressNode extends BagEntity implements AddressNode {
    private BagAddress address;
    private Object buildingRef;
    private Building building;
    
    @Override
    public Class<? extends Entity> getType() {
        return AddressNode.class;
    }

    @Override
    public int compareTo(AddressNode o) {
        // TODO Auto-generated method stub
        return 0;
    }

    public void setAddress(BagAddress address) {
        this.address = address;
    }

    @Override
    public Address getAddress() {
        return address;
    }

//    @Override
//    public Block getBlock() {
//        // TODO Auto-generated method stub
//        return null;
//    }

    @Override
    public Object getBuildingRef() {
        return buildingRef;
    }

    public void setBuildingRef(Object buildingRef) {
        this.buildingRef = buildingRef;
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
    public void setGeometry(Point point) {
        super.setGeometry(point);
    }

    @Override
    public Command updateGeometry(Point point) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Point getGeometry() {
        return (Point) super.getGeometry();
    }
}
