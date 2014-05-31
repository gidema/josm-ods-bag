package org.openstreetmap.josm.plugins.ods.bag.osm;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.openstreetmap.josm.data.osm.DataSet;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.plugins.ods.bag.BagAddress;
import org.openstreetmap.josm.plugins.ods.bag.BagBuilding;
import org.openstreetmap.josm.plugins.ods.bag.osm.BagBuildingPrimitiveBuilder.Statistics.Stat;
import org.openstreetmap.josm.plugins.ods.builtenvironment.AddressNode;

public class BagBuildingPrimitiveBuilder extends BagPrimitiveBuilder<BagBuilding> {
    private final static List<String> trafo =
            Arrays.asList("TRAF","TRAN","TRFO","TRNS");
        private final static List<String> garage =
                Arrays.asList("GAR","GRG");

    public BagBuildingPrimitiveBuilder(DataSet targetDataSet) {
        super(targetDataSet);
    }

    @Override
    public void buildTags(BagBuilding building, OsmPrimitive primitive) {
        super.buildTags(building, primitive);
        primitive.put("start_date", building.getStartDate());
        primitive.put("ref:bag", building.getReferenceId().toString());
        String buildingType = building.getBuildingType();
        if ("yes".equals(buildingType)) {
            buildingType = analyzeBuildingType(building, primitive);
        }
        if (building.isUnderConstruction()) {
            primitive.put("building", "construction");
            primitive.put("construction", buildingType);                
        }
        else {
            primitive.put("building", buildingType);
        }
        BagAddress address = building.getAddress();
        if (address != null) {
            BagAddressPrimitiveBuilder.buildTags(address, primitive);
        }
    }
    
    private String analyzeBuildingType(BagBuilding building, OsmPrimitive primitive) {
        Set<AddressNode> addressNodes = building.getAddressNodes();
        if (addressNodes.size() == 1) {
            return getBuildingType(addressNodes.iterator().next(), primitive);
        }
        return getBuildingType(addressNodes, primitive);
    }

    private static String getBuildingType(AddressNode address, OsmPrimitive primitive) {
        String type = "yes";
        String gebruiksDoel = address.getOtherTags().get("bag:gebruiksdoel");
        if (gebruiksDoel != null) {
            switch (gebruiksDoel.toLowerCase()) {
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
        }
        String extra = address.getOtherTags().get("huisnummertoevoeging");
        if (extra != null) {
            extra = extra.toUpperCase();
            if (trafo.contains(extra)) {
                primitive.put("power", "substation");
            }
            else if (garage.contains(extra)) {
                type = "garage";
            }
        }
        return type;
    }

    private String getBuildingType(Set<AddressNode> addresses, OsmPrimitive primitive) {
        Statistics stats = new Statistics();
        Iterator<AddressNode> it = addresses.iterator();
        while (it.hasNext()) {
            AddressNode address = it.next();
            String function = address.getOtherTags().get("bag:gebruiksdoel");
            String sArea = address.getOtherTags().get("bag:oppervlakte");
            if (function != null && sArea != null) {
                stats.add(function, Double.parseDouble(sArea));
            }
        }
        Stat largest = stats.getLargest();
        String type = "yes";
        if (largest.percentage > 0.75) {
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
        }
        return type;
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
            
            public void add(double a) {
                this.area += a;
                this.count++;
            }
        }
    }
}
