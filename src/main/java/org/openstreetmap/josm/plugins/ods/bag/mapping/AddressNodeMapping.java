package org.openstreetmap.josm.plugins.ods.bag.mapping;

import org.openstreetmap.josm.plugins.ods.bag.entity.BagBuildingUnit;
import org.openstreetmap.josm.plugins.ods.bag.entity.OdAddressNode;
import org.openstreetmap.josm.plugins.ods.bag.entity.osm.OsmAddressNode;
import org.openstreetmap.josm.plugins.ods.mapping.AbstractMapping;
import org.openstreetmap.josm.plugins.ods.mapping.MatchStatus;
import org.openstreetmap.josm.plugins.ods.mapping.UpdateStatus;

public class AddressNodeMapping extends AbstractMapping<OsmAddressNode, OdAddressNode> {
    Object id;

    public AddressNodeMapping(OsmAddressNode an1, OdAddressNode an2) {
        super(an1, an2);
        // TODO This is the only place with a direct reference to the referceId. 
        this.id = new Object();
    }

    @Override
    public Object getId() {
        return id;
    }

    @Override
    public OsmAddressNode getOsmEntity() {
        return super.getOsmEntity();
    }

    @Override
    public OdAddressNode getOpenDataEntity() {
        return super.getOpenDataEntity();
    }

    private MatchStatus getGeometryMatch() {
        // If the addressNodes are in the same addressable object, we don't look at
        // their exact location
        if (getOsmEntity().getBuilding() != null && getOpenDataEntity().getAddressableObject() != null) {
            var osmBuildingId = getOsmEntity().getBuilding().getBuildingId();
            var addressableObject = getOpenDataEntity().getAddressableObject();
            if (addressableObject instanceof BagBuildingUnit buildingUnit) {
                if (buildingUnit.getBuilding().getBuildingId().equals(osmBuildingId)) {
                    return MatchStatus.COMPARABLE;
                };
            }
        }
        return MatchStatus.NO_MATCH;
    }

    @Override
    public void analyze() {
        var odEntity = getOpenDataEntity();
        if (odEntity != null && this.getOsmEntities().isEmpty()) {
            odEntity.setUpdateStatus(UpdateStatus.Addition);
            return;
        }
        if (odEntity != null && this.isTwoWay()) {
            odEntity.setUpdateStatus(UpdateStatus.Mapped);
            var adOsm = getOsmEntity().getAddress();
            var adNl = getOpenDataEntity().getAddress();
            var houseNumberMatch = MatchStatus.match(adOsm.getHouseNumber().getHouseNumber(), adNl.getHouseNumber().getHouseNumber());
            var postcodeMatch = MatchStatus.match(adOsm.getPostcode(), adNl.getPostcode());
            var streetMatch = MatchStatus.match(adOsm.getStreetName(), adNl.getStreetName());
            var cityMatch = MatchStatus.match(adOsm.getCityName(), adNl.getCityName());
            odEntity.setAttributeMatch(MatchStatus.combine(houseNumberMatch, postcodeMatch,
                streetMatch, cityMatch));
            odEntity.setStatusMatch(MatchStatus.match(getOsmEntity().getStatus(), getOpenDataEntity().getStatus()));
            odEntity.setGeometryMatch(getGeometryMatch());
        }
    }
}