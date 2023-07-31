package org.openstreetmap.josm.plugins.ods.bag.entity;

public enum BuildingUnitStatus {
    UNKNOWN, PLANNED, CONSTRUCTION, IN_USE, IN_USE_NOT_MEASURED, NOT_REALIZED, WITHDRAWN, RECONSTRUCTION, INADVERTENTLY_CREATED, OUT_OF_USE;

    public static BuildingUnitStatus parse(String status) {
        switch (status) {
        case "Verblijfsobject gevormd":
            return BuildingUnitStatus.PLANNED;
        case "Verblijfsobject in gebruik":
            return BuildingUnitStatus.IN_USE;
        case "Verblijfsobject buiten gebruik":
            return BuildingUnitStatus.OUT_OF_USE;
        case "Verblijfsobject in gebruik (niet ingemeten)":
            return BuildingUnitStatus.IN_USE_NOT_MEASURED;
        case "Verbouwing verblijfsobject":
            return BuildingUnitStatus.RECONSTRUCTION;
        case "Verblijfsobject ingetrokken":
            return BuildingUnitStatus.WITHDRAWN;
        case "Niet gerealiseerd verblijfsobject":
            return BuildingUnitStatus.NOT_REALIZED;
        case "Verblijfsobject ten onrechte opgevoerd":
            return BuildingUnitStatus.INADVERTENTLY_CREATED;
        default:
            return BuildingUnitStatus.IN_USE;
        }
    }
    
    @Override
    public String toString() {
        return super.toString().toLowerCase();
    }
}
