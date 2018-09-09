package org.openstreetmap.josm.plugins.ods.bag.relations;

import java.util.Objects;

public class BuildingUnitToAddressNodeRelation extends Relation<BuildingUnitToAddressNodeRelation.Tuple> {
    public static class Tuple implements Relation.Tuple {
        private final Long buildingUnitId;
        private final Long addressNodeId;

        public Tuple(Long buildingUnitId, Long addressNodeId) {
            super();
            this.buildingUnitId = buildingUnitId;
            this.addressNodeId = addressNodeId;
        }

        public Long getBuildingUnitId() {
            return buildingUnitId;
        }

        public Long getAddressNodeId() {
            return addressNodeId;
        }

        @Override
        public int hashCode() {
            return Objects.hash(buildingUnitId, addressNodeId);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null || !(obj instanceof Tuple)) {
                return false;
            }
            Tuple other = (Tuple) obj;
            return Objects.equals(other.buildingUnitId, this.buildingUnitId) &&
                    Objects.equals(other.addressNodeId, this.addressNodeId);
        }
    }
}
