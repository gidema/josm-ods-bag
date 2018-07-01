package org.openstreetmap.josm.plugins.ods.bag.osm.build;

import java.util.Map;

import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.plugins.ods.entities.OsmEntity;

public abstract class BagOsmEntityBuilder {

    /*
     * Earlier versions of the BAG import plug-in and some other small BAG
     * imports have introduced tags that are no longer used.
     * This method translates these tags into their current equivalent and
     * remove the old tags.
     */
    public static void normalizeTags(OsmPrimitive primitive) {
        String source = primitive.get("source");
        if (source != null && source.toUpperCase().startsWith("BAG")) {
            primitive.put("source", "BAG");
            if (source.length() == 11 && source.charAt(6) == '-') {
                try {
                    String month = source.substring(4, 6);
                    String year = source.substring(7, 11);
                    int m = Integer.parseInt(month);
                    int y = Integer.parseInt(year);
                    primitive.put("source:date", String.format("%1$4d-%2$02d", y, m));
                } catch (Exception e) {
                    // Something went wrong. Ignore the source date and print
                    // the stack trace
                    e.printStackTrace();
                }
            }
        }
        primitive.remove("bag:status");
        primitive.remove("bag:begindatum");
        normalizeReference(primitive, "ref:bag");
        normalizeReference(primitive, "ref:bagid");
        normalizeReference(primitive, "bag:id");
        normalizeReference(primitive, "bag:pand_id");
        normalizeReference(primitive, "ref:vbo_id");
        normalizeBagExtract(primitive);
    }

    private static void normalizeReference(OsmPrimitive primitive, String key) {
        String value = primitive.get(key);
        if (value == null) return;
        primitive.remove(key);
        if (value.length() == 0) return;
        try {
            Long reference = Long.parseLong(value);
            primitive.put("ref:bag", reference.toString());
        }
        catch (NumberFormatException e) {
            return;
        }
    }

    private static void normalizeBagExtract(OsmPrimitive primitive) {
        String value = primitive.get("bag:extract");
        if (value == null) return;
        primitive.remove("bag:extract");
        if (value.startsWith("9999PND") ||
                value.startsWith("9999LIG") || value.startsWith("9999STA")) {
            StringBuilder sb = new StringBuilder(10);
            sb.append(value.substring(11, 15)).append("-")
            .append(value.substring(9, 11)).append("-")
            .append(value.substring(7, 9));
            primitive.put("source:date", sb.toString());
        }
    }

    public static void parseKeys(OsmEntity entity, Map<String, String> tags) {
        entity.setSource(tags.remove("source"));
        String sourceDate = tags.remove("source:date");
        if (sourceDate != null) {
            entity.setSourceDate(sourceDate);
        }
    }
}
