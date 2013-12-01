package org.openstreetmap.josm.plugins.ods.bag;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.opengis.feature.simple.SimpleFeature;
import org.openstreetmap.josm.plugins.ods.crs.CRSException;
import org.openstreetmap.josm.plugins.ods.crs.CRSUtil;
import org.openstreetmap.josm.plugins.ods.entities.BuildException;
import org.openstreetmap.josm.plugins.ods.entities.imported.ImportedBuilding;
import org.openstreetmap.josm.plugins.ods.issue.ImportIssue;
import org.openstreetmap.josm.plugins.ods.issue.Issue;
import org.openstreetmap.josm.plugins.ods.metadata.MetaData;
import org.openstreetmap.josm.plugins.ods.metadata.MetaDataException;

import com.vividsolutions.jts.geom.MultiPolygon;

public class BagBuilding extends ImportedBuilding {
	private final static DateFormat dateFormat= new SimpleDateFormat("yyyy-MM-dd");
	private Long identificatie;
	private Integer bouwjaar;
	private String status;
	private String gebruiksdoel;
	private Double oppervlakte_min;
	private Double oppervlakte_max;
	private Long aantal_verblijfsobjecten;
	private Date bagExtract;
	
	public void init(MetaData metaData) throws BuildException {
		SimpleFeature feature = getFeature();
		identificatie = ((Double)feature.getProperty("identificatie").getValue()).longValue();
		bouwjaar = ((Double)feature.getProperty("bouwjaar").getValue()).intValue();
		status = (String)feature.getProperty("status").getValue();
		gebruiksdoel = (String)feature.getProperty("gebruiksdoel").getValue();
		oppervlakte_min = (Double)feature.getProperty("oppervlakte_min").getValue();
		oppervlakte_max = (Double)feature.getProperty("oppervlakte_max").getValue();
		aantal_verblijfsobjecten = (Long)feature.getProperty("aantal_verblijfsobjecten").getValue();
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

	public Integer getBouwjaar() {
		return bouwjaar;
	}

	public void setBouwjaar(Integer bouwjaar) {
		this.bouwjaar = bouwjaar;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getGebruiksdoel() {
		return gebruiksdoel;
	}

	public void setGebruiksdoel(String gebruiksdoel) {
		this.gebruiksdoel = gebruiksdoel;
	}

	public Double getOppervlakte_min() {
		return oppervlakte_min;
	}

	public void setOppervlakte_min(Double oppervlakte_min) {
		this.oppervlakte_min = oppervlakte_min;
	}

	public Double getOppervlakte_max() {
		return oppervlakte_max;
	}

	public void setOppervlakte_max(Double oppervlakte_max) {
		this.oppervlakte_max = oppervlakte_max;
	}

	public Long getAantal_verblijfsobjecten() {
		return aantal_verblijfsobjecten;
	}

	public void setAantal_verblijfsobjecten(Long aantal_verblijfsobjecten) {
		this.aantal_verblijfsobjecten = aantal_verblijfsobjecten;
	}

	public Date getBagExtract() {
		return bagExtract;
	}

	public void setBagExtract(Date bagExtract) {
		this.bagExtract = bagExtract;
	}

	
	public boolean isUnderConstruction() {
		return "bouw gestart".equals(status);
	}

	public String getStartDate() {
		return bouwjaar.toString();
	}

	@Override
	protected Map<String, String> getKeys() {
		Map<String, String> keys = super.getKeys();
		// TODO skip null values
		keys.put("source", "BAG");
		keys.put("source:date", dateFormat.format(getBagExtract()));
		keys.put("start_date", getStartDate().toString());
		keys.put("ref:bag", getIdentificatie().toString());
//		keys.put("bag:status", getStatus());
		return keys;
	}
	
}
