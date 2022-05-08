package org.openstreetmap.josm.plugins.ods.bag.match;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.plugins.ods.Matcher;
import org.openstreetmap.josm.plugins.ods.ODS;
import org.openstreetmap.josm.plugins.ods.bag.entity.NLAddress;
import org.openstreetmap.josm.plugins.ods.bag.entity.NlHouseNumber;
import org.openstreetmap.josm.plugins.ods.bag.entity.OdAddressNode;
import org.openstreetmap.josm.plugins.ods.bag.entity.BagBuilding;
import org.openstreetmap.josm.plugins.ods.bag.entity.BagBuildingUnit;
import org.openstreetmap.josm.plugins.ods.bag.entity.osm.OsmAddress;
import org.openstreetmap.josm.plugins.ods.bag.entity.osm.OsmAddressNode;
import org.openstreetmap.josm.plugins.ods.bag.entity.osm.OsmAddressNodeStore;
import org.openstreetmap.josm.plugins.ods.bag.entity.osm.OsmAddressNodeStore.PostcodeHousenumber;
import org.openstreetmap.josm.plugins.ods.bag.entity.osm.OsmBuilding;
import org.openstreetmap.josm.plugins.ods.bag.entity.storage.BagAddressNodeStore;
import org.openstreetmap.josm.plugins.ods.bag.entity.storage.BagBuildingStore;
import org.openstreetmap.josm.plugins.ods.context.OdsContext;
import org.openstreetmap.josm.plugins.ods.matching.Match;

/**
 * Matcher that tries to find matches between Open Data (OD) entities and entities that
 * are present on the OSM layer.
 *
 * @author Gertjan Idema
 *
 */
public class AddressNodeMatcher implements Matcher {
    private final OdsContext context;

    private final Map<Object, Match<OsmAddressNode, OdAddressNode>> addressNodeMatches = new HashMap<>();
    private final OsmAddressNodeStore osmAddressNodeStore;
    private final BagAddressNodeStore odAddressNodeStore;
    private final List<OsmAddressNode> unidentifiedOsmAddressNodes = new LinkedList<>();
    private final List<OdAddressNode> unmatchedOpenDataAddressNodes = new LinkedList<>();
    private final List<OsmAddressNode> unmatchedOsmAddressNodes = new LinkedList<>();


    public AddressNodeMatcher(OdsContext context) {
        super();
        this.context = context;
        odAddressNodeStore = context.getComponent(BagAddressNodeStore.class);
        osmAddressNodeStore = context.getComponent(OsmAddressNodeStore.class);
    }

    @Override
    public void run() {
        matchBuildingAddressNodes();
        matchAddressNodesByPcHnr();
    }

    /**
     * Try to match address nodes for matching buildings
     */
    private void matchBuildingAddressNodes() {
        BagBuildingStore  buildingStore = context.getComponent(BagBuildingStore.class);
        for (BagBuilding building : buildingStore) {
            if (building.getMatch() != null && building.getMatch().isSimple()) {
                matchAddresses(building.getMatch());
            }
        }
    }

    private void matchAddresses(BuildingMatch match) {
        OsmBuilding osmBuilding = match.getOsmEntity();
        BagBuilding odBuilding = match.getOpenDataEntity();
        Map<AddressKey, OdAddressNode> odNodes = new HashMap<>();
        for (BagBuildingUnit bu : odBuilding.getBuildingUnits().values()) {
            odNodes.put(new AddressKey(bu.getMainAddressNode()), bu.getMainAddressNode());
        }
        for (OsmAddressNode anOsm : osmBuilding.getAddressNodes()) {
            AddressKey key = new AddressKey(anOsm);
            OdAddressNode anOd = odNodes.get(key);
            if (anOd != null) {
                matchAddressNodes(anOsm, anOd);
            }
        }
    }

    private void matchAddressNodes(OsmAddressNode anOsm, OdAddressNode anOd) {
        OsmAddress adOsm = anOsm.getAddress();
        NLAddress adOd = anOd.getAddress();

        if (Objects.equals(adOsm.getHouseNumber(), adOd.getHouseNumber())
                && Objects.equals(adOsm.getPostcode(), adOd.getPostcode())) {
            AddressNodeMatch match = new AddressNodeMatch(anOsm, anOd);
            match.analyze();
            match.updateMatchTags();
            addressNodeMatches.put(match.getId(), match);
        }
    }

    private void matchAddressNodesByPcHnr() {
        unmatchedOpenDataAddressNodes.clear();
        for (OdAddressNode anOd : odAddressNodeStore) {
            if (anOd.getMatch() == null) {
                String postcode = anOd.getAddress().getPostcode();
                Integer houseNr = anOd.getAddress().getHouseNumber().getHouseNumber();
                if (postcode != null) {
                    PostcodeHousenumber pcHnr = new PostcodeHousenumber(postcode, houseNr);
                    List<OsmAddressNode> candidates = osmAddressNodeStore.getPcHnrIndex().getAll(pcHnr);
                    if (!candidates.isEmpty()) {
                        OsmAddressNode anOsm = findMatch(anOd, candidates);
                        if (anOsm != null) {
                            AddressNodeMatch match = new AddressNodeMatch(anOsm, anOd);
                            match.analyze();
                            match.updateMatchTags();
                            addressNodeMatches.put(match.getId(), match);
                        }
                    }
                }
                unmatchedOpenDataAddressNodes.add(anOd);
            }
        }
        analyze();
    }

    private OsmAddressNode findMatch(OdAddressNode anOd, List<OsmAddressNode> candidates) {
        for (OsmAddressNode anOsm : candidates) {
            if (anOd.getAddress().getHouseNumber().equals(anOsm.getAddress().getHouseNumber())) {
                return anOsm;
            }
        }
        return null;
    }

    public void analyze() {
        for (Match<OsmAddressNode, OdAddressNode> match : addressNodeMatches.values()) {
            if (match.isSimple()) {
                match.analyze();
                match.updateMatchTags();
            }
        }
        for (OdAddressNode addressNode: unmatchedOpenDataAddressNodes) {
            OsmPrimitive osm = addressNode.getPrimitive();
            if (osm != null) {
                osm.put(ODS.KEY.IDMATCH, "false");
                osm.put(ODS.KEY.STATUS, addressNode.getStatus().toString());
            }
        }
    }

    @Override
    public void reset() {
        addressNodeMatches.clear();
        unidentifiedOsmAddressNodes.clear();
        unmatchedOpenDataAddressNodes.clear();
        unmatchedOsmAddressNodes.clear();
    }

    private static class AddressKey {
        private final NlHouseNumber houseNumber;
        private final String postcode;

        public AddressKey(OsmAddressNode an) {
            this.houseNumber = an.getAddress().getHouseNumber();
            this.postcode = an.getAddress().getPostcode();
        }

        public AddressKey(OdAddressNode an) {
            this.houseNumber = an.getAddress().getHouseNumber();
            this.postcode = an.getAddress().getPostcode();
        }

        @Override
        public int hashCode() {
            return Objects.hash(postcode, houseNumber);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (!(obj instanceof AddressKey)) return false;
            AddressKey key = (AddressKey) obj;
            return Objects.equals(houseNumber, key.houseNumber)
                    && Objects.equals(postcode, key.postcode);
        }
    }
}
