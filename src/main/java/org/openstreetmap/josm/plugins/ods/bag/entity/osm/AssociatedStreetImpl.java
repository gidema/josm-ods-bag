package org.openstreetmap.josm.plugins.ods.bag.entity.osm;

import java.util.Collection;
import java.util.LinkedList;

import org.openstreetmap.josm.data.osm.Relation;

public class AssociatedStreetImpl implements AssociatedStreet {
    private Relation relation;
    private String name;
    private final Collection<OsmBuilding> buildings = new LinkedList<>();
    private final Collection<OsmAddressNode> nodes = new LinkedList<>();
    private final Collection<OsmStreet> streets = new LinkedList<>();


    public void setRelation(Relation relation) {
        this.relation = relation;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    public void addBuilding(OsmBuilding building) {
        buildings.add(building);
    }

    public void addNode(OsmAddressNode node) {
        nodes.add(node);
    }

    public void addStreet(OsmStreet street) {
        streets.add(street);
    }

    @Override
    public Relation getOsmPrimitive() {
        return relation;
    }


    @Override
    public Collection<OsmBuilding> getBuildings() {
        return buildings;
    }

    @Override
    public Collection<OsmAddressNode> getAddressNodes() {
        return nodes;
    }

    @Override
    public Collection<OsmStreet> getStreets() {
        return streets;
    }
}
