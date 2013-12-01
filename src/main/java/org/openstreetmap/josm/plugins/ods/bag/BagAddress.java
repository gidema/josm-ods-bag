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
import org.openstreetmap.josm.plugins.ods.entities.imported.ImportedAddress;
import org.openstreetmap.josm.plugins.ods.issue.ImportIssue;
import org.openstreetmap.josm.plugins.ods.issue.Issue;
import org.openstreetmap.josm.plugins.ods.metadata.MetaData;
import org.openstreetmap.josm.plugins.ods.metadata.MetaDataException;

import com.vividsolutions.jts.geom.Point;

public class BagAddress extends ImportedAddress {
	private final static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	private Long identificatie;
	private Integer huisnummer;
	private String huisletter;
	private String huisnummertoevoeging;
	private String postcode;
	private String status;
	private String gebruiksdoel;
	private Long gerelateerdPand;
	private String openbareRuimte;
	private String woonplaats;
	private Date bagExtract;
	private String houseNumber;
	private Point geometry;

	public void init(MetaData metaData) throws BuildException {
		SimpleFeature feature = getFeature();
		identificatie = ((Double) feature.getProperty("identificatie").getValue()).longValue();
		huisnummer = ((Double) feature.getProperty("huisnummer").getValue()).intValue();
		huisletter = (String) feature.getProperty("huisletter").getValue();
		huisnummertoevoeging = (String) feature.getProperty("toevoeging").getValue();
		status = (String) feature.getProperty("status").getValue();
		gebruiksdoel = (String) feature.getProperty("gebruiksdoel").getValue();
		gerelateerdPand = ((Double) feature.getProperty("pandidentificatie").getValue()).longValue();
		openbareRuimte = (String) feature.getProperty("openbare_ruimte").getValue();
		woonplaats = (String) feature.getProperty("woonplaats").getValue();
		postcode = (String) feature.getProperty("postcode").getValue();
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

	public Serializable getId() {
		return getIdentificatie();
	}

	public Long getIdentificatie() {
		return identificatie;
	}

	public Integer getHuisnummer() {
		return huisnummer;
	}

	public String getHuisLetter() {
		return huisletter;
	}

	public String getHuisNummerToevoeging() {
		return huisnummertoevoeging;
	}

	public String getStatus() {
		return status;
	}

	public String getGebruiksdoel() {
		return gebruiksdoel;
	}

	public Long getGerelateerdPand() {
		return gerelateerdPand;
	}

	public Serializable getBuildingRef() {
		return gerelateerdPand;
	}

	public String getOpenbareRuimte() {
		return openbareRuimte;
	}

	public String getWoonplaats() {
		return woonplaats;
	}

	public String getPostcode() {
		return postcode;
	}

	@Override
	public String getHouseNumber() {
		if (houseNumber == null) {
			houseNumber = getHuisnummer().toString();
			if (getHuisLetter() != null) {
				houseNumber += getHuisLetter();
			}
			if (getHuisNummerToevoeging() != null) {
				houseNumber += getHuisNummerToevoeging();
			}
		}
		return houseNumber;
	}

	@Override
	public String getStreetName() {
		return getOpenbareRuimte();
	}

	@Override
	public String getPlaceName() {
		return getWoonplaats();
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
	protected Map<String, String> getKeys() {
		Map<String, String> keys = super.getKeys();
		keys.put("source", "BAG");
		keys.put("source:date", dateFormat.format(getBagExtract()));
		// keys.put("ref:bagid", getIdentificatie().toString());
		// keys.put("bag:status", getStatus());
		keys.put("bag:function", getGebruiksdoel());
		return keys;
	}

}
