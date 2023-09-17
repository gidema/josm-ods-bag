package org.openstreetmap.josm.plugins.ods.bag.mapping;

import static org.openstreetmap.josm.plugins.ods.bag.entity.BuildingStatus.CONSTRUCTION;
import static org.openstreetmap.josm.plugins.ods.bag.entity.BuildingStatus.IN_USE;
import static org.openstreetmap.josm.plugins.ods.bag.entity.BuildingStatus.IN_USE_NOT_MEASURED;
import static org.openstreetmap.josm.plugins.ods.bag.entity.BuildingStatus.PLANNED;
import static org.openstreetmap.josm.plugins.ods.mapping.MatchStatus.MATCH;
import static org.openstreetmap.josm.plugins.ods.mapping.MatchStatus.NO_MATCH;

import java.util.Objects;

import org.openstreetmap.josm.plugins.ods.bag.entity.BagBuilding;
import org.openstreetmap.josm.plugins.ods.bag.entity.BuildingStatus;
import org.openstreetmap.josm.plugins.ods.bag.entity.osm.OsmBuilding;
import org.openstreetmap.josm.plugins.ods.mapping.AbstractMapping;
import org.openstreetmap.josm.plugins.ods.mapping.UpdateStatus;

public class BuildingMapping extends AbstractMapping<OsmBuilding, BagBuilding> {
    public BuildingMapping(OsmBuilding osmBuilding, BagBuilding odBuilding) {
        super(osmBuilding, odBuilding);
    }

    @Override
    public void analyze() {
        var odBuilding = getOpenDataEntity();
        if (odBuilding.getUpdateStatus() == UpdateStatus.Unknown) {
            if (getOsmEntities().isEmpty()) {
                odBuilding.setUpdateStatus(UpdateStatus.Addition);
                return;
            }
        }
        if (odBuilding.getStatus() == BuildingStatus.REMOVED) {
            odBuilding.setUpdateStatus(UpdateStatus.Deletion);
            return;
        }
        if (this.isTwoWay() && this.isSimple()) {
            odBuilding.setUpdateStatus(UpdateStatus.Mapped);
            odBuilding.setGeometryMatch(MappingUtils.compareGeometry(getOsmEntity(), getOpenDataEntity()));
            if (startDatesAreCompareble() && statusesAreCompareble()) {
                odBuilding.setAttributeMatch(MATCH);
            }
            else odBuilding.setAttributeMatch(NO_MATCH);
        }
    }

    private boolean startDatesAreCompareble() {
        return Objects.equals(getOsmEntity().getStartDate(), Objects.toString(getOpenDataEntity().getStartYear().toString()));
    }

    private boolean statusesAreCompareble() {
        BuildingStatus osmStatus = getOsmEntity().getStatus();
        BuildingStatus odStatus = getOpenDataEntity().getStatus();
        if (osmStatus.equals(odStatus)) {
            return true;
        }
        if (osmStatus.equals(IN_USE) && odStatus.equals(IN_USE_NOT_MEASURED)) {
            return true;
        }
        if (odStatus.equals(PLANNED) && osmStatus.equals(CONSTRUCTION)) {
            return true;
        }
        if (odStatus.equals(CONSTRUCTION) &&
                (osmStatus.equals(IN_USE) || osmStatus.equals(IN_USE_NOT_MEASURED))) {
            return true;
        }
        return false;
    }

//    @Override
//    public UpdateTaskType getUpdateTaskType() {
//        OsmBuilding osmBuilding = getOsmEntity();
//        if (osmBuilding == null) {
//            return UpdateTaskType.ADD;
//        }
//        if (!isSimple()) {
//            return UpdateTaskType.NONE;
//        }
//        BagBuilding bagBuilding = getOpenDataEntity();
//        if (bagBuilding.getStatus() == BuildingStatus.REMOVED) {
//            if (osmBuilding.getPrimitive().isDeleted()) {
//                return UpdateTaskType.NONE;
//            }
//            return UpdateTaskType.DELETE;
//        }
//        if (bagBuilding.getGeometryMatch() == NO_MATCH) {
//            return UpdateTaskType.MODIFY;
//        }
//        return UpdateTaskType.NONE;
//    }
}