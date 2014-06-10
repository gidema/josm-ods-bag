package org.openstreetmap.josm.plugins.ods.bag.external;

import org.opengis.feature.simple.SimpleFeature;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.plugins.ods.entities.BuildException;
import org.openstreetmap.josm.plugins.ods.entities.builtenvironment.Address;
import org.openstreetmap.josm.plugins.ods.metadata.MetaData;

public class ExternalBagLigplaats extends ExternalBagBuilding {
	private ExternalBagAddress address;
	
	public ExternalBagLigplaats(SimpleFeature feature) {
		super(feature);
		address = new ExternalBagAddress(feature);
	}

	@Override
	public void init(MetaData metaData) throws BuildException {
		super.init(metaData);
		address.init(metaData);
	}

	@Override
	public void buildTags(OsmPrimitive primitive) {
		super.buildTags(primitive);
		address.buildTags(primitive);
    	primitive.put("building", "houseboat");
	}
	
	public Address getAddress() {
		return address;
	}
}
