package org.openstreetmap.josm.plugins.ods.bag.internal;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.plugins.ods.crs.InvalidGeometryException;
import org.openstreetmap.josm.plugins.ods.entities.BuildException;
import org.openstreetmap.josm.plugins.ods.entities.internal.InternalEntity;
import org.openstreetmap.josm.plugins.ods.issue.Issue;
import org.openstreetmap.josm.plugins.ods.issue.JosmIssue;

import com.vividsolutions.jts.geom.Geometry;

public abstract class InternalBagEntity implements InternalEntity {
	protected OsmPrimitive primitive;
	protected Geometry geometry;
    protected String source;
    protected String sourceDate;
    protected Long referenceId;
    private Map<String, String> otherKeys;
    
	public InternalBagEntity(OsmPrimitive primitive) {
		super();
		this.primitive = primitive;
	}

	@Override
    @SuppressWarnings("unchecked")
    public Long getId() {
	    return primitive.getId();
	}
	
    @Override
    public Long getReferenceId() {
        return referenceId;
    }

    @Override
	public String getSource() {
		return source;
	}
	
	public String getSourceDate() {
		return sourceDate;
	}

	@Override
	public boolean isInternal() {
		return true;
	}

	@Override
    public boolean isDeleted() {
        return false;
    }

    @Override
	public Geometry getGeometry() {
		return geometry;
	}

	@Override
    public boolean hasName() {
        return false;
    }

    @Override
	public String getName() {
		return null;
	}

	@Override
	public Collection<OsmPrimitive> getPrimitives() {
		return Collections.singleton(primitive);
	}
	
    @Override
	public OsmPrimitive getPrimitive() {
		return primitive;
	}

	@Override
    public void build() throws BuildException {
        try {
            buildGeometry();
            parseKeys();
        } catch (InvalidGeometryException e) {
            Issue issue = new JosmIssue(primitive, e);
            throw new BuildException(issue);
        }
    }
    
    protected abstract void buildGeometry() throws InvalidGeometryException;
    
    
    @Override
	public Map<String, String> getOtherKeys() {
		return otherKeys;
	}

	protected void parseKeys() {
		Map<String, String> keys = primitive.getKeys();
		Iterator<Entry<String, String>> it = 
		    keys.entrySet().iterator();
		while (it.hasNext()) {
			Entry<String, String> entry =it.next();
			String key = entry.getKey();
			String value = entry.getValue();
			if (parseKey(key, value)) {
				it.remove();
			}
		}
		otherKeys = keys;
	}

	protected boolean parseKey(String key, String value) {
        if ("source".equals(key)
                && value.toUpperCase().startsWith("BAG")) {
            source = "BAG";
            if (value.length() == 11 && value.charAt(6) == '-') {
                try {
                    String month = value.substring(4, 6);
                    String year = value.substring(7, 11);
                    int m = Integer.parseInt(month);
                    int y = Integer.parseInt(year);
                    sourceDate = String.format("%1$4d-%2$02d", y, m);
                }
                catch (Exception e) {
                    // Something went wrong. Ignore the source date and print the stack trace
                    e.printStackTrace();
                }
            }
            return true;
        }
        if ("source:date".equals(key)) {
            sourceDate = value;
            return true;
        }
        if ("ref:bagid".equals(key) || "bag:id".equals(key) ||
                "ref:bag".equals(key) || "bag:pand_id".equals(key) ||
                "ref:vbo_id".equals(key)) {
            referenceId = parseReference(value);
            return true;
        }
        if ("bag:extract".equals(key)) {
            sourceDate = parseBagExtract(value);
            return true;
        }
        return false;
	}
	
    private static String parseBagExtract(String s) {
        if (s.startsWith("9999PND") || s.startsWith("9999LIG") || s.startsWith("9999STA")) {
            StringBuilder sb = new StringBuilder(10);
            sb.append(s.substring(11,15)).append("-").append(s.substring(9,11)).append("-").append(s.substring(7, 9));
            return sb.toString();
        }
        return s;
    }

    private static Long parseReference(String value) {
        if (value.length() == 0) return null;
        int i=0;
        while (i<value.length() && Character.isDigit(value.charAt(i))) {
            i++;
        }
        if (i == 0) return null;
        return new Long(value.substring(0, i));
    }
 }
