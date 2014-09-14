package org.openstreetmap.josm.plugins.ods.bag.gt.build;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.openstreetmap.josm.plugins.ods.bag.entity.BagAddressNode;
import org.openstreetmap.josm.plugins.ods.bag.gt.build.BagBuildingTypeAnalyzer.Statistics.Stat;
import org.openstreetmap.josm.plugins.ods.entities.builtenvironment.AddressNode;
import org.openstreetmap.josm.plugins.ods.entities.builtenvironment.Building;
import org.openstreetmap.josm.plugins.ods.entities.builtenvironment.BuildingType;
import org.openstreetmap.josm.plugins.ods.entities.builtenvironment.GtBuildingStore;
import org.openstreetmap.josm.plugins.ods.tasks.Task;

public class BagBuildingTypeAnalyzer implements Task {
    private final static List<String> trafo =
            Arrays.asList("TRAF","TRAN","TRFO","TRNS");
    private final static List<String> garage =
            Arrays.asList("GAR","GRG");
    
    private final GtBuildingStore buildingStore;
    
    public BagBuildingTypeAnalyzer(GtBuildingStore buildingStore) {
        super();
        this.buildingStore = buildingStore;
    }

    @Override
    public void run() {
        for (Building building : buildingStore) {
            analyzeBuildingType(building);
        }
        
    }

    public void analyzeBuildingType(Building building) {
        if (BuildingType.HOUSEBOAT.equals(building.getBuildingType()) ||
                BuildingType.STATIC_CARAVAN.equals(building.getBuildingType())) {
            return;
        }
        BuildingType type = BuildingType.UNCLASSIFIED;
        if (building.getAddressNodes().size() == 1) {
            type = getBuildingType((BagAddressNode)building.getAddressNodes().get(0));
        }
        else {
            type = getBuildingType(building.getAddressNodes());
        }
        building.setBuildingType(type);
    }

    private BuildingType getBuildingType(List<AddressNode> addresses) {
        Statistics stats = new Statistics();
        Iterator<AddressNode> it = addresses.iterator();
        while (it.hasNext()) {
            BagAddressNode addressNode = (BagAddressNode) it.next();
            BuildingType type = getBuildingType(addressNode);
            stats.add(type, addressNode.getArea());
        }
        Stat largest = stats.getLargest();
        BuildingType type = null;
        if (largest.percentage > 0.75) {
            switch (largest.type) {
            case HOUSE:
                type = BuildingType.APARTMENTS;
                break;
            case PRISON:
            case RETAIL:
            case OFFICE:
                type = largest.type;
                break;
            default:
                type = BuildingType.UNCLASSIFIED;
            }
        }
        return type;
    }

    private static BuildingType getBuildingType(BagAddressNode addressNode) {
        String extra = addressNode.getHuisNummerToevoeging();
        if (extra != null) {
            extra = extra.toUpperCase();
            if (trafo.contains(extra)) {
                return BuildingType.SUBSTATION;
            }
            else if (garage.contains(extra)) {
                return BuildingType.GARAGE;
            }
        }
        switch (addressNode.getGebruiksdoel().toLowerCase()) {
        case "woonfunctie":
            return BuildingType.HOUSE;
        case "overige gebruiksfunctie":
            return BuildingType.UNCLASSIFIED;
        case "industriefunctie":
            return BuildingType.INDUSTRIAL;
        case "winkelfunctie":
            return BuildingType.RETAIL;
        case "kantoorfunctie":
            return BuildingType.OFFICE;
        case "celfunctie":
            return BuildingType.PRISON;
        default: 
            return BuildingType.UNCLASSIFIED;
        }
    }

    class Statistics {
        private Map<BuildingType, Row> rows = new HashMap<>();
        private double totalArea = 0.0;
        
        public void add(BuildingType type, double area) {
            Row row = rows.get(type);
            if (row == null) {
                row = new Row();
                rows.put(type,  row);
            }
            row.add(area);
            totalArea += area;
        }
        
        public Stat getLargest() {
            Stat stat = new Stat();
            for (Entry<BuildingType, Row> entry : rows.entrySet()) {
                Row row = entry.getValue();
                if (row.area > stat.area) {
                    stat.type = entry.getKey();
                    stat.area = row.area;
                    stat.count = row.count; 
                }
            }
            stat.percentage = stat.area/totalArea;
            return stat;
        }
        
        class Stat {
            BuildingType type;
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
