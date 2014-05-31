package org.openstreetmap.josm.plugins.ods.bag.gt;

import org.opengis.feature.simple.SimpleFeature;
import org.openstreetmap.josm.plugins.ods.bag.BagEntity;
import org.openstreetmap.josm.plugins.ods.entities.BuildException;
import org.openstreetmap.josm.plugins.ods.entities.Entity;
import org.openstreetmap.josm.plugins.ods.entities.EntityFactory;
import org.openstreetmap.josm.plugins.ods.metadata.MetaData;

public class BagEntityFactory implements EntityFactory<SimpleFeature> {
    private BagBuildingBuilder buildingBuilder = new BagBuildingBuilder("yes");
    private BagBuildingBuilder houseboatBuilder = new BagBuildingBuilder("houseboat");
    private BagBuildingBuilder staticCaravanBuilder = new BagBuildingBuilder("static_caravan");
    private BagAddressNodeBuilder addressNodeBuilder = new BagAddressNodeBuilder();
    
	@Override
	public Entity buildEntity(SimpleFeature feature, MetaData metaData) throws BuildException {
		BagEntity entity;
		switch (feature.getName().getLocalPart()) {
		case "bag:verblijfsobject":
		case "nevenadres":
			entity = addressNodeBuilder.build(feature, metaData);
			break;
		case "bag:pand":
            entity = buildingBuilder.build(feature, metaData);
            break;
        case "bag:ligplaats":
            entity = houseboatBuilder.build(feature, metaData);
            break;
        case "bag:standplaats":
			entity = staticCaravanBuilder.build(feature, metaData);
			break;
//		case "city":
//			entity = new ExternalBagCity(feature);
//			break;
		default:
			return null;
		}
		return entity;
	}
}
