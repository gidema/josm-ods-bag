package org.openstreetmap.josm.plugins.ods.bag.entity;

public enum BuildingUnitStatus {
    UNKNOWN, PLANNED, CONSTRUCTION, IN_USE, IN_USE_NOT_MEASURED, NOT_REALIZED, REMOVAL_DUE, REMOVED, RECONSTRUCTION, INADVERTENTLY_CREATED;

    @Override
    public String toString() {
        return super.toString().toLowerCase();
    }
}
