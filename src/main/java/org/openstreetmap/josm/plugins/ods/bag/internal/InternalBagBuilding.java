package org.openstreetmap.josm.plugins.ods.bag.internal;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.data.osm.OsmPrimitiveType;
import org.openstreetmap.josm.data.osm.Relation;
import org.openstreetmap.josm.data.osm.Way;
import org.openstreetmap.josm.plugins.ods.crs.InvalidGeometryException;
import org.openstreetmap.josm.plugins.ods.crs.InvalidMultiPolygonException;
import org.openstreetmap.josm.plugins.ods.entities.Entity;
import org.openstreetmap.josm.plugins.ods.entities.builtenvironment.AddressNode;
import org.openstreetmap.josm.plugins.ods.entities.builtenvironment.Block;
import org.openstreetmap.josm.plugins.ods.entities.builtenvironment.Building;
import org.openstreetmap.josm.plugins.ods.entities.builtenvironment.City;
import org.openstreetmap.josm.plugins.ods.jts.GeoUtil;

import com.vividsolutions.jts.geom.Geometry;

public class InternalBagBuilding extends InternalBagEntity implements Building {
    private Block block;
    private Set<Building> neighbours = new HashSet<>();
    private String buildingType = "yes";
    private String startDate;
    private boolean underConstruction = false;
    private Set<AddressNode> addresses = new HashSet<AddressNode>();
    private Map<String, String> addressKeys = new HashMap<>();
    private boolean hasAddress = false; // True if this building has address tags
    private boolean incomplete = false;

    public InternalBagBuilding(OsmPrimitive primitive) {
        super(primitive);
    }

    @Override
    public void setIncomplete(boolean incomplete) {
        this.incomplete = incomplete; 
    }

    @Override
    public Class<? extends Entity> getType() {
        return Building.class;
    }

	protected boolean parseKey(String key, String value) {
		if (super.parseKey(key, value)) {
			return true;
		}
        if ("building".equals(key)) {
            if ("construction".equals(value)) {
                underConstruction = true;
                if (primitive.hasKey("construction")) {
                    buildingType = primitive.get("construction");
                }
            }
            else {
                buildingType = value;
            }
            return true;
       }
       if ("3dshapes:ggmodelk".equals(key) ||
            ("source".equals(key) && "3dShapes".equals(value))) {
            source="3dshapes";
           return true;
       }
       if ("start_date".equals(key)) {
    	   startDate = value;
    	   return true;
       }
       if ("bag:bouwjaar".equals(key)) {
    	   startDate = value;
           primitive.put("start_date",  value);
           primitive.put(key,  null);
           return true;
       }
       if ("addr:housenumber".equals(key)) {
            InternalBagAddressNode address = new InternalBagAddressNode(primitive);
            address.build();
            getAddresses().add(address);
            hasAddress = true;
            return true;
        }
        if ("address:street".equals(key) ||
              "address:housename".equals(key) ||
              "address:city".equals(key) ||
              "address:postcode".equals(key)) {
              // Save other address keys in case address:housenumber is missing
              addressKeys.put(key, value);
        }
        return false;
    }

    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }

    @Override
    public Geometry getGeometry() {
        return geometry;
    }

    @Override
    public City getCity() {
        // TODO Auto-generated method stub
        return null;
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

    public String getBuildingType() {
        return buildingType;
    }

    @Override
    public boolean isIncomplete() {
        return primitive.isIncomplete();
    }

    @Override
    public String getSource() {
        return source;
    }

    public boolean isUnderConstruction() {
        return underConstruction;
    }

    @Override
    public String getStartDate() {
        return startDate;
    }
    
    public boolean hasAddress() {
        return hasAddress;
    }
    
    protected void buildGeometry() throws InvalidGeometryException {
        if (primitive.getDisplayType() == OsmPrimitiveType.CLOSEDWAY) {
            buildGeometry((Way)primitive);
        }
        else if (primitive.getDisplayType() == OsmPrimitiveType.MULTIPOLYGON) {
            buildGeometry((Relation)primitive);
        }
    }

    private void buildGeometry(Way way) throws IllegalArgumentException {
        GeoUtil geoUtil = GeoUtil.getInstance();
        geometry = geoUtil.toPolygon(way);
    }
    
    private void buildGeometry(Relation relation) throws InvalidMultiPolygonException {
        GeoUtil geoUtil = GeoUtil.getInstance();
        geometry = geoUtil.toMultiPolygon(relation);
    }
    
    @Override
    public Set<Building> getNeighbours() {
        return neighbours;
    }

    @Override
    public void addNeighbour(Building building) {
        neighbours.add(building);
    }

	@Override
	public boolean isDeleted() {
		return false;
	}
}
