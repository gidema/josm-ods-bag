package org.openstreetmap.josm.plugins.ods.bag.entity.impl;

import static org.openstreetmap.josm.plugins.ods.entities.Entity.Completeness.Unknown;

import org.locationtech.jts.geom.Point;
import org.openstreetmap.josm.plugins.ods.bag.entity.BagBuilding;
import org.openstreetmap.josm.plugins.ods.bag.entity.BagBuildingUnit;
import org.openstreetmap.josm.plugins.ods.bag.entity.NLAddress;
import org.openstreetmap.josm.plugins.ods.bag.entity.NlHouseNumber;
import org.openstreetmap.josm.plugins.ods.bag.entity.OdAddressNode;
import org.openstreetmap.josm.plugins.ods.bag.match.AddressNodeMatch;
import org.openstreetmap.josm.plugins.ods.entities.EntityStatus;
import org.openstreetmap.josm.plugins.ods.entities.impl.AbstractOdEntity;
import org.openstreetmap.josm.tools.I18n;
import org.openstreetmap.josm.tools.Logging;

public class BagOdAddressNode extends AbstractOdEntity implements OdAddressNode {
    private Long addressId;
    private NLAddress address;
    private Long buildingRef;
    private BagBuildingUnit buildingUnit;
    private BagBuilding building;
    private AddressNodeMatch match;
    private boolean secondary = false;

    public void setAddressId(Long addressId) {
        this.addressId = addressId;
    }

    public void setAddress(NLAddress address) {
        this.address = address;
    }

    @Override
    public Long getAddressId() {
        return addressId;
    }

    @Override
    public NLAddress getAddress() {
        return address;
    }

    @Override
    public Completeness getCompleteness() {
        return building == null ? Unknown : building.getCompleteness();
    }

    @Override
    public Long getBuildingRef() {
        return buildingRef;
    }

    public void setBuildingRef(Long buildingRef) {
        this.buildingRef = buildingRef;
    }

    public BagBuildingUnit getBuildingUnit() {
        return buildingUnit;
    }

    public void setBuildingUnit(BagBuildingUnit buildingUnit) {
        this.buildingUnit = buildingUnit;
    }

    @Override
    public void setBuilding(BagBuilding building) {
        this.building = building;
    }

    @Override
    public BagBuilding getBuilding() {
        return buildingUnit != null ? buildingUnit.getBuilding() : building;
    }

    public void setGeometry(Point point) {
        super.setGeometry(point);
    }

    @Override
    public boolean readyForImport() {
        switch (getStatus()) {
        case IN_USE:
        case IN_USE_NOT_MEASURED:
        case CONSTRUCTION:
            return true;
        case PLANNED:
            // Import planned addresses if the building is under construction
            if (building != null && building.getStatus().equals(EntityStatus.CONSTRUCTION)) {
                return true;
            }
            return false;
        default:
            return false;
        }
    }
    
    @Override
    public Point getGeometry() {
        try {
            return (Point) super.getGeometry();
        }
        catch (ClassCastException e) {
            Logging.warn(I18n.tr("The geometry of {0} is not a point", toString()));
            return super.getGeometry().getCentroid();
        }
    }

    @Override
    public AddressNodeMatch getMatch() {
        return match;
    }

    @Override
    public void setMatch(AddressNodeMatch match) {
        this.match = match;
    }

    public boolean isSecondarys() {
        return secondary;
    }

    public void setSecondary(boolean secondary) {
        this.secondary = secondary;
    }

    public NlHouseNumber getHouseNumber() {
        return getAddress().getHouseNumber();
    }
    
    //    @Override
    //    public int compareTo(Address o) {
    //        return getAddress().compareTo(o);
    //    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getAddress());
        if (getStatus() != null) {
            sb.append(" (").append(getStatus()).append(")");
        }
        return sb.toString();
    }
}
