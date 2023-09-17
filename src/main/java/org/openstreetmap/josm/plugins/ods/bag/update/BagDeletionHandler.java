package org.openstreetmap.josm.plugins.ods.bag.update;

import static org.openstreetmap.josm.plugins.ods.mapping.UpdateStatus.DeletionUpdated;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.openstreetmap.josm.command.Command;
import org.openstreetmap.josm.command.DeleteCommand;
import org.openstreetmap.josm.data.osm.DataSet;
import org.openstreetmap.josm.data.osm.Node;
import org.openstreetmap.josm.data.osm.Way;
import org.openstreetmap.josm.gui.layer.OsmDataLayer;
import org.openstreetmap.josm.plugins.ods.context.OdsContext;
import org.openstreetmap.josm.plugins.ods.entities.OdEntity;
import org.openstreetmap.josm.plugins.ods.entities.OsmEntity;
import org.openstreetmap.josm.plugins.ods.entities.osm.OsmLayerManager;
import org.openstreetmap.josm.plugins.ods.mapping.Mapping;
import org.openstreetmap.josm.plugins.ods.update.DeletionHandler;

public class BagDeletionHandler implements DeletionHandler {
    private final OsmDataLayer osmDataLayer;
    private final Set<Node> nodesToDelete = new HashSet<>();
    private final Set<Way> waysToDelete = new HashSet<>();
    private final Set<OdEntity> updatedEntities = new HashSet<>();
    private List<Command> commands;
    
    public BagDeletionHandler(OdsContext context) {
        super();
        this.osmDataLayer = context.getComponent(OsmLayerManager.class).getOsmDataLayer();
    }

    @Override
    public void handle(List<Mapping<?, ?>> candidates) {
        candidates.forEach(candidate -> {
            if (candidate.isSimple()) {
                var entity = candidate.getOsmEntity();
                switch (entity.getPrimitive().getDisplayType()) {
                case CLOSEDWAY:
                case WAY:
                    prepareWay(entity, (Way) entity.getPrimitive());
                    updatedEntities.add(candidate.getOpenDataEntity());
                    break;
                case NODE:
                    prepareNode(entity, (Node) entity.getPrimitive());
                    updatedEntities.add(candidate.getOpenDataEntity());
                    break;
                default:
                    // TODO support deletion of multiploygons
                    break;
                }
            }
        });
        keepConnectedNodes();
        buildCommands();
        updatedEntities.forEach(entity -> {
            entity.getMapping().getOpenDataEntities().forEach(
                    odEntity -> odEntity.setUpdateStatus(DeletionUpdated));
        });
    }

    private void keepConnectedNodes() {
        var it = nodesToDelete.iterator();
        it.forEachRemaining(node -> {
            if (!waysToDelete.containsAll(node.getReferrers())) {
                it.remove();
            }
        });
    }

    private void prepareWay(OsmEntity entity, Way way) {
        if (entity.getOtherTags().isEmpty()) {
            waysToDelete.add(way);
            way.getNodes().forEach(node -> {
                if (!node.hasKeys()) {
                    nodesToDelete.add(node);
                }
            });
        }    
    }

    private void prepareNode(OsmEntity entity, Node node) {
        if (entity.getOtherTags().isEmpty()) {
            nodesToDelete.add(node);
        }
    }

    @Override
    public List<Command> getCommands() {
        return commands;
    }
    
    private void buildCommands() {
        DataSet dataSet = osmDataLayer.getDataSet();
        commands =  new LinkedList<>();
        // Delete nodes
        waysToDelete.forEach(way -> {
            commands.add(new DeleteCommand(dataSet, way));
        });
        // Delete ways
        nodesToDelete.forEach(node -> {
            commands.add(new DeleteCommand(dataSet, node));
        });
    }
}
