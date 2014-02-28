package org.openstreetmap.josm.plugins.ods.bag;

import org.opengis.feature.simple.SimpleFeature;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.plugins.ods.bag.external.ExternalBagAddressNode;
import org.openstreetmap.josm.plugins.ods.bag.external.ExternalBagBuilding;
import org.openstreetmap.josm.plugins.ods.bag.external.ExternalBagCity;
import org.openstreetmap.josm.plugins.ods.bag.external.ExternalBagEntity;
import org.openstreetmap.josm.plugins.ods.bag.external.ExternalBagLigplaats;
import org.openstreetmap.josm.plugins.ods.bag.external.ExternalBagStandplaats;
import org.openstreetmap.josm.plugins.ods.bag.internal.InternalBagAddressNode;
import org.openstreetmap.josm.plugins.ods.bag.internal.InternalBagBuilding;
import org.openstreetmap.josm.plugins.ods.bag.internal.InternalBagLigplaats;
import org.openstreetmap.josm.plugins.ods.bag.internal.InternalBagStandplaats;
import org.openstreetmap.josm.plugins.ods.entities.BuildException;
import org.openstreetmap.josm.plugins.ods.entities.Entity;
import org.openstreetmap.josm.plugins.ods.entities.EntityFactory;
import org.openstreetmap.josm.plugins.ods.metadata.MetaData;

public class BagEntityFactory implements EntityFactory {
	@Override
	public Entity buildEntity(String entityType, MetaData metaData, SimpleFeature feature) throws BuildException {
		ExternalBagEntity entity;
		switch (entityType) {
		case "address":
			entity = new ExternalBagAddressNode(feature);
			break;
		case "building":
			entity = new ExternalBagBuilding(feature);
			break;
		case "houseboat":
			entity = new ExternalBagLigplaats(feature);
			break;
		case "static_caravan":
			entity = new ExternalBagStandplaats(feature);
			break;
		case "city":
			entity = new ExternalBagCity(feature);
			break;
		default:
			return null;
		}
		entity.init(metaData);
		return entity;
	}
	
	@Override
	public Entity buildEntity(String entityType, OsmPrimitive primitive)
			throws BuildException {
		Entity entity;
		switch (entityType) {
		case "address":
			entity = new InternalBagAddressNode(primitive);
			break;
		case "building":
			entity = new InternalBagBuilding(primitive);
			break;
		case "houseboat":
			entity = new InternalBagLigplaats(primitive);
			break;
		case "static_caravan":
			entity = new InternalBagStandplaats(primitive);
			break;
		default:
			return null;
		}
		entity.build();
		return entity;
	}
}
