package org.openstreetmap.josm.plugins.ods.bag.enrichment;

import static org.openstreetmap.josm.plugins.ods.entities.Entity.Completeness.Complete;

import java.util.LinkedList;
import java.util.List;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Polygonal;
import org.locationtech.jts.geom.prep.PreparedPolygon;
import org.openstreetmap.josm.plugins.ods.bag.entity.storage.BagBuildingStore;
import org.openstreetmap.josm.plugins.ods.bag.entity.storage.BagDemolishedBuildingStore;
import org.openstreetmap.josm.plugins.ods.context.OdsContext;
import org.openstreetmap.josm.plugins.ods.context.OdsContextJob;

/**
 * Enricher to update the completeness parameter for an open data building;
 * Incomplete building should not be imported, as they can't be connected to neighboring Osm buildings.
 *
 * @author Gertjan Idema <mail@gertjanidema.nl>
 *
 */
public class BuildingCompletenessEnricher implements OdsContextJob {

    public BuildingCompletenessEnricher() {
        super();
    }

    
    @Override
    public void run(OdsContext context) {
        processNormalBuildings(context);
        processDemolishedBuildings(context);
    }
    
    private void processNormalBuildings(OdsContext context) {
        BagBuildingStore buildingStore = context.getComponent(BagBuildingStore.class);
        Geometry boundary = buildingStore.getBoundary();
        List<PreparedPolygon> boundaries = new LinkedList<>();
        for (int i=0; i<boundary.getNumGeometries(); i++) {
            Polygonal polygonal = (Polygonal)boundary.getGeometryN(i);
            boundaries.add(new PreparedPolygon(polygonal));
        }
        buildingStore.forEach(building -> {
            if (building.getCompleteness() != Complete) {
                for (PreparedPolygon prep : boundaries) {
                    if (prep.covers(building.getGeometry())) {
                        building.setCompleteness(Complete);
                        break;
                    }
                }
            }
        });
    }

    private void processDemolishedBuildings(OdsContext context) {
        BagDemolishedBuildingStore buildingStore = context.getComponent(BagDemolishedBuildingStore.class);
        Geometry boundary = buildingStore.getBoundary();
        List<PreparedPolygon> boundaries = new LinkedList<>();
        for (int i=0; i<boundary.getNumGeometries(); i++) {
            Polygonal polygonal = (Polygonal)boundary.getGeometryN(i);
            boundaries.add(new PreparedPolygon(polygonal));
        }
        buildingStore.forEach(building -> {
            if (building.getCompleteness() != Complete) {
                for (PreparedPolygon prep : boundaries) {
                    if (prep.covers(building.getGeometry())) {
                        building.setCompleteness(Complete);
                        break;
                    }
                }
            }
        });
    }

}
