package org.openstreetmap.josm.plugins.ods.bag.entity;

public enum BuildingStatus {
    UNKNOWN, PLANNED, CONSTRUCTION, IN_USE, IN_USE_NOT_MEASURED, NOT_CARRIED_THROUGH, REMOVAL_DUE, REMOVED;

    @Override
    public String toString() {
        return super.toString().toLowerCase();
    }
}
