package org.openstreetmap.josm.plugins.ods.bag.update;

import static org.openstreetmap.josm.plugins.ods.bag.BagImportModule.BuildingAlignmentTolerance;
import static org.openstreetmap.josm.plugins.ods.mapping.UpdateStatus.AdditionUpdated;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.openstreetmap.josm.command.AddCommand;
import org.openstreetmap.josm.command.Command;
import org.openstreetmap.josm.command.MoveCommand;
import org.openstreetmap.josm.data.osm.DataSet;
import org.openstreetmap.josm.data.osm.Node;
import org.openstreetmap.josm.data.osm.OsmPrimitiveType;
import org.openstreetmap.josm.data.osm.Way;
import org.openstreetmap.josm.gui.layer.OsmDataLayer;
import org.openstreetmap.josm.plugins.ods.context.OdsContext;
import org.openstreetmap.josm.plugins.ods.entities.OdEntity;
import org.openstreetmap.josm.plugins.ods.entities.osm.OsmLayerManager;
import org.openstreetmap.josm.plugins.ods.mapping.Mapping;
import org.openstreetmap.josm.plugins.ods.mapping.update.AdditionsCommand;
import org.openstreetmap.josm.plugins.ods.osm.NodeDWithinLatLon;
import org.openstreetmap.josm.plugins.ods.update.AdditionHandler;

public class BagAdditionHandler implements AdditionHandler {
    private final OdsContext context; 
    private final OsmDataLayer osmDataLayer;
    private final NodeDWithinLatLon dWithin;
    private final Set<Way> odWays = new HashSet<>();
    private final Set<Way> newOsmWays = new HashSet<>();
    private final Set<Node> newOsmNodes = new HashSet<>();
    private final Map<Node, OdNodeDetails> odNodes = new HashMap<>();
    private final Set<Node> osmNodePool = new HashSet<>();
    private final Set<OdEntity> affectedEntities = new HashSet<>();
    private Command command;

    public BagAdditionHandler(OdsContext context) {
        super();
        this.context = context;
        this.osmDataLayer = context.getComponent(OsmLayerManager.class).getOsmDataLayer();
        Double tolerance = context.getParameter(BuildingAlignmentTolerance);
        this.dWithin = new NodeDWithinLatLon(tolerance);
    }

    @Override
    public void handle(List<Mapping<?, ?>> mappings) {
        collectData(mappings);
        analyseOdNodes();
        processUnmatchedOdNodes();
        buildCommand();
        affectedEntities.forEach(entity -> {
            entity.getMapping().getOpenDataEntities().forEach(
                    odEntity -> odEntity.setUpdateStatus(AdditionUpdated));
        });
    }

    /**
     * Collect all ways and nodes on the Bag and Osm layers that participate in the geometry update
     * 
     * @param matches
     */
    private void collectData(List<Mapping<?, ?>> mappings) {
        mappings.forEach(mapping -> {
            var odEntity = mapping.getOpenDataEntity();
            if (odEntity.getPrimitive().getDisplayType().equals(OsmPrimitiveType.CLOSEDWAY)) {
                var odWay = (Way) odEntity.getPrimitive();
                odWay.getNodes().forEach(node -> {
                    odNodes.computeIfAbsent(node, n -> new OdNodeDetails(n));
                });
                affectedEntities.add(odEntity);
                odWays.add(odWay);
            }
            if (odEntity.getPrimitive().getDisplayType().equals(OsmPrimitiveType.NODE)) {
                affectedEntities.add(odEntity);
                var node = (Node) odEntity.getPrimitive();
                newOsmNodes.add(new Node(node, true));
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
            }
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


    @Override
    public Optional<Command> getCommand() {
        return Optional.ofNullable(command);
    }

    private void buildCommand() {
        DataSet dataSet = osmDataLayer.getDataSet();
        List<Command> newNodeCommands = new LinkedList<>();
        List<Command> moveNodeCommands = new LinkedList<>();
        List<Command> newWayCommands =  new LinkedList<>();
        // Create new POI nodes
        newOsmNodes.forEach(node -> {
            newNodeCommands.add(new AddCommand(dataSet, node));
        });
        // Create new way nodes
        odNodes.forEach((odNode, details) -> {
            Node osmNode = details.osmNode;
            if (osmNode.isNew() && osmNode.getDataSet() == null) {
                newNodeCommands.add(new AddCommand(dataSet, osmNode));
            }
            else if (details.isMovingNode()) {
                moveNodeCommands.add(new MoveCommand(osmNode, odNode.getCoor()));
            }
        }); 
        odWays.forEach(way -> {
            List<Node> nodes = new ArrayList<>(way.getNodesCount());
            way.getNodes().forEach(node -> {
                nodes.add(odNodes.get(node).osmNode);
            });
            Map<String, String> tags = new HashMap<>(way.getKeys());
            Way newWay = new Way();
            newWay.setNodes(nodes);
            newWay.setKeys(tags);
            newWayCommands.add(new AddCommand(dataSet, newWay));
            newOsmWays.add(newWay);
        });
        // Create a single sequence command for all commands
        List<Command> commands = new LinkedList<>();
        commands.addAll(newNodeCommands);
        commands.addAll(moveNodeCommands);
        commands.addAll(newWayCommands);
        if (commands.isEmpty()) {
            this.command = null;
        }
        else {
            this.command = new AdditionsCommand(context, commands);
        }
    }

    
    /**
     * Get the new Node primitives. This doesn't include the nodes that are used for new ways. 
     */
    @Override
    public Collection<Node> getAddedNodes() {
        return newOsmNodes;
    }

    @Override
    public Collection<Way> getAddedWays() {
        return newOsmWays;
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
