package org.openstreetmap.josm.plugins.openservices.bag;

import java.io.Serializable;
import java.util.Date;

import org.openstreetmap.josm.plugins.openservices.crs.CRSUtil;
import org.openstreetmap.josm.plugins.openservices.entities.buildings.ImportAddress;

import com.vividsolutions.jts.geom.Point;

public class BagAddress extends ImportAddress {
	private Long identificatie;
	private Integer huisnummer;
	private String houseNumber;
	private String postcode;
	private String status;
	private String gebruiksdoel;
	private Long gerelateerdPand;
	private String openbareRuimte;
	private String woonplaats;
	private Date bagExtract;
	private Point geometry;
	
	public Serializable getId() {
		return identificatie;
	}

	public Long getIdentificatie() {
		if (identificatie == null) {
			identificatie = ((Double)getFeature().getProperty("identificatie").getValue()).longValue();
		}
		return identificatie;
	}

	public Integer getHuisnummer() {
		if (huisnummer == null) {
			huisnummer = ((Double)getFeature().getProperty("huisnummer").getValue()).intValue();
		}
		return huisnummer;
	}

	public String getHuisLetter() {
		// No use in caching as the value will be null in most cases
		return (String) getFeature().getProperty("huisletter").getValue();
	}

	public String getHuisNummerToevoeging() {
		// No use in caching as the value will be null in most cases
		return (String) getFeature().getProperty("toevoeging").getValue();
	}

	public String getStatus() {
		if (status == null) {
			status = (String) getFeature().getProperty("status").getValue();
		}
		return status;
	}

	public String getGebruiksdoel() {
		if (gebruiksdoel == null) {
			gebruiksdoel = (String) getFeature().getProperty("gebruiksdoel").getValue();
		}
		return gebruiksdoel;
	}

	public Long getGerelateerdPand() {
		if (gerelateerdPand == null) {
			gerelateerdPand =((Double)getFeature().getProperty("pandidentificatie").getValue()).longValue();
        }
		return gerelateerdPand;
	}
	
	public Serializable getBuildingRef() {
		return gerelateerdPand;
	}

	public String getOpenbareRuimte() {
		if (openbareRuimte == null) {
			openbareRuimte = (String) getFeature().getProperty("openbare_ruimte").getValue();
		}
		return openbareRuimte;
	}

	public String getWoonplaats() {
		if (woonplaats == null) {
			woonplaats = (String) getFeature().getProperty("woonplaats").getValue();
		}
		return woonplaats;
	}

	public String getPostcode() {
		if (postcode == null) {
			postcode = (String) getFeature().getProperty("postcode").getValue();
		}
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
		return openbareRuimte;
	}

	public Date getBagExtract() {
		return bagExtract;
	}

	public void setBagExtract(Date bagExtract) {
		this.bagExtract = bagExtract;
	}
	
	public Point getGeometry() {
		if (geometry == null) {
			geometry = (Point) CRSUtil.transform(getFeature());
		}
		return geometry;
	}
}
