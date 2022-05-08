package org.openstreetmap.josm.plugins.ods.bag.enrichment.osm;

import java.util.HashSet;
import java.util.Set;

import org.openstreetmap.josm.data.osm.Node;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.data.osm.OsmPrimitiveType;
import org.openstreetmap.josm.data.osm.Way;
import org.openstreetmap.josm.plugins.ods.bag.entity.BagBuilding;
import org.openstreetmap.josm.plugins.ods.bag.entity.osm.OsmBuilding;
import org.openstreetmap.josm.plugins.ods.matching.Match;

/**
 * This class is part of the building update process.
 * It's concern is to analyse the buildings on the OSM layer that are about
 * to be updated with a new geometry.
 *
 * @author Gertjan Idema <mail@gertjanidema.nl>
 *
 */
public class OdBuildingAnalyzer {
    private final Set<Match<OsmBuilding, BagBuilding>> buildingMatches;
    private final Set<OsmPrimitive> includedPrimitives = new HashSet<>();
    private final Set<Node> nodes = new HashSet<>();

    //    private final Set<Node> connectedBuildingNodes = new HashSet<>();
    //    private final Set<Node> connectedOtherNodes = new HashSet<>();

    public OdBuildingAnalyzer(Set<Match<OsmBuilding, BagBuilding>> updateableMatches) {
        super();
        this.buildingMatches = updateableMatches;
    }

    public void analyze() {
        // Collect the building primitives in a set, so we can look them up
        for (Match<OsmBuilding, BagBuilding> match : buildingMatches) {
            assert match.isSimple();
            includedPrimitives.add(match.getOsmEntity().getPrimitive());
        }
        for (OsmPrimitive osm : includedPrimitives) {
            // Focus on Ways. We'll tackle relations later
            if (osm.getDisplayType().equals(OsmPrimitiveType.CLOSEDWAY)) {
                nodes.addAll(((Way)osm).getNodes());
            }
        }
    }

    public Set<Node> getNodes() {
        return nodes;
    }
}
