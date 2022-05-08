package org.openstreetmap.josm.plugins.ods.bag.match;

import java.util.LinkedList;
import java.util.List;

import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.plugins.ods.OdsModule;
import org.openstreetmap.josm.plugins.ods.bag.entity.BagBuilding;
import org.openstreetmap.josm.plugins.ods.bag.entity.osm.OsmBuilding;
import org.openstreetmap.josm.plugins.ods.entities.EntityStatus;
import org.openstreetmap.josm.plugins.ods.matching.Match;
import org.openstreetmap.josm.plugins.ods.matching.MatchStatus;
import org.openstreetmap.josm.plugins.ods.matching.update.EntityUpdater;

public class BuildingUpdater implements EntityUpdater {
    //    private final BuildingGeometryUpdater geometryUpdater;
    //    private final BuildingGeometryUpdaterNg geometryUpdater;

    public BuildingUpdater(@SuppressWarnings("unused") OdsModule module) {
        super();
        //        this.geometryUpdater = new BuildingGeometryUpdaterNg(module);
    }

    @Override
    public void update(List<Match<?, ?>> matches) {
        List<Match<OsmBuilding, BagBuilding>> geometryUpdateNeeded = new LinkedList<>();
        for (Match<?, ?> match : matches) {
            if (match instanceof BuildingMatch) {
                BuildingMatch buildingMatch = (BuildingMatch) match;
                if (match.getGeometryMatch() == MatchStatus.NO_MATCH) {
                    geometryUpdateNeeded.add(buildingMatch);
                }
                OsmBuilding osmBuilding = buildingMatch.getOsmEntity();
                BagBuilding odBuilding = buildingMatch.getOpenDataEntity();
                if (match.getAttributeMatch().equals(MatchStatus.NO_MATCH)) {
                    updateAttributes(osmBuilding, odBuilding);
                }
                if (!match.getStatusMatch().equals(MatchStatus.MATCH)) {
                    updateStatus(osmBuilding, odBuilding);
                }
            }
        }
        //        geometryUpdater.updateGeometries(geometryUpdateNeeded);
    }

    private static void updateAttributes(OsmBuilding osmBuilding, BagBuilding odBuilding) {
        OsmPrimitive osmPrimitive = osmBuilding.getPrimitive();
        osmBuilding.setSourceDate(odBuilding.getSourceDate());
        osmPrimitive.put("source:date", odBuilding.getSourceDate());
        osmBuilding.setStartDate(odBuilding.getStartDate());
        osmPrimitive.put("start_date", odBuilding.getStartDate());
        osmPrimitive.setModified(true);
    }

    private static void updateStatus(OsmBuilding osmBuilding, BagBuilding odBuilding) {
        OsmPrimitive odPrimitive = odBuilding.getPrimitive();
        OsmPrimitive osmPrimitive = osmBuilding.getPrimitive();
        if (osmBuilding.getStatus().equals(EntityStatus.CONSTRUCTION) &&
                (odBuilding.getStatus().equals(EntityStatus.IN_USE) ||
                        odBuilding.getStatus().equals(EntityStatus.IN_USE_NOT_MEASURED))
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
