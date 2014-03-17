package org.openstreetmap.josm.plugins.ods.bag.gt;

import org.opengis.feature.simple.SimpleFeature;
import org.openstreetmap.josm.plugins.ods.bag.BagEntity;
import org.openstreetmap.josm.plugins.ods.entities.BuildException;
import org.openstreetmap.josm.plugins.ods.entities.Entity;
import org.openstreetmap.josm.plugins.ods.entities.EntityFactory;
import org.openstreetmap.josm.plugins.ods.metadata.MetaData;

public class BagEntityFactory implements EntityFactory<SimpleFeature> {
    private BagBuildingBuilder buildingBuilder = new BagBuildingBuilder();
    private BagAddressNodeBuilder addressNodeBuilder = new BagAddressNodeBuilder();
    
	@Override
	public Entity buildEntity(SimpleFeature feature, MetaData metaData) throws BuildException {
		BagEntity entity;
		switch (feature.getName().getLocalPart()) {
		case "address":
			entity = addressNodeBuilder.build(feature, metaData);
			break;
		case "building":
			entity = buildingBuilder.build(feature, metaData);
			break;
//		case "houseboat":
//			entity = new ExternalBagLigplaats(feature);
//			break;
//		case "static_caravan":
//			entity = new ExternalBagStandplaats(feature);
//			break;
//		case "city":
//			entity = new ExternalBagCity(feature);
//			break;
		default:
			return null;
		}
		return entity;
	}
}
