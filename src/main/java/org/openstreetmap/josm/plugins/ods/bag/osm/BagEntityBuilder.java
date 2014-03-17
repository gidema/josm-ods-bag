package org.openstreetmap.josm.plugins.ods.bag.osm;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.plugins.ods.bag.BagEntity;
import org.openstreetmap.josm.plugins.ods.crs.InvalidGeometryException;
import org.openstreetmap.josm.plugins.ods.entities.EntityBuilder;
import org.openstreetmap.josm.plugins.ods.metadata.MetaData;

import com.vividsolutions.jts.geom.Geometry;

public abstract class BagEntityBuilder<T extends BagEntity> implements EntityBuilder<OsmPrimitive, T> {
    @Override
    public T build(OsmPrimitive primitive, MetaData metaData) {
        T entity = createEntity();
        parseData(entity, primitive);
        return entity;
    }

    private void parseData(T entity, OsmPrimitive primitive) {
        try {
            entity.setGeometry(buildGeometry(primitive));
            parseKeys(entity, primitive);
        } catch (InvalidGeometryException e) {
            throw new RuntimeException(e);
        }
    }

    protected abstract T createEntity();
    protected abstract Geometry buildGeometry(OsmPrimitive primitive) throws InvalidGeometryException;

    protected Map<String, String> parseKeys(T entity, OsmPrimitive primitive) {
        Map<String, String> keys = new TreeMap<>();
        keys.putAll(primitive.getKeys());
        Iterator<Entry<String, String>> it = 
            keys.entrySet().iterator();
        while (it.hasNext()) {
            Entry<String, String> entry =it.next();
            String key = entry.getKey();
            String value = entry.getValue();
            if (parseKey(entity, key, value)) {
                it.remove();
            }
        }
        return keys;
    }

    protected boolean parseKey(T entity, String key, String value) {
        if ("source".equals(key)
                && value.toUpperCase().startsWith("BAG")) {
            entity.setSource("BAG");
            if (value.length() == 11 && value.charAt(6) == '-') {
                try {
                    String month = value.substring(4, 6);
                    String year = value.substring(7, 11);
                    int m = Integer.parseInt(month);
                    int y = Integer.parseInt(year);
                    entity.setSourceDate(String.format("%1$4d-%2$02d", y, m));
                }
                catch (Exception e) {
                    // Something went wrong. Ignore the source date and print the stack trace
                    e.printStackTrace();
                }
            }
            return true;
        }
        if ("source:date".equals(key)) {
            entity.setSourceDate(value);
            return true;
        }
        if ("ref:bagid".equals(key) || "bag:id".equals(key) ||
                "ref:bag".equals(key) || "bag:pand_id".equals(key) ||
                "ref:vbo_id".equals(key)) {
            entity.setIdentificatie(parseReference(value));
            return true;
        }
        if ("bag:extract".equals(key)) {
            entity.setSourceDate(parseBagExtract(value));
            return true;
        }
        return false;
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
    
    private static String parseBagExtract(String s) {
        if (s.startsWith("9999PND") || s.startsWith("9999LIG") || s.startsWith("9999STA")) {
            StringBuilder sb = new StringBuilder(10);
            sb.append(s.substring(11,15)).append("-").append(s.substring(9,11)).append("-").append(s.substring(7, 9));
            return sb.toString();
        }
        return s;
    }
}
