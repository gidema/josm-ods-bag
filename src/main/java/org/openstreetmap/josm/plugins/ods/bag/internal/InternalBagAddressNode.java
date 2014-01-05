package org.openstreetmap.josm.plugins.ods.bag.internal;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map.Entry;

import org.openstreetmap.josm.data.osm.Node;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.data.osm.OsmPrimitiveType;
import org.openstreetmap.josm.plugins.ods.entities.Entity;
import org.openstreetmap.josm.plugins.ods.entities.builtenvironment.Address;
import org.openstreetmap.josm.plugins.ods.entities.builtenvironment.AddressNode;
import org.openstreetmap.josm.plugins.ods.entities.builtenvironment.Block;
import org.openstreetmap.josm.plugins.ods.entities.builtenvironment.Building;
import org.openstreetmap.josm.plugins.ods.entities.internal.InternalAddress;
import org.openstreetmap.josm.plugins.ods.jts.GeoUtil;

import com.vividsolutions.jts.geom.Point;

public class InternalBagAddressNode implements AddressNode {
    private OsmPrimitive primitive;
    private Building building;
	private Address address;
    private String source = null;
    private String sourceDate;
    private GeoUtil geoUtil = GeoUtil.getInstance();

    private Point geometry;
    
    public InternalBagAddressNode(OsmPrimitive primitive) {
    	this.primitive = primitive;
        address = new InternalAddress(primitive);
    }
    
    @Override
    public Address getAddress() {
        return address;
    }

    @Override
    public Class<? extends Entity> getType() {
        return AddressNode.class;
    }

    @Override
	public boolean isInternal() {
		return true;
	}

	@Override
	public boolean isIncomplete() {
		return primitive.isIncomplete();
	}

	@Override
	public boolean isDeleted() {
		return false;
	}

	@Override
	public Object getId() {
		return primitive.getId();
	}

	@Override
	public Collection<OsmPrimitive> getPrimitives() {
		return Collections.singletonList(primitive);
	}

	public void build() {
		if (primitive.getType() == OsmPrimitiveType.NODE) {
            geometry = geoUtil.toPoint((Node)primitive);
		}

        Iterator<Entry<String, String>> it =
            primitive.getKeys().entrySet().iterator();
        while (it.hasNext()) {
            Entry<String, String> entry = it.next();
            String key = entry.getKey();
            String value = entry.getValue();
            if ("addr:housenumber".equals(key) ||
                 "addr:street".equals(key) ||
                 "addr:housename".equals(key) ||
                 "addr:city".equals(key) ||
                 "addr:postcode".equals(key)) {
                // Ignore address related keys
                continue;
            }
            // TODO improve this (also move to Bag specific)
            else if ("source".equals(key) && key.toUpperCase().startsWith("BAG")) {
                source = "BAG";
            }
            else if ("bag:extract".equals(key)) {
                sourceDate = parseBagExtract(value);
            }
        }
    }
    
    @Override
    public String getName() {
        return null;
    }

    @Override
    public Block getBlock() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Object getBuildingRef() {
        if (building != null) {
        	return building.getId();
        }
        return null;
    }

    
    @Override
	public void setBuilding(Building building) {
		this.building = building;
	}

	@Override
    public Building getBuilding() {
        return building;
    }

    @Override
    public Point getGeometry() {
    	return geometry;
    }

    public String getSource() {
        return source;
    }
    
    public String getSourceDate() {
        return sourceDate;
    }

    private String parseBagExtract(String s) {
        if (s.startsWith("9999PND") || s.startsWith("9999LIG") || s.startsWith("9999STA")) {
            StringBuilder sb = new StringBuilder(10);
            sb.append(s.substring(11,15)).append("-").append(s.substring(9,11)).append("-").append(s.substring(7, 9));
            return sb.toString();
        }
        return s;
    }
}
