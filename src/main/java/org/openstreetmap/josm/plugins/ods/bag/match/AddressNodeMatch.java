package org.openstreetmap.josm.plugins.ods.bag.match;

import org.openstreetmap.josm.plugins.ods.bag.entity.NLAddress;
import org.openstreetmap.josm.plugins.ods.bag.entity.OdAddressNode;
import org.openstreetmap.josm.plugins.ods.bag.entity.osm.OsmAddress;
import org.openstreetmap.josm.plugins.ods.bag.entity.osm.OsmAddressNode;
import org.openstreetmap.josm.plugins.ods.matching.MatchImpl;
import org.openstreetmap.josm.plugins.ods.matching.MatchStatus;

public class AddressNodeMatch extends MatchImpl<OsmAddressNode, OdAddressNode> {
    Object id;
    private MatchStatus houseNumberMatch;
    private MatchStatus fullHouseNumberMatch;
    private MatchStatus postcodeMatch;
    private MatchStatus streetMatch;
    private MatchStatus cityMatch;

    public AddressNodeMatch(OsmAddressNode an1, OdAddressNode an2) {
        super(an1, an2);
        // TODO This is the only place with a direct reference to the referceId. 
        this.id = new Object();
        an1.setMatch(this);
        an2.setMatch(this);
    }

    @Override
    public Object getId() {
        return id;
    }

    @Override
    public MatchStatus getGeometryMatch() {
        // If the addressNodes are in the same building, we don't look at
        // their exact location
        if (getOsmEntity().getBuilding() != null && getOpenDataEntity().getBuilding() != null) {
            return MatchStatus.match(getOsmEntity().getBuilding().getBuildingId(),
                getOpenDataEntity().getBuilding().getBuildingId());
        }
        return MatchStatus.NO_MATCH;
    }

    @Override
    public MatchStatus getStatusMatch() {
        return MatchStatus.match(getOsmEntity().getStatus(), getOpenDataEntity().getStatus());
    }

    @Override
    public MatchStatus getAttributeMatch() {
        return MatchStatus.combine(houseNumberMatch, fullHouseNumberMatch, postcodeMatch,
                streetMatch, cityMatch);
    }

    @Override
    public void analyze() {
        OsmAddress ad1 = getOsmEntity().getAddress();
        NLAddress ad2 = getOpenDataEntity().getAddress();
        houseNumberMatch = MatchStatus.match(ad1.getHouseNumber().getHouseNumber(), ad2.getHouseNumber().getHouseNumber());
        fullHouseNumberMatch = MatchStatus.NO_MATCH;
        postcodeMatch = MatchStatus.match(ad1.getPostcode(), ad2.getPostcode());
        streetMatch = MatchStatus.match(ad1.getStreetName(), ad2.getStreetName());
        cityMatch = MatchStatus.match(ad1.getCityName(), ad2.getCityName());
    }
}