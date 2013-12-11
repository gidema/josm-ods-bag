package org.openstreetmap.josm.plugins.ods.bag;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

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

public class BagBuilding extends ExternalBuilding {
	private final static List<String> trafo =
		Arrays.asList("TRAF","TRAN","TRFO","TRNS");
	private final static List<String> garage =
			Arrays.asList("GAR","GRG");
	
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
	protected void buildTags(OsmPrimitive primitive) {
		super.buildTags(primitive);
		primitive.put("source", "BAG");
		primitive.put("source:date", dateFormat.format(getBagExtract()));
		primitive.put("start_date", getStartDate().toString());
		primitive.put("ref:bag", getIdentificatie().toString());
    	analyzeBuildingType(primitive);
	}
	
	private void analyzeBuildingType(OsmPrimitive primitive) {
		if (getAddresses().isEmpty()) {
			return;
		}
		if (getAddresses().size() == 1) {
			analyzeBuildingType((BagAddressNode) getAddresses().toArray()[0], primitive);
		}
		return;
	}

	private void analyzeBuildingType(BagAddressNode address, OsmPrimitive primitive) {
		String type;
		switch (address.getGebruiksdoel().toLowerCase()) {
		case "woonfunctie":
			type = "house";
			break;
		case "overige gebruiksfunctie":
			type = "yes";
			break;
		case "industriefunctie":
			type = "industrial";
			break;
		case "winkelfunctie":
			type = "retail";
			break;
		case "kantoorfunctie":
			type = "commercial";
			break;
		default: 
			type = "yes";
		}
		String extra = address.getAddress().getHuisNummerToevoeging();
		if (extra != null) {
			extra = extra.toUpperCase();
			if (trafo.contains(extra)) {
				primitive.put("power", "sub_station");
			}
			else if (garage.contains(extra)) {
				type = "garage";
			}
		}
		primitive.put("building", type);
	}
}
