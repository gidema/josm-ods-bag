package org.openstreetmap.josm.plugins.openservices.bag;

import java.util.Date;

import org.opengis.feature.simple.SimpleFeature;
import org.openstreetmap.josm.plugins.openservices.MappingException;
import org.openstreetmap.josm.plugins.openservices.crs.CRSUtil;
import org.openstreetmap.josm.plugins.openservices.entities.ImportEntityBuilder;
import org.openstreetmap.josm.plugins.openservices.entities.imprt.ImportBuilding;
import org.openstreetmap.josm.plugins.openservices.metadata.MetaData;
import org.openstreetmap.josm.plugins.openservices.metadata.MetaDataException;

import com.vividsolutions.jts.geom.Polygon;

public class BagPandEntityBuilder implements ImportEntityBuilder<ImportBuilding> {
	private MetaData context;
	// TODO don't hardcode feature name
    private final static String FEATURE_NAME = "bagviewer:pand";
    
	public void setContext(MetaData context) throws MetaDataException {
		this.context = context;
	}

	public String getFeatureName() {
		return FEATURE_NAME;
	}

	public ImportBuilding build(SimpleFeature feature) throws MappingException {
		BagBuilding building = new BagBuilding();
		building.setFeature(feature);
		building.setIdentificatie(((Double)feature.getProperty("identificatie").getValue()).longValue());
		building.setBouwjaar(((Double)feature.getProperty("bouwjaar").getValue()).intValue());
		building.setStatus((String)feature.getProperty("status").getValue());
		building.setGebruiksdoel((String)feature.getProperty("gebruiksdoel").getValue());
		building.setOppervlakte_min((Double)feature.getProperty("oppervlakte_min").getValue());
		building.setOppervlakte_max((Double)feature.getProperty("oppervlakte_max").getValue());
		building.setAantal_verblijfsobjecten((Long)feature.getProperty("aantal_verblijfsobjecten").getValue());
		building.setGeometry((Polygon) CRSUtil.transform((SimpleFeature) feature));
		try {
			building.setBagExtract((Date) context.get("bag.source.date"));
		} catch (MetaDataException e) {
			throw new MappingException(e.getMessage());
		}
		return building;
	}
  
	
}
