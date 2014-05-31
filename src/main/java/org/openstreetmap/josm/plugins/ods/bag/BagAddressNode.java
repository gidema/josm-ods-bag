package org.openstreetmap.josm.plugins.ods.bag;

import org.openstreetmap.josm.command.Command;
import org.openstreetmap.josm.plugins.ods.builtenvironment.Address;
import org.openstreetmap.josm.plugins.ods.builtenvironment.AddressNode;
import org.openstreetmap.josm.plugins.ods.builtenvironment.Building;
import org.openstreetmap.josm.plugins.ods.entities.Entity;

import com.vividsolutions.jts.geom.Point;

public class BagAddressNode extends BagEntity implements AddressNode {
    private BagAddress address;
    private Long buildingRef;
    private Building building;
    
    @Override
    public Class<? extends Entity> getType() {
        return AddressNode.class;
    }

    
    @Override
    public boolean isIncomplete() {
        if (building != null) {
            return building.isIncomplete();
        }
        return super.isIncomplete();
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
    public Long getBuildingRef() {
        return buildingRef;
    }

    public void setBuildingRef(Long buildingRef) {
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
    
    @Override
    public String toString() {
        return getAddress().toString();
    }
}
