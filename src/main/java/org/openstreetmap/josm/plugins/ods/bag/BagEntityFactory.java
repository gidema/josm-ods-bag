package org.openstreetmap.josm.plugins.ods.bag;

import org.opengis.feature.simple.SimpleFeature;
import org.openstreetmap.josm.plugins.ods.entities.BuildException;
import org.openstreetmap.josm.plugins.ods.entities.EntityFactory;
import org.openstreetmap.josm.plugins.ods.entities.builtenvironment.Address;
import org.openstreetmap.josm.plugins.ods.entities.builtenvironment.Building;
import org.openstreetmap.josm.plugins.ods.entities.builtenvironment.City;
import org.openstreetmap.josm.plugins.ods.entities.external.ExternalBuiltEnvironmentAnalyzer;
import org.openstreetmap.josm.plugins.ods.entities.external.ExternalEntity;
import org.openstreetmap.josm.plugins.ods.entities.external.ExternalEntityAnalyzer;
import org.openstreetmap.josm.plugins.ods.metadata.MetaData;

public class BagEntityFactory implements EntityFactory {
	@Override
	public ExternalEntity buildEntity(String entityType, MetaData metaData, SimpleFeature feature) throws BuildException {
		ExternalEntity entity;
		if (Address.TYPE.equals(entityType)) {
			entity = new BagAddress();
		}
		else if (Building.TYPE.equals(entityType)) {
			entity = new BagBuilding();
		}
		else if (City.TYPE.equals(entityType)) {
			entity = new BagCity();
		}
		else {
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
