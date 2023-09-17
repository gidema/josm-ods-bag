package org.openstreetmap.josm.plugins.ods.bag.osm.update;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.openstreetmap.josm.command.AddCommand;
import org.openstreetmap.josm.command.ChangeNodesCommand;
import org.openstreetmap.josm.command.ChangePropertyCommand;
import org.openstreetmap.josm.command.Command;
import org.openstreetmap.josm.command.DeleteCommand;
import org.openstreetmap.josm.command.MoveCommand;
import org.openstreetmap.josm.data.osm.DataSet;
import org.openstreetmap.josm.data.osm.Node;
import org.openstreetmap.josm.data.osm.OsmPrimitiveType;
import org.openstreetmap.josm.data.osm.Way;
import org.openstreetmap.josm.gui.layer.OsmDataLayer;
import org.openstreetmap.josm.plugins.ods.bag.BagImportModule;
import org.openstreetmap.josm.plugins.ods.bag.entity.BagBuilding;
import org.openstreetmap.josm.plugins.ods.bag.entity.osm.OsmBuilding;
import org.openstreetmap.josm.plugins.ods.context.OdsContext;
import org.openstreetmap.josm.plugins.ods.entities.OdEntity;
import org.openstreetmap.josm.plugins.ods.entities.OsmEntity;
import org.openstreetmap.josm.plugins.ods.entities.opendata.OdLayerManager;
import org.openstreetmap.josm.plugins.ods.entities.osm.OsmLayerManager;
import org.openstreetmap.josm.plugins.ods.mapping.Mapping;
import org.openstreetmap.josm.plugins.ods.mapping.update.EntityUpdater;
import org.openstreetmap.josm.plugins.ods.osm.NodeDWithinLatLon;

public class BuildingUpdater implements EntityUpdater {
    private final OsmDataLayer osmDataLayer;
    private final OdLayerManager odLayerManager;
    private final NodeDWithinLatLon dWithin;
    private final List<Mapping<? extends OsmEntity, ? extends OdEntity>> matches = new LinkedList<>();
    private final Map<Way, Way> odWays = new HashMap<>();
    private final Set<Way> osmWays = new HashSet<>();
    private final Map<Node, OdNodeDetails> odNodes = new HashMap<>();
    private final Set<Node> osmNodePool = new HashSet<>();

    public BuildingUpdater(OdsContext context) {
        super();
        this.osmDataLayer = context.getComponent(OsmLayerManager.class).getOsmDataLayer();
        this.odLayerManager = context.getComponent(OdLayerManager.class);
        Double tolerance = context.getParameter(BagImportModule.BuildingAlignmentTolerance);
        this.dWithin = new NodeDWithinLatLon(tolerance);
    }

    @Override
    public void update(List<Mapping<?, ?>> matches) {
        // TODO Auto-generated method stub
        
    }

    public List<Command> updateGeometries(OsmDataLayer layer) {
        layer.getDataSet().getAllSelected().forEach(p -> {
            OdEntity odEntity = odLayerManager.getEntity(p);
            if (odEntity instanceof BagBuilding) {
                BagBuilding building = (BagBuilding) odEntity;
                Mapping<? extends OsmEntity, ? extends OdEntity> match = building.getMapping();
                if (match.getOpenDataEntity().getPrimitive().getDisplayType().equals(OsmPrimitiveType.CLOSEDWAY)
                  && match.getOsmEntity().getPrimitive().getDisplayType().equals(OsmPrimitiveType.CLOSEDWAY)) {
                    matches.add(match);
                }
            }
        });
        collectData();
        analyseOdNodes();
        cleanupOsmNodePool();
        processUnmatchedOdNodes();
        return buildCommands();
    }

    /**
     * Cleanup the pool of osm node that are available for re-use.
     * Remove nodes with tags and nodes that are connected to other objects  
     */
    private void cleanupOsmNodePool() {
        osmNodePool.removeIf(node -> (
                node.hasKeys() || !osmWays.containsAll(node.getReferrers())
        ));
    }

    /**
     * Collect all ways and nodes on the Bag and Osm layers that participate in the geometry update
     * 
     * @param matches
     */
    private void collectData() {
        matches.forEach(match -> {
            BagBuilding bagBuilding = (BagBuilding) match.getOpenDataEntity();
            Way odWay = null;
            Way osmWay = null;
            if (bagBuilding.getPrimitive().getDisplayType().equals(OsmPrimitiveType.CLOSEDWAY)) {
                odWay = (Way) bagBuilding.getPrimitive();
                odWay.getNodes().forEach(node -> {
                    odNodes.computeIfAbsent(node, n -> new OdNodeDetails(n));
                });
            }
            OsmBuilding osmBuilding = (OsmBuilding) match.getOsmEntity();
            if (osmBuilding.getPrimitive().getDisplayType().equals(OsmPrimitiveType.CLOSEDWAY)) {
                osmWay = (Way) osmBuilding.getPrimitive();
                osmNodePool.addAll(osmWay.getNodes());
            }
            if (odWay != null && osmWay != null) {
                odWays.put(odWay, osmWay);
                osmWays.add(osmWay);
            }
        });
    }
    
    private void analyseOdNodes() {
        DataSet dataSet = osmDataLayer.getDataSet();
        // Find nodes on the osm datalayer that are a near match to the od Nodes     
        odNodes.forEach((node, details) -> {
            Node osmNode = dWithin.findNode(dataSet, node);
            if (osmNode != null) {
                details.osmNode = osmNode;
                osmNodePool.remove(osmNode);            }
        });
    }

    private void processUnmatchedOdNodes() {
        Iterator<Node> it = osmNodePool.iterator();
        // Process each nodeDetail for which the osmNode is missing
        odNodes.values().stream().filter(d -> d.osmNode == null).forEach(details -> {
            if (it.hasNext()) {
                details.osmNode = it.next();
                it.remove();
            }
            else {
                details.osmNode = new Node(details.odNode.getCoor());
            }
        });       
    }

    private List<Command> buildCommands() {
        DataSet dataSet = osmDataLayer.getDataSet();
        List<Command> newNodeCommands = new LinkedList<>();
        List<Command> moveNodeCommands = new LinkedList<>();
        List<Command> updateCommands =  new LinkedList<>();
        List<Command> deleteNodeCommands = new LinkedList<>();
        // Create new nodes
        odNodes.forEach((odNode, details) -> {
            Node osmNode = details.osmNode;
            if (osmNode.isNew()) {
                newNodeCommands.add(new AddCommand(dataSet, osmNode));
            }
            else if (details.isMovingNode()) {
                moveNodeCommands.add(new MoveCommand(osmNode, odNode.getCoor()));
            }
        });
        odWays.forEach((odWay, osmWay) -> {
            List<Node> nodes = new ArrayList<>(odWay.getNodesCount());
            odWay.getNodes().forEach(node -> {
                nodes.add(odNodes.get(node).osmNode);
            });
            updateCommands.add(new ChangeNodesCommand(osmWay, nodes));
            Map<String, String> tags = new HashMap<>();
            tags.put("source:date", odWay.get("source:date"));
            if (osmWay.get("building").equals("yes")) {
                tags.put("building", odWay.get("building"));
            }
            if (osmWay.get("building").equals("construction") &&
                    !odWay.get("building").equals("construction")) {
                String type = odWay.get("building");
                if (type.equals("yes")) {
                    type = osmWay.get("construction");
                }
                tags.put("building", type);
                tags.put("construction", null);
            }
            if (osmWay.get("ref:bag").length() != 16) {
                tags.put("ref:bag", odWay.get("ref:bag"));                
            }
            if (!Objects.equals(odWay.get("start_date"), osmWay.get("start_date"))) {
                tags.put("start_date", odWay.get("start_date"));                
            }
            updateCommands.add(new ChangePropertyCommand(Collections.singleton(osmWay), tags));
        });
        osmNodePool.forEach(osmNode -> {
            deleteNodeCommands.add(new DeleteCommand(osmNode));
        });
        // Create a single sequence command for all commands
        List<Command> commands = new LinkedList<>();
        commands.addAll(newNodeCommands);
        commands.addAll(moveNodeCommands);
        commands.addAll(updateCommands);
        commands.addAll(deleteNodeCommands);
        return commands;
    }

    private static class OdNodeDetails {
        Node odNode; // The original node on the Open Data layer
        Node osmNode = null; // The osm Node that will be used in the geometry update

        public OdNodeDetails(Node node) {
            this.odNode = node;
        }
        
        public boolean isMovingNode() {
            return !osmNode.getCoor().equals(odNode.getCoor());
        }
    }    
}
