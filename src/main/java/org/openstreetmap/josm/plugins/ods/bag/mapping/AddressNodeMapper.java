package org.openstreetmap.josm.plugins.ods.bag.mapping;

import static java.util.function.Predicate.not;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.plugins.ods.Mapper;
import org.openstreetmap.josm.plugins.ods.ODS;
import org.openstreetmap.josm.plugins.ods.bag.entity.NlHouseNumber;
import org.openstreetmap.josm.plugins.ods.bag.entity.OdAddressNode;
import org.openstreetmap.josm.plugins.ods.bag.entity.osm.OsmAddressNode;
import org.openstreetmap.josm.plugins.ods.bag.entity.osm.OsmAddressNodeStore;
import org.openstreetmap.josm.plugins.ods.bag.entity.storage.BagAddressNodeStore;
import org.openstreetmap.josm.plugins.ods.context.OdsContext;
import org.openstreetmap.josm.plugins.ods.entities.OsmEntity;
import org.openstreetmap.josm.plugins.ods.mapping.Mapping;
import org.openstreetmap.josm.plugins.ods.util.OneOrMany;
import org.openstreetmap.josm.plugins.ods.util.OneOrManyMap;

/**
 * Mapper that tries to find matches between Open Data (OD) entities and entities that
 * are present on the OSM layer.
 *
 * @author Gertjan Idema
 *
 */
public class AddressNodeMapper implements Mapper {
    private final OdsContext context;

//    private final Map<PostcodeHousenumber, AddressNodeMapping> postcodeMappings = new HashMap<>();
    private final Set<AddressNodeMapping> addressNodeMappings = new HashSet<>();
    private final OsmAddressNodeStore osmAddressNodeStore;
    private final BagAddressNodeStore odAddressNodeStore;
    private final List<OsmAddressNode> unidentifiedOsmAddressNodes = new LinkedList<>();
    private final List<OdAddressNode> unmappedOpenDataAddressNodes = new LinkedList<>();

    public AddressNodeMapper(OdsContext context) {
        super();
        this.context = context;
        odAddressNodeStore = context.getComponent(BagAddressNodeStore.class);
        osmAddressNodeStore = context.getComponent(OsmAddressNodeStore.class);
    }

    @Override
    public void run() {
        var osmIndex = indexOsmNodes();
        for (OdAddressNode anOd : odAddressNodeStore) {
            var mapping = mapAddressNodeByPcHnr(anOd, osmIndex);
            if (mapping == null) {
                mapping = new AddressNodeMapping(null, anOd);
                anOd.setMapping(mapping);
            }
            addressNodeMappings.add(mapping);
        }
        analyze();
    }

    /**
     * Try to map address nodes for mapped buildings
     */
//    private void mapBuildingAddressNodes() {
//        BagBuildingStore  buildingStore = context.getComponent(BagBuildingStore.class);
//        for (BagBuilding building : buildingStore) {
//            if (building.getMapping() != null && building.getMapping().isSimple()) {
//                mapddresses((BuildingMapping) building.getMapping());
//            }
//        }
//    }
//
//    private void mapddresses(BuildingMapping mapping) {
//        OsmBuilding osmBuilding = mapping.getOsmEntity();
//        BagBuilding odBuilding = mapping.getOpenDataEntity();
//        Map<AddressKey, OdAddressNode> odNodes = new HashMap<>();
//        for (BagBuildingUnit bu : odBuilding.getBuildingUnits().values()) {
//            odNodes.put(new AddressKey(bu.getMainAddressNode()), bu.getMainAddressNode());
//        }
//        for (OsmAddressNode anOsm : osmBuilding.getAddressNodes()) {
//            AddressKey key = new AddressKey(anOsm);
//            OdAddressNode anOd = odNodes.get(key);
//            if (anOd != null) {
//                mapAddressNodes(anOsm, anOd);
//            }
//        }
//    }
//
//    private void mapAddressNodes(OsmAddressNode anOsm, OdAddressNode anOd) {
//        OsmAddress adOsm = anOsm.getAddress();
//        NLAddress adOd = anOd.getAddress();
//
//        if (Objects.equals(adOsm.getHouseNumber(), adOd.getHouseNumber())
//                && Objects.equals(adOsm.getPostcode(), adOd.getPostcode())) {
//            AddressNodeMapping mapping = new AddressNodeMapping(anOsm, anOd);
//            mapping.analyze();
//            mapping.refreshUpdateTags();
//            addressNodeMappings.add(mapping);
//        }
//    }

    private AddressNodeMapping mapAddressNodeByPcHnr(OdAddressNode anOd, Map<AddressKey, OneOrMany<OsmAddressNode>> osmIndex) {
        var candidates = osmIndex.get(new AddressKey(anOd));
        if (candidates != null && !candidates.hasMany()) {
            OsmAddressNode anOsm = candidates.get();
            AddressNodeMapping mapping = new AddressNodeMapping(anOsm, anOd);
            mapping.analyze();
            mapping.refreshUpdateTags();
            anOd.setMapping(mapping);
            anOsm.setMapping(mapping);
            return mapping;
        }
        return null;
    }

   private static OsmAddressNode findMapping(OdAddressNode anOd, List<OsmAddressNode> candidates) {
        for (OsmAddressNode anOsm : candidates) {
            if (anOd.getAddress().getHouseNumber().equals(anOsm.getAddress().getHouseNumber())) {
                return anOsm;
            }
        }
        return null;
    }

    public void analyze() {
        for (Mapping<OsmAddressNode, OdAddressNode> mapping : addressNodeMappings) {
            mapping.analyze();
            mapping.refreshUpdateTags();
        }
        for (OdAddressNode addressNode: unmappedOpenDataAddressNodes) {
            OsmPrimitive osm = addressNode.getPrimitive();
            if (osm != null) {
                osm.put(ODS.KEY.IDMATCH, "false");
                osm.put(ODS.KEY.STATUS, addressNode.getStatus().toString());
            }
        }
    }
    
    private Map<AddressKey, OneOrMany<OsmAddressNode>> indexOsmNodes() {
        Map<AddressKey, OneOrMany<OsmAddressNode>> unMappedOsmNodes = new HashMap<>();
//        osmAddressNodeStore.stream().filter(not(n -> n.isMapped())).forEach(osmAN -> {
        osmAddressNodeStore.stream().forEach(osmAN -> {
            AddressKey addressKey = new AddressKey(osmAN);
            unMappedOsmNodes.compute(addressKey, (key, existing) -> {
                // Most common case
                if (existing == null) {
                    return new OneOrManyMap<>(osmAN, OsmEntity::getPrimitiveId);
                }
                existing.add(osmAN);
                return existing;
            });
        });
        return unMappedOsmNodes;
    }

    @Override
    public void reset() {
        addressNodeMappings.clear();
        unidentifiedOsmAddressNodes.clear();
        unmappedOpenDataAddressNodes.clear();
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
