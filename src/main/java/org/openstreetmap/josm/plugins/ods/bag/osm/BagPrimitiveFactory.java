package org.openstreetmap.josm.plugins.ods.bag.osm;

import java.util.HashMap;
import java.util.Map;

import org.openstreetmap.josm.data.osm.DataSet;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.plugins.ods.bag.BagEntity;
import org.openstreetmap.josm.plugins.ods.builtenvironment.AddressNode;
import org.openstreetmap.josm.plugins.ods.builtenvironment.Building;
import org.openstreetmap.josm.plugins.ods.entities.Entity;
import org.openstreetmap.josm.plugins.ods.osm.PrimitiveFactory;

public class BagPrimitiveFactory implements PrimitiveFactory {
    private Map<Class<? extends Entity>, BagPrimitiveBuilder<? extends BagEntity>> builders = 
        new HashMap<>();
    
    public BagPrimitiveFactory(DataSet dataSet) {
        builders.put(Building.class, new BagBuildingPrimitiveBuilder(dataSet));
        builders.put(AddressNode.class,  new BagAddressNodePrimitiveBuilder(dataSet));
    }
    
    @Override
    public OsmPrimitive[] buildPrimitives(Entity entity) {
        @SuppressWarnings("unchecked")
        BagPrimitiveBuilder<BagEntity> builder = (BagPrimitiveBuilder<BagEntity>) builders.get(entity.getType());
        return builder.createPrimitives((BagEntity) entity);
    }
}
