package org.openstreetmap.josm.plugins.ods.bag.update;

import java.util.LinkedList;
import java.util.List;

import org.openstreetmap.josm.command.Command;
import org.openstreetmap.josm.plugins.ods.bag.mapping.BuildingMapping;
import org.openstreetmap.josm.plugins.ods.context.OdsContext;
import org.openstreetmap.josm.plugins.ods.mapping.Mapping;
import org.openstreetmap.josm.plugins.ods.mapping.UpdateStatus;
import org.openstreetmap.josm.plugins.ods.update.ModificationHandler;

public class BagModificationHandler implements ModificationHandler {
    private final OdsContext context;
    private final List<Command> commands = new LinkedList<>();
    private BuildingModificationHandler buildingModificationHandler;

    public BagModificationHandler(OdsContext context) {
        super();
        this.context = context;
    }

    @Override
    public void handle(List<Mapping<?, ?>> matches) {
        List<BuildingMapping> buildingMappings = new LinkedList<>();
        matches.forEach(m -> {
            if (m instanceof BuildingMapping mapping) {
                buildingMappings.add(mapping);
            }
        });
        if (!buildingMappings.isEmpty()) {
            buildingModificationHandler = new BuildingModificationHandler(context);
            buildingModificationHandler.handle(buildingMappings);
            commands.addAll(buildingModificationHandler.buildCommands());
            buildingMappings.forEach(mapping -> {
                mapping.getOpenDataEntities().forEach(
                        odEntity -> odEntity.setUpdateStatus(UpdateStatus.Unknown));});

        }
    }

    @Override
    public List<Command> buildCommands() {
        return commands;
    }
}