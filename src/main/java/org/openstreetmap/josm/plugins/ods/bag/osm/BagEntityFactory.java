package org.openstreetmap.josm.plugins.ods.bag.osm;

import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.plugins.ods.entities.BuildException;
import org.openstreetmap.josm.plugins.ods.entities.Entity;
import org.openstreetmap.josm.plugins.ods.entities.EntityFactory;
import org.openstreetmap.josm.plugins.ods.metadata.MetaData;

public class BagEntityFactory implements EntityFactory<OsmPrimitive> {
    private BagBuildingBuilder buildingBuilder = new BagBuildingBuilder();
    private BagAddressNodeBuilder addressNodeBuilder = new BagAddressNodeBuilder();
    
	@Override
	public Entity buildEntity(OsmPrimitive primitive, MetaData metaData) throws BuildException {
        if (primitive.isIncomplete()) {
             return null;
        }
        switch (primitive.getType()) {
        case NODE:
            if (primitive.hasKey("addr:housenumber")) {
                return addressNodeBuilder.build(primitive, null);
            }
            return null;
        case RELATION:
            if (primitive.hasKey("building") || primitive.hasKey("building:part")) {
                return buildingBuilder.build(primitive, null);
            }
            return null;
        case WAY:
            if (primitive.hasKey("building") || primitive.hasKey("building:part")) {
                return buildingBuilder.build(primitive, null);
            }
            return null;
        default:
            return null;
        }
    }
}
