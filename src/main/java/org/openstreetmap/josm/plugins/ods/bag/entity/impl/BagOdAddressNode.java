package org.openstreetmap.josm.plugins.ods.bag.entity.impl;

import static org.openstreetmap.josm.plugins.ods.entities.Entity.Completeness.Unknown;

import org.locationtech.jts.geom.Point;
import org.openstreetmap.josm.plugins.ods.bag.entity.AddressableObjectStatus;
import org.openstreetmap.josm.plugins.ods.bag.entity.BagAddressableObject;
import org.openstreetmap.josm.plugins.ods.bag.entity.NLAddress;
import org.openstreetmap.josm.plugins.ods.bag.entity.NlHouseNumber;
import org.openstreetmap.josm.plugins.ods.bag.entity.OdAddressNode;
import org.openstreetmap.josm.plugins.ods.entities.impl.AbstractOdEntity;
import org.openstreetmap.josm.plugins.ods.update.UpdateTaskType;
import org.openstreetmap.josm.tools.I18n;
import org.openstreetmap.josm.tools.Logging;

public class BagOdAddressNode extends AbstractOdEntity implements OdAddressNode {
    private Long addressId;
    private NLAddress address;
    private Long buildingRef;
    private BagAddressableObject addressableObject;
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
        return addressableObject == null ? Unknown : addressableObject.getCompleteness();
    }

    @Override
    public Long getBuildingRef() {
        return buildingRef;
    }

    public void setBuildingRef(Long buildingRef) {
        this.buildingRef = buildingRef;
    }

    @Override
    public void setAddressableObject(BagAddressableObject addressableObject) {
        this.addressableObject = addressableObject;
    }

    @Override
    public BagAddressableObject getAddressableObject() {
        return addressableObject;
    }

    public void setGeometry(Point point) {
        super.setGeometry(point);
    }

    @Override
    public boolean readyForImport() {
        switch (getStatus()) {
        case IN_USE:
        case IN_USE_NOT_MEASURED:
        case ASSIGNED:
        case CONSTRUCTION:
            return true;
        case PLANNED:
            // Import planned addresses if the building is under construction
            if (addressableObject != null && addressableObject.getAddressableStatus().equals(AddressableObjectStatus.CONSTRUCTION)) {
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
    public String getStatusTag() {
        return getStatus().toString();
    }

    @Override
    public AddressableObjectStatus getStatus() {
        if (getAddressableObject() == null) {
            return AddressableObjectStatus.UNKNOWN;
        }
        switch (getAddressableObject().getAddressableStatus()) {
        case CONSTRUCTION:
            return AddressableObjectStatus.CONSTRUCTION;
        case INADVERTENTLY_CREATED:
            return AddressableObjectStatus.INADVERTENTLY_CREATED;
        case IN_USE:
            return AddressableObjectStatus.IN_USE;
        case IN_USE_NOT_MEASURED:
            return AddressableObjectStatus.IN_USE_NOT_MEASURED;
        case NOT_CARRIED_THROUGH:
            return AddressableObjectStatus.NOT_CARRIED_THROUGH;
        case PLANNED:
            return AddressableObjectStatus.PLANNED;
        case RECONSTRUCTION:
            return AddressableObjectStatus.RECONSTRUCTION;
        case REMOVAL_DUE:
            return AddressableObjectStatus.REMOVAL_DUE;
        case REMOVED:
            return AddressableObjectStatus.REMOVED;
        case ASSIGNED:
            return AddressableObjectStatus.ASSIGNED;
        case WITHDRAWN:
            return AddressableObjectStatus.WITHDRAWN;
        default:
            return AddressableObjectStatus.UNKNOWN;
        }
    }

    public boolean isSecondary() {
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
    public UpdateTaskType getUpdateTaskType() {
        if (getMapping().getOsmEntities().isEmpty() && readyForImport()) {
            return UpdateTaskType.ADD;
        }
        return UpdateTaskType.NONE;
    }

    @Override
    public void refreshUpdateTags() {
        super.refreshUpdateTags();
    }

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
