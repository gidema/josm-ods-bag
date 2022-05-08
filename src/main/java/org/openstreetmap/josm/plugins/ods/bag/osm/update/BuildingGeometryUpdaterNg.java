package org.openstreetmap.josm.plugins.ods.bag.osm.update;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import org.openstreetmap.josm.command.AddCommand;
import org.openstreetmap.josm.command.ChangeNodesCommand;
import org.openstreetmap.josm.command.Command;
import org.openstreetmap.josm.command.DeleteCommand;
import org.openstreetmap.josm.data.osm.DataSet;
import org.openstreetmap.josm.data.osm.Node;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.data.osm.OsmPrimitiveType;
import org.openstreetmap.josm.data.osm.Way;
import org.openstreetmap.josm.gui.layer.OsmDataLayer;
import org.openstreetmap.josm.plugins.ods.bag.enrichment.osm.OdBuildingAnalyzer;
import org.openstreetmap.josm.plugins.ods.bag.enrichment.osm.OsmBuildingAligner;
import org.openstreetmap.josm.plugins.ods.bag.enrichment.osm.OsmBuildingAnalyzer;
import org.openstreetmap.josm.plugins.ods.bag.entity.BagBuilding;
import org.openstreetmap.josm.plugins.ods.bag.entity.osm.OsmBuilding;
import org.openstreetmap.josm.plugins.ods.bag.entity.osm.OsmBuildingStore;
import org.openstreetmap.josm.plugins.ods.context.OdsContext;
import org.openstreetmap.josm.plugins.ods.entities.osm.OsmLayerManager;
import org.openstreetmap.josm.plugins.ods.matching.Match;
import org.openstreetmap.josm.plugins.ods.osm.update.NodePool;
import org.openstreetmap.josm.plugins.ods.osm.update.PoolNode;

public class BuildingGeometryUpdaterNg {
    private final OsmBuildingAligner osmBuildingAligner;
    private final OsmDataLayer osmDataLayer;
    private final Map<Node, PoolNode> nodeMapping = new HashMap<>();

    public BuildingGeometryUpdaterNg(OdsContext context) {
        super();
        this.osmDataLayer = context.getComponent(OsmLayerManager.class).getOsmDataLayer();
        this.osmBuildingAligner = new OsmBuildingAligner(context.getComponent(OsmBuildingStore.class));
    }

    public void updateGeometries(List<Match<OsmBuilding, BagBuilding>> matches) {
        Set<Match<OsmBuilding, BagBuilding>> updateableMatches = new HashSet<>();
        updateableMatches.addAll(matches);
        OsmBuildingAnalyzer osmAnalyzer = new OsmBuildingAnalyzer(matches);
        osmAnalyzer.analyze();
        updateableMatches.removeAll(osmAnalyzer.getExcludedMatches());
        OdBuildingAnalyzer odAnalyzer = new OdBuildingAnalyzer(updateableMatches);
        odAnalyzer.analyze();
        NodePool nodePool = osmAnalyzer.getNodePool();
        // First find
        for (Node node : odAnalyzer.getNodes()) {
            PoolNode matchingNode = nodePool.getMatchingNode(node);
            if (matchingNode != null) {
                nodeMapping.put(node, matchingNode);
            }
        }
        // Update the geometries for the selected buildings
        for (Match<OsmBuilding, BagBuilding> match : matches) {
            updateGeometry(match.getOsmEntity(), match.getOpenDataEntity());
        }
        // Realign the updated buildings to the neighbour buildings
        for (Match<OsmBuilding, BagBuilding> match : matches) {
            osmBuildingAligner.align(match.getOsmEntity());
        }
    }

    private void updateGeometry(OsmBuilding osmBuilding, BagBuilding odBuilding) {
        OsmPrimitive osmPrimitive = osmBuilding.getPrimitive();
        OsmPrimitive odPrimitive = odBuilding.getPrimitive();
        // Only update osm ways to start with
        if (osmPrimitive.getDisplayType() != OsmPrimitiveType.CLOSEDWAY ||
                odPrimitive.getDisplayType() != OsmPrimitiveType.CLOSEDWAY) {
            return;
        }
        Way osmWay = (Way) osmPrimitive;
        Way odWay = (Way) odPrimitive;
        DataSet dataSet = osmDataLayer.getDataSet();
        List<Node> osmNodes = osmWay.getNodes();
        List<Node> odNodes = odWay.getNodes();
        ListIterator<Node> it = odNodes.listIterator();
        List<Node> nodesToRemove = new LinkedList<>();
        while (it.hasNext()) {
            Node odNode = it.next();
            Node newNode = (Node) dataSet.getPrimitiveById(odNode);
            if (newNode == null) {
                // TODO Try to re-use old nodes
                newNode = new Node(odNode);
                Command addCommand = new AddCommand(dataSet, newNode);
                addCommand.executeCommand();
            }
            it.set(newNode);
        }
        Command cmd = new ChangeNodesCommand(osmWay, odNodes);
        cmd.executeCommand();
        for (Node node: osmNodes) {
            if (node.getReferrers().size() == 0) {
                nodesToRemove.add(node);
            }
        }
        if (nodesToRemove.size() > 0) {
            cmd = new DeleteCommand(nodesToRemove);
            cmd.executeCommand();
        }
    }
}
