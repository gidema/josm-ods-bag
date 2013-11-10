package org.openstreetmap.josm.plugins.openservices.bag;

import java.util.Date;

import org.opengis.feature.simple.SimpleFeature;
import org.openstreetmap.josm.plugins.openservices.MappingException;
import org.openstreetmap.josm.plugins.openservices.entities.ImportEntityBuilder;
import org.openstreetmap.josm.plugins.openservices.metadata.MetaData;
import org.openstreetmap.josm.plugins.openservices.metadata.MetaDataException;

public class BagAdresEntityBuilder implements ImportEntityBuilder<BagAddress> {
	private MetaData context;
	// TODO don't hardcode feature name
    private final static String FEATURE_NAME = "bagviewer:verblijfsobject";
    
	public void setContext(MetaData context) throws MetaDataException {
		this.context = context;
	}

	public String getFeatureName() {
		return FEATURE_NAME;
	}

	public BagAddress build(SimpleFeature feature) throws MappingException {
		BagAddress address = new BagAddress();
		address.setFeature(feature);
		try {
			address.setBagExtract((Date) context.get("bag.source.date"));
		} catch (MetaDataException e) {
			throw new MappingException(e.getMessage());
		}
		return address;
	}
}
