package org.openstreetmap.josm.plugins.openservices.bag;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.openstreetmap.josm.plugins.openservices.entities.buildings.ImportBuilding;

public class BagBuilding extends ImportBuilding {
	private final static DateFormat dateFormat= new SimpleDateFormat("YYYY-MM-dd");
	private Long identificatie;
	private Integer bouwjaar;
	private String status;
	private String gebruiksdoel;
	private Double oppervlakte_min;
	private Double oppervlakte_max;
	private Long aantal_verblijfsobjecten;
	private Date bagExtract;
	
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

	@Override
	protected Map<String, String> getKeys() {
		Map<String, String> keys = super.getKeys();
		keys.put("source", "BAG");
		keys.put("bag:bouwjaar", getBouwjaar().toString());
		keys.put("ref:bagid", getIdentificatie().toString());
		keys.put("bag:status", getStatus());
		keys.put("bag:extract", dateFormat.format(getBagExtract()));
		return keys;
	}
	
}
