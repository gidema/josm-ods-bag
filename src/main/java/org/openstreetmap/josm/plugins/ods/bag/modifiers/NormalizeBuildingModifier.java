package org.openstreetmap.josm.plugins.ods.bag.modifiers;

import org.openstreetmap.josm.plugins.ods.bag.entity.BagBuilding;
import org.openstreetmap.josm.plugins.ods.entities.EntityModifier;

/**
 * The original BAG import partially normalised the building geometries,
 * by making the (outer) rings clockwise.
 * This class normalises the geometries of imported buildings.
 * 
 * @author gertjan
 *
 */
public class NormalizeBuildingModifier implements EntityModifier<BagBuilding> {

    @Override
    public void modify(BagBuilding building) {
        building.setGeometry(building.getGeometry().norm());
    }

    @Override
    public Class<BagBuilding> getTargetType() {
        return BagBuilding.class;
    }

    @Override
    public boolean isApplicable(BagBuilding target) {
        return true;
    }
}
