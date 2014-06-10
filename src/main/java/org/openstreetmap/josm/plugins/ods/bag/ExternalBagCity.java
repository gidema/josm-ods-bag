package org.openstreetmap.josm.plugins.ods.bag;

import org.opengis.feature.simple.SimpleFeature;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.plugins.ods.bag.external.ExternalBagEntity;
import org.openstreetmap.josm.plugins.ods.crs.CRSException;
import org.openstreetmap.josm.plugins.ods.crs.CRSUtil;
import org.openstreetmap.josm.plugins.ods.entities.BuildException;
import org.openstreetmap.josm.plugins.ods.entities.Entity;
import org.openstreetmap.josm.plugins.ods.entities.builtenvironment.City;
import org.openstreetmap.josm.plugins.ods.issue.ImportIssue;
import org.openstreetmap.josm.plugins.ods.issue.Issue;
import org.openstreetmap.josm.plugins.ods.metadata.MetaData;

import com.vividsolutions.jts.geom.MultiPolygon;

public class ExternalBagCity extends ExternalBagEntity {
	private String name;
	
	public ExternalBagCity(SimpleFeature feature) {
		super(feature);
	}

	public void init(MetaData metaData) throws BuildException {
		name = (String) feature.getProperty("woonplaats").getValue();
		try {
			geometry = (MultiPolygon) CRSUtil.getInstance().transform(feature);
		} catch (CRSException e) {
			Issue issue = new ImportIssue(feature.getID(), e);
		    throw new BuildException(issue);
		}
	}
	
	
	@Override
	public Class<? extends Entity> getType() {
		return City.class;
	}


	@Override
	public boolean isIncomplete() {
		return true;
	}


	@Override
	public boolean isDeleted() {
		return false;
	}


	@Override
	public String getName() {
		return name;
	}


	public void build() throws BuildException {
		
	}

	@Override
	public void buildTags(OsmPrimitive primitive) {
		super.buildTags(primitive);
		primitive.put("ref:woonplaatscode", getId().toString());
        primitive.put("admin_level", "10");
        primitive.put("authoritative", "yes");
	}
}
