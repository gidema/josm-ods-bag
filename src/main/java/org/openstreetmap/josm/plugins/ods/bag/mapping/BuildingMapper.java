package org.openstreetmap.josm.plugins.ods.bag.mapping;

import static java.util.function.Predicate.not;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openstreetmap.josm.plugins.ods.Mapper;
import org.openstreetmap.josm.plugins.ods.bag.entity.BagBuilding;
import org.openstreetmap.josm.plugins.ods.bag.entity.DemolishedBuilding;
import org.openstreetmap.josm.plugins.ods.bag.entity.osm.OsmBuilding;
import org.openstreetmap.josm.plugins.ods.bag.entity.osm.OsmBuildingStore;
import org.openstreetmap.josm.plugins.ods.bag.entity.storage.BagBuildingStore;
import org.openstreetmap.josm.plugins.ods.bag.entity.storage.BagDemolishedBuildingStore;
import org.openstreetmap.josm.plugins.ods.context.OdsContext;
import org.openstreetmap.josm.plugins.ods.entities.OdEntity;
import org.openstreetmap.josm.plugins.ods.entities.OsmEntity;
import org.openstreetmap.josm.plugins.ods.mapping.Mapping;

public class BuildingMapper implements Mapper {
    private final Map<Long, BuildingMapping> buildingMappings = new HashMap<>();
    private final OsmBuildingStore osmBuildingStore;
    private final BagBuildingStore odBuildingStore;
    private final BagDemolishedBuildingStore demolishedBuildingStore;

    public BuildingMapper(OdsContext context) {
        super();
        osmBuildingStore = context.getComponent(OsmBuildingStore.class);
        odBuildingStore = context.getComponent(BagBuildingStore.class);
        demolishedBuildingStore = context.getComponent(BagDemolishedBuildingStore.class);
    }

    @Override
    public void run() {
        odBuildingStore.stream().filter(not(building -> building.isMapped(true))).forEach(building -> {
            processOpenDataBuilding(building);
        });
        demolishedBuildingStore.stream().filter(not(building -> building.isMapped(true))).forEach(building -> {
            processDemolishedBuilding(building);
        });
        osmBuildingStore.stream().filter(not(OsmEntity::isMapped)).forEach(building -> {
            processOsmBuilding(building);
        });
        analyze();
    }

    private void processOpenDataBuilding(BagBuilding odBuilding) {
        Long id = odBuilding.getBuildingId();
        BuildingMapping mapping = buildingMappings.get(id);
        if (mapping != null) {
            mapping.addOpenDataEntity(odBuilding);
            return;
        }
        List<OsmBuilding> osmBuildings = osmBuildingStore.getBuildingIdIndex().getAll(id);
        if (osmBuildings.size() > 0) {
            mapping = new BuildingMapping(osmBuildings.get(0), odBuilding);
            for (int i=1; i<osmBuildings.size() ; i++) {
                OsmBuilding osmBuilding = osmBuildings.get(i);
                osmBuilding.setMapping(mapping);
                mapping.addOsmEntity(osmBuilding);
            }
            buildingMappings.put(id, mapping);
        } else {
            mapping = new BuildingMapping(null, odBuilding);
            buildingMappings.put(id, mapping);
        }
    }

    private void processDemolishedBuilding(DemolishedBuilding odBuilding) {
        Long id = odBuilding.getBuildingId();
        BuildingMapping mapping = buildingMappings.get(id);
        if (mapping != null) {
            mapping.addOpenDataEntity(odBuilding);
            return;
        }
        List<OsmBuilding> osmBuildings = osmBuildingStore.getBuildingIdIndex().getAll(id);
        if (osmBuildings.size() > 0) {
            mapping = new BuildingMapping(osmBuildings.get(0), odBuilding);
            for (int i=1; i<osmBuildings.size() ; i++) {
                OsmBuilding osmBuilding = osmBuildings.get(i);
                osmBuilding.setMapping(mapping);
                mapping.addOsmEntity(osmBuilding);
            }
            buildingMappings.put(id, mapping);
        } else {
            mapping = new BuildingMapping(null, odBuilding);
            buildingMappings.put(id, mapping);
        }
    }

    private void processOsmBuilding(OsmBuilding osmBuilding) {
        Long id = osmBuilding.getBuildingId();
        if (id == null) {
            return;
        }
        BagBuilding odBuilding = odBuildingStore.get(id);
        if (odBuilding != null) {
            BuildingMapping mapping = new BuildingMapping(osmBuilding, odBuilding);
            buildingMappings.put(id, mapping);
        }
    }

    public void analyze() {
        for (Mapping<OsmBuilding, BagBuilding> mapping : buildingMappings.values()) {
            mapping.analyze();
            mapping.refreshUpdateTags();
        }
    }

    @Override
    public void reset() {
        buildingMappings.clear();
    }
}
