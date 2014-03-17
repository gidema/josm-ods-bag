package org.openstreetmap.josm.plugins.ods.bag.osm;

import java.util.LinkedList;
import java.util.List;

import org.opengis.feature.simple.SimpleFeature;
import org.openstreetmap.josm.data.osm.Node;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.data.osm.Relation;
import org.openstreetmap.josm.data.osm.Way;
import org.openstreetmap.josm.plugins.ods.bag.BagEntity;
import org.openstreetmap.josm.plugins.ods.bag.osm.BagAddressNodeBuilder;
import org.openstreetmap.josm.plugins.ods.bag.osm.BagBuildingBuilder;
import org.openstreetmap.josm.plugins.ods.bag.internal.InternalBagAddressNode;
import org.openstreetmap.josm.plugins.ods.bag.internal.InternalBagBuilding;
import org.openstreetmap.josm.plugins.ods.bag.internal.InternalBagLigplaats;
import org.openstreetmap.josm.plugins.ods.bag.internal.InternalBagStandplaats;
import org.openstreetmap.josm.plugins.ods.entities.BuildException;
import org.openstreetmap.josm.plugins.ods.entities.DefaultEntitySet;
import org.openstreetmap.josm.plugins.ods.entities.Entity;
import org.openstreetmap.josm.plugins.ods.entities.EntityFactory;
import org.openstreetmap.josm.plugins.ods.entities.builtenvironment.Building;
import org.openstreetmap.josm.plugins.ods.issue.Issue;
import org.openstreetmap.josm.plugins.ods.metadata.MetaData;

public class BagEntityFactory implements EntityFactory<OsmPrimitive> {
    private BagBuildingBuilder buildingBuilder = new BagBuildingBuilder();
    private BagAddressNodeBuilder addressNodeBuilder = new BagAddressNodeBuilder();
    
	@Override
	public Entity buildEntity(OsmPrimitive primitive, MetaData metaData) throws BuildException {
		BagEntity entity;
        if (primitive.isIncomplete()) {
             return null;
        }
        switch (primitive.getType()) {
        case NODE:
            if (primitive.hasKey("addr:housenumber")) {
                return addressNodeBuilder.build(primitive, null);
            }
        case RELATION:
            if (primitive.hasKey("building") || primitive.hasKey("building:part")) {
                return buildingBuilder.build(primitive, null);
            }
        case WAY:
            if (primitive.hasKey("building") || primitive.hasKey("building:part")) {
                return buildingBuilder.build(primitive, null);
            }
        default:
            return null;
        }
//        if (!issues.isEmpty()) {
//            throw new BuildException(issues);
//        }
    }
}
