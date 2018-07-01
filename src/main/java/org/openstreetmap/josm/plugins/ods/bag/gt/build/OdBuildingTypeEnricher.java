package org.openstreetmap.josm.plugins.ods.bag.gt.build;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.openstreetmap.josm.plugins.ods.bag.entity.BagOdAddressNode;
import org.openstreetmap.josm.plugins.ods.bag.gt.build.OdBuildingTypeEnricher.Statistics.Stat;
import org.openstreetmap.josm.plugins.ods.domains.buildings.BuildingType;
import org.openstreetmap.josm.plugins.ods.domains.buildings.OdAddressNode;
import org.openstreetmap.josm.plugins.ods.domains.buildings.OdBuilding;
import org.openstreetmap.josm.plugins.ods.domains.buildings.impl.OdBuildingStore;

public class OdBuildingTypeEnricher {
    private final static List<String> trafo =
            Arrays.asList("TRAF","TRAN","TRFO","TRNS");
    private final static List<String> garage =
            Arrays.asList("GAR","GRG");
    private final OdBuildingStore odBuildingStore;

    public OdBuildingTypeEnricher(OdBuildingStore odBuildingStore) {
        super();
        this.odBuildingStore = odBuildingStore;
    }

    public void run() {
        odBuildingStore.forEach(this::update);
    }

    public void update(OdBuilding building) {
        if (BuildingType.HOUSEBOAT.equals(building.getBuildingType()) ||
                BuildingType.STATIC_CARAVAN.equals(building.getBuildingType())) {
            return;
        }
        BuildingType type = BuildingType.UNCLASSIFIED;
        if (building.getAddressNodes().size() == 1) {
            type = getBuildingType((BagOdAddressNode)building.getAddressNodes().get(0));
        }
        else {
            type = getBuildingType(building.getAddressNodes());
        }
        building.setBuildingType(type);
    }

    private BuildingType getBuildingType(List<OdAddressNode> addresses) {
        Statistics stats = new Statistics();
        Iterator<OdAddressNode> it = addresses.iterator();
        while (it.hasNext()) {
            BagOdAddressNode addressNode = (BagOdAddressNode) it.next();
            BuildingType type = getBuildingType(addressNode);
            stats.add(type, addressNode.getArea());
        }
        Stat largest = stats.getLargest();
        BuildingType type = BuildingType.UNCLASSIFIED;
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

    private static BuildingType getBuildingType(BagOdAddressNode addressNode) {
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
        private final Map<BuildingType, Row> rows = new HashMap<>();
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
