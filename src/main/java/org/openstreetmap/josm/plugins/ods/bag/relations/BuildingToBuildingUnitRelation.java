package org.openstreetmap.josm.plugins.ods.bag.relations;

import java.util.HashSet;
import java.util.Objects;

@SuppressWarnings("serial")
public class BuildingToBuildingUnitRelation extends HashSet<BuildingToBuildingUnitRelation.Tuple> {
    public static class Tuple {
        private final Long buildingId;
        private final Long buildingUnitId;

        public Tuple(Long buildingId, Long buildingUnitId) {
            super();
            this.buildingId = buildingId;
            this.buildingUnitId = buildingUnitId;
        }

        public Long getBuildingId() {
            return buildingId;
        }

        public Long getBuildingUnitId() {
            return buildingUnitId;
        }

        @Override
        public int hashCode() {
            return Objects.hash(buildingId, buildingUnitId);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null || !(obj instanceof Tuple)) {
                return false;
            }
            Tuple other = (Tuple) obj;
            return Objects.equals(other.buildingUnitId, this.buildingUnitId) &&
                    Objects.equals(other.buildingId, this.buildingId);
        }
    }
}
