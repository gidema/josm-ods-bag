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
import org.openstreetmap.josm.plugins.ods.entities.external.ExternalBuilding;
import org.openstreetmap.josm.plugins.ods.issue.ImportIssue;
import org.openstreetmap.josm.plugins.ods.issue.Issue;
import org.openstreetmap.josm.plugins.ods.metadata.MetaData;
import org.openstreetmap.josm.plugins.ods.metadata.MetaDataException;

import com.vividsolutions.jts.geom.MultiPolygon;

public class BagHouseboat extends ExternalBuilding {
	private BagAddress address;
	private final static DateFormat dateFormat= new SimpleDateFormat("yyyy-MM-dd");
	private Long identificatie;
	private String status;
	private Date bagExtract;
	
	public void init(MetaData metaData) throws BuildException {
		SimpleFeature feature = getFeature();
		address = new BagAddress(feature);
		address.init(metaData);
		identificatie = ((Double)feature.getProperty("identificatie").getValue()).longValue();
		status = (String)feature.getProperty("status").getValue();
		try {
			setGeometry((MultiPolygon) CRSUtil.getInstance().transform((SimpleFeature) feature));
		} catch (CRSException e) {
			Issue issue = new ImportIssue(feature.getID(), e);
			throw new BuildException(issue);
		}
		try {
			setBagExtract((Date) metaData.get("bag.source.date"));
		} catch (MetaDataException e) {
			Issue issue = new ImportIssue(feature.getID(), e);
			throw new BuildException(issue);
		}
	}
	
	public void build() {
	}
	
	public Serializable getId() {
		return identificatie;
	}
	
	public Long getIdentificatie() {
		return identificatie;
	}

	public void setIdentificatie(Long identificatie) {
		this.identificatie = identificatie;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Date getBagExtract() {
		return bagExtract;
	}

	public void setBagExtract(Date bagExtract) {
		this.bagExtract = bagExtract;
	}

	
	public boolean isUnderConstruction() {
		return false;
	}

	public String getStartDate() {
		return null;
	}

	@Override
	protected void buildTags(OsmPrimitive primitive) {
		super.buildTags(primitive);
		address.buildTags(primitive);
		primitive.put("source", "BAG");
		primitive.put("source:date", dateFormat.format(getBagExtract()));
		primitive.put("start_date", getStartDate().toString());
		primitive.put("ref:bag", getIdentificatie().toString());
    	primitive.put("building", "houseboat");
	}
}
