package org.openstreetmap.josm.plugins.ods.bag;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.opengis.feature.simple.SimpleFeature;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.plugins.ods.crs.CRSException;
import org.openstreetmap.josm.plugins.ods.crs.CRSUtil;
import org.openstreetmap.josm.plugins.ods.entities.BuildException;
import org.openstreetmap.josm.plugins.ods.entities.external.ExternalCity;
import org.openstreetmap.josm.plugins.ods.issue.ImportIssue;
import org.openstreetmap.josm.plugins.ods.issue.Issue;
import org.openstreetmap.josm.plugins.ods.metadata.MetaData;
import org.openstreetmap.josm.plugins.ods.metadata.MetaDataException;

import com.vividsolutions.jts.geom.MultiPolygon;

public class BagCity extends ExternalCity {
	private final static DateFormat dateFormat = new SimpleDateFormat(
			"yyyy-MM-dd");
	private Long identificatie;
	private Date bagExtract;

	public void init(MetaData metaData) throws BuildException {
		SimpleFeature feature = getFeature();
		identificatie = ((Double) feature.getProperty("identificatie").getValue()).longValue();
		name = (String) feature.getProperty("woonplaats").getValue();
		try {
			geometry = (MultiPolygon) CRSUtil.getInstance().transform(feature);
		} catch (CRSException e) {
			Issue issue = new ImportIssue(feature.getID(), e);
		    throw new BuildException(issue);
		}
		try {
			bagExtract = (Date) metaData.get("bag.source.date");
		} catch (MetaDataException e) {
			Issue issue = new ImportIssue(feature.getID(), e);
			throw new BuildException(issue);
		}
	}
	
	public void build() throws BuildException {
		
	}

	public Serializable getId() {
		return getIdentificatie();
	}

	public Long getIdentificatie() {
		return identificatie;
	}

	@Override
	protected void buildTags(OsmPrimitive primitive) {
		super.buildTags(primitive);
		primitive.put("ref:woonplaatscode", getIdentificatie().toString());
        primitive.put("admin_level", "10");
        primitive.put("authoritative", "yes");
		primitive.put("source", "BAG");
		primitive.put("source:date", dateFormat.format(bagExtract));
	}

}
