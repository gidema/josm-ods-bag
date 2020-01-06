package org.openstreetmap.josm.plugins.ods.bag.modifiers;

import org.openstreetmap.josm.plugins.ods.domains.buildings.OdBuilding;
import org.openstreetmap.josm.plugins.ods.entities.EntityModifier;

/**
 * The original BAG import partially normalised the building geometries,
 * by making the (outer) rings clockwise.
 * This class normalises the geometries of imported buildings.
 * 
 * @author gertjan
 *
 */
public class NormalizeBuildingModifier implements EntityModifier<OdBuilding> {

    @Override
    public void modify(OdBuilding building) {
        building.setGeometry(building.getGeometry().norm());
    }

    @Override
    public Class<OdBuilding> getTargetType() {
        return OdBuilding.class;
    }

    @Override
    public boolean isApplicable(OdBuilding target) {
        return true;
    }
}
