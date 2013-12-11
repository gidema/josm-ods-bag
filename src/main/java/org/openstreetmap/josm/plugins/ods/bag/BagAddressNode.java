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
import org.openstreetmap.josm.plugins.ods.entities.external.ExternalAddressNode;
import org.openstreetmap.josm.plugins.ods.issue.ImportIssue;
import org.openstreetmap.josm.plugins.ods.issue.Issue;
import org.openstreetmap.josm.plugins.ods.metadata.MetaData;
import org.openstreetmap.josm.plugins.ods.metadata.MetaDataException;

import com.vividsolutions.jts.geom.Point;

public class BagAddressNode extends ExternalAddressNode {
	private final static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	private Long identificatie;
	private BagAddress address;
	private String status;
	private String gebruiksdoel;
	private Long gerelateerdPand;
	private Date bagExtract;
	private Point geometry;

	public void init(MetaData metaData) throws BuildException {
		SimpleFeature feature = getFeature();
		address = new BagAddress(feature);
		address.init(metaData);
		identificatie = ((Double) feature.getProperty("identificatie").getValue()).longValue();
		status = (String) feature.getProperty("status").getValue();
		gebruiksdoel = (String) feature.getProperty("gebruiksdoel").getValue();
		gerelateerdPand = ((Double) feature.getProperty("pandidentificatie").getValue()).longValue();
		try {
			geometry = (Point) CRSUtil.getInstance().transform(feature);
		} catch (CRSException e) {
		// TODO Auto-generated catch block
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

	@Override
	public BagAddress getAddress() {
		return (BagAddress) super.getAddress();
	}

	public Serializable getId() {
		return getIdentificatie();
	}

	public Long getIdentificatie() {
		return identificatie;
	}

	public String getStatus() {
		return status;
	}
	
	public String getGebruiksdoel() {
		return gebruiksdoel;
	}

	public Serializable getBuildingRef() {
		return gerelateerdPand;
	}

	public Date getBagExtract() {
		return bagExtract;
	}

	public void setBagExtract(Date bagExtract) {
		this.bagExtract = bagExtract;
	}

	public Point getGeometry() {
		return geometry;
	}

	@Override
	protected void buildTags(OsmPrimitive primitive) {
		super.buildTags(primitive);
		primitive.put("source", "BAG");
		primitive.put("source:date", dateFormat.format(getBagExtract()));
		// keys.put("ref:bagid", getIdentificatie().toString());
		// keys.put("bag:status", getStatus());
//		if (!"woonfunctie".equalsIgnoreCase(gebruiksdoel)) {
//		    keys.put("bag:function", gebruiksdoel);
//		}
	}

}
