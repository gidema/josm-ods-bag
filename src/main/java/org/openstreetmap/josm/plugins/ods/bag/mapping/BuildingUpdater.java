package org.openstreetmap.josm.plugins.ods.bag.mapping;

import java.util.LinkedList;
import java.util.List;

import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.plugins.ods.OdsModule;
import org.openstreetmap.josm.plugins.ods.bag.entity.BagBuilding;
import org.openstreetmap.josm.plugins.ods.bag.entity.osm.OsmBuilding;
import org.openstreetmap.josm.plugins.ods.mapping.Mapping;
import org.openstreetmap.josm.plugins.ods.mapping.MatchStatus;
import org.openstreetmap.josm.plugins.ods.mapping.update.EntityUpdater;
import org.openstreetmap.josm.plugins.ods.bag.entity.BuildingStatus;

public class BuildingUpdater implements EntityUpdater {

    public BuildingUpdater(@SuppressWarnings("unused") OdsModule module) {
        super();
    }

    @Override
    public void update(List<Mapping<?, ?>> matches) {
        List<Mapping<OsmBuilding, BagBuilding>> geometryUpdateNeeded = new LinkedList<>();
        for (Mapping<?, ?> match : matches) {
            if (match instanceof BuildingMapping) {
                BuildingMapping buildingMatch = (BuildingMapping) match;
                if (match.getOpenDataEntity().getGeometryMatch() == MatchStatus.NO_MATCH) {
                    geometryUpdateNeeded.add(buildingMatch);
                }
                OsmBuilding osmBuilding = buildingMatch.getOsmEntity();
                BagBuilding odBuilding = buildingMatch.getOpenDataEntity();
                if (match.getOpenDataEntity().getAttributeMatch().equals(MatchStatus.NO_MATCH)) {
                    updateAttributes(osmBuilding, odBuilding);
                }
                if (!match.getOpenDataEntity().getStatusMatch().equals(MatchStatus.MATCH)) {
                    updateStatus(osmBuilding, odBuilding);
                }
            }
        }
    }

    private static void updateAttributes(OsmBuilding osmBuilding, BagBuilding odBuilding) {
        OsmPrimitive osmPrimitive = osmBuilding.getPrimitive();
        osmBuilding.setSourceDate(odBuilding.getSourceDate());
        osmPrimitive.put("source:date", odBuilding.getSourceDate());
        if (odBuilding.getStartYear() != null) {
            osmBuilding.setStartDate(odBuilding.getStartYear().toString());
            osmPrimitive.put("start_date", odBuilding.getStartYear().toString());
        }
        osmPrimitive.setModified(true);
    }

    private static void updateStatus(OsmBuilding osmBuilding, BagBuilding odBuilding) {
        OsmPrimitive odPrimitive = odBuilding.getPrimitive();
        OsmPrimitive osmPrimitive = osmBuilding.getPrimitive();
        if (osmBuilding.getStatus().equals(BuildingStatus.CONSTRUCTION) &&
                (odBuilding.getStatus().equals(BuildingStatus.IN_USE) ||
                        odBuilding.getStatus().equals(BuildingStatus.IN_USE_NOT_MEASURED))
                ) {
            osmBuilding.setSourceDate(odBuilding.getSourceDate());
            osmPrimitive.put("source:date", odBuilding.getSourceDate());
            osmPrimitive.put("building", odPrimitive.get("building"));
            osmPrimitive.put("construction", null);
            osmBuilding.setStatus(odBuilding.getStatus());
            osmPrimitive.setModified(true);
        }
    }
}
