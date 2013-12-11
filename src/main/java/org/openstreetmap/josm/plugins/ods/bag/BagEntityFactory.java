package org.openstreetmap.josm.plugins.ods.bag;

import org.opengis.feature.simple.SimpleFeature;
import org.openstreetmap.josm.plugins.ods.entities.BuildException;
import org.openstreetmap.josm.plugins.ods.entities.EntityFactory;
import org.openstreetmap.josm.plugins.ods.entities.external.ExternalBuiltEnvironmentAnalyzer;
import org.openstreetmap.josm.plugins.ods.entities.external.ExternalEntity;
import org.openstreetmap.josm.plugins.ods.entities.external.ExternalEntityAnalyzer;
import org.openstreetmap.josm.plugins.ods.metadata.MetaData;

public class BagEntityFactory implements EntityFactory {
	@Override
	public ExternalEntity buildEntity(String entityType, MetaData metaData, SimpleFeature feature) throws BuildException {
		ExternalEntity entity;
		switch (entityType) {
		case "address":
			entity = new BagAddressNode();
			break;
		case "building":
			entity = new BagBuilding();
			break;
		case "houseboat":
			entity = new BagHouseboat();
			break;
//		case "static_caravan":
//			entity = new BagBuilding(BagBuildingType.static_caravan);
//			break;
		case "city":
			entity = new BagCity();
			break;
		default:
			return null;
		}
		entity.setFeature(feature);
		entity.init(metaData);
		return entity;
	}
	
	@Override
	public ExternalEntityAnalyzer getEntityAnalyzer() {
		return new ExternalBuiltEnvironmentAnalyzer();
	}

}
