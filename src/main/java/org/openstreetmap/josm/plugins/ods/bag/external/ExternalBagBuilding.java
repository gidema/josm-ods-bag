package org.openstreetmap.josm.plugins.ods.bag.external;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.opengis.feature.simple.SimpleFeature;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.plugins.ods.bag.external.ExternalBagBuilding.Statistics.Stat;
import org.openstreetmap.josm.plugins.ods.crs.CRSException;
import org.openstreetmap.josm.plugins.ods.crs.CRSUtil;
import org.openstreetmap.josm.plugins.ods.entities.BuildException;
import org.openstreetmap.josm.plugins.ods.entities.Entity;
import org.openstreetmap.josm.plugins.ods.entities.builtenvironment.AddressNode;
import org.openstreetmap.josm.plugins.ods.entities.builtenvironment.Block;
import org.openstreetmap.josm.plugins.ods.entities.builtenvironment.Building;
import org.openstreetmap.josm.plugins.ods.entities.builtenvironment.City;
import org.openstreetmap.josm.plugins.ods.entities.external.FeatureUtil;
import org.openstreetmap.josm.plugins.ods.issue.ImportIssue;
import org.openstreetmap.josm.plugins.ods.issue.Issue;
import org.openstreetmap.josm.plugins.ods.metadata.MetaData;

import com.vividsolutions.jts.geom.MultiPolygon;

public class ExternalBagBuilding extends ExternalBagEntity implements Building {
	private final static List<String> trafo =
		Arrays.asList("TRAF","TRAN","TRFO","TRNS");
	private final static List<String> garage =
			Arrays.asList("GAR","GRG");
	
	private Integer bouwjaar;
	private String status;
	private boolean incomplete;
	private String gebruiksdoel;
	private Double oppervlakte_min;
	private Double oppervlakte_max;
	private Set<AddressNode> addresses = new HashSet<>();
	private Long aantal_verblijfsobjecten;
	private Set<Building> neighbours = new HashSet<>();
	private Block block;
	private City city;
	
	public ExternalBagBuilding(SimpleFeature feature) {
		super(feature);
	}

	public void init(MetaData metaData) throws BuildException {
		super.init(metaData);
		bouwjaar = FeatureUtil.getInteger(feature, "bouwjaar");
		status = FeatureUtil.getString(feature, "status");
		gebruiksdoel = FeatureUtil.getString(feature, "gebruiksdoel");
		oppervlakte_min = FeatureUtil.getDouble(feature, "oppervlakte_min");
		oppervlakte_max = FeatureUtil.getDouble(feature, "oppervlakte_max");
		aantal_verblijfsobjecten = FeatureUtil.getLong(feature, "aantal_verblijfsobjecten");
		try {
			setGeometry((MultiPolygon) CRSUtil.getInstance().transform((SimpleFeature) feature));
		} catch (CRSException e) {
			Issue issue = new ImportIssue(feature.getID(), e);
			throw new BuildException(issue);
		}
	}
	
	@Override
	public Class<? extends Entity> getType() {
		return Building.class;
	}

	public Integer getBouwjaar() {
		return bouwjaar;
	}

	public String getStatus() {
		return status;
	}

	public String getGebruiksdoel() {
		return gebruiksdoel;
	}

	public Double getOppervlakte_min() {
		return oppervlakte_min;
	}

	public Double getOppervlakte_max() {
		return oppervlakte_max;
	}

	public Long getAantal_verblijfsobjecten() {
		return aantal_verblijfsobjecten;
	}

	public boolean isUnderConstruction() {
		return "bouw gestart".equals(status);
	}

	public void setIncomplete(boolean incomplete) {
		this.incomplete = incomplete;
	}
	
	@Override
	public boolean isIncomplete() {
		return incomplete;
	}

	@Override
	public boolean isDeleted() {
		return "Pand gesloopt".equals(status);
	}

	@Override
	public String getName() {
		return null;
	}

	public String getStartDate() {
		return (bouwjaar == null ? null : bouwjaar.toString());
	}

	@Override
	public void buildTags(OsmPrimitive primitive) {
		super.buildTags(primitive);
		primitive.put("start_date", getStartDate());
		primitive.put("ref:bag", getId().toString());
		primitive.put("building", "yes");
    	analyzeBuildingType(primitive);
	}
	
	private void analyzeBuildingType(OsmPrimitive primitive) {
		if (addresses.isEmpty()) {
			return;
		}
		if (addresses.size() == 1) {
			analyzeBuildingType((ExternalBagAddressNode) addresses.toArray()[0], primitive);
		}
		else {
			analyzeBuildingType(addresses, primitive);
		}
		return;
	}

	private void analyzeBuildingType(Set<AddressNode> addresses, OsmPrimitive primitive) {
		Statistics stats = new Statistics();
		Iterator<AddressNode> it = addresses.iterator();
		while (it.hasNext()) {
			ExternalBagAddressNode address = (ExternalBagAddressNode) it.next();
			stats.add(address.getGebruiksdoel(), address.getArea());
		}
		Stat largest = stats.getLargest();
		if (largest.percentage > 0.75) {
		    String type;
		    switch (largest.name) {
		    case "woonfunctie":
			    type = "apartments";
			    break;
		    case "celfunctie":
		    	primitive.put("amenity", "prison");
			    type = "prison";
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
			primitive.put("building", type);
		}
	}

	private void analyzeBuildingType(ExternalBagAddressNode address, OsmPrimitive primitive) {
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

	@Override
	public City getCity() {
		return city;
	}

	@Override
	public Set<AddressNode> getAddresses() {
		return addresses;
	}

	@Override
	public void setBlock(Block block) {
		this.block = block;
	}

	@Override
	public Block getBlock() {
		return block;
	}

	@Override
	public Set<Building> getNeighbours() {
		return neighbours;
	}

	@Override
	public void addNeighbour(Building building) {
		this.neighbours.add(building);
	}
	
	class Statistics {
		private Map<String, Row> rows = new HashMap<>();
		private double totalArea = 0.0;
		
		public void add(String name, double area) {
			Row row = rows.get(name);
			if (row == null) {
				row = new Row();
				rows.put(name,  row);
			}
			row.add(area);
			totalArea += area;
		}
		
		public Stat getLargest() {
			Stat stat = new Stat();
			for (Entry<String, Row> entry : rows.entrySet()) {
				Row row = entry.getValue();
				if (row.area > stat.area) {
					stat.name = entry.getKey();
					stat.area = row.area;
					stat.count = row.count; 
				}
			}
			stat.percentage = stat.area/totalArea;
			return stat;
		}
		
		class Stat {
			String name = "";
			int count = 0;
			double area = 0.0;
			double percentage = 0.0;
		}
		
	 	class Row {
			int count = 0;
			double area = 0;
			
			public void add(double area) {
				this.area += area;
				this.count++;
			}
		}
	}
	
}
