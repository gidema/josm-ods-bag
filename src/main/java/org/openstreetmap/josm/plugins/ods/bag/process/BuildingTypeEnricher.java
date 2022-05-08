package org.openstreetmap.josm.plugins.ods.bag.process;

import static org.openstreetmap.josm.plugins.ods.bag.entity.BAGBuildingType.APARTMENTS;
import static org.openstreetmap.josm.plugins.ods.bag.entity.BAGBuildingType.GARAGE;
import static org.openstreetmap.josm.plugins.ods.bag.entity.BAGBuildingType.HOUSE;
import static org.openstreetmap.josm.plugins.ods.bag.entity.BAGBuildingType.INDUSTRIAL;
import static org.openstreetmap.josm.plugins.ods.bag.entity.BAGBuildingType.OFFICE;
import static org.openstreetmap.josm.plugins.ods.bag.entity.BAGBuildingType.PRISON;
import static org.openstreetmap.josm.plugins.ods.bag.entity.BAGBuildingType.RETAIL;
import static org.openstreetmap.josm.plugins.ods.bag.entity.BAGBuildingType.SUBSTATION;
import static org.openstreetmap.josm.plugins.ods.bag.entity.BAGBuildingType.UNCLASSIFIED;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.openstreetmap.josm.plugins.ods.bag.entity.BAGBuildingType;
import org.openstreetmap.josm.plugins.ods.bag.entity.BagBuildingUnit;
import org.openstreetmap.josm.plugins.ods.bag.entity.NLAddress;
import org.openstreetmap.josm.plugins.ods.bag.entity.storage.BagBuildingStore;
import org.openstreetmap.josm.plugins.ods.bag.process.BuildingTypeEnricher.Statistics.Stat;
import org.openstreetmap.josm.plugins.ods.context.OdsContext;
import org.openstreetmap.josm.plugins.ods.context.OdsContextJob;

public class BuildingTypeEnricher implements OdsContextJob {
    private final static List<String> trafo =
            Arrays.asList("TRAF","TRAN","TRFO","TRNS");
    private final static List<String> garage =
            Arrays.asList("GAR","GRG");

    public BuildingTypeEnricher() {
        super();
    }

    @Override
    public void run(OdsContext context) {
        BagBuildingStore buildingStore = context.getComponent(BagBuildingStore.class);
        buildingStore.forEach(building -> {
            if (BAGBuildingType.HOUSEBOAT.equals(building.getBuildingType()) ||
                BAGBuildingType.STATIC_CARAVAN.equals(building.getBuildingType())) {
                return;
            }
            BAGBuildingType type = BAGBuildingType.UNCLASSIFIED;
            if (building.getBuildingUnits().size() == 1) {
                type = getBuildingType(building.getBuildingUnits().values().iterator().next());
            }
            else {
                type = getBuildingType(building.getBuildingUnits().values());
            }
            building.setBuildingType(type);
        });
    }

    private BAGBuildingType getBuildingType(Collection<BagBuildingUnit> buildingUnits) {
        Statistics stats = new Statistics();
        Iterator<BagBuildingUnit> it = buildingUnits.iterator();
        while (it.hasNext()) {
            BagBuildingUnit buildingUnit = it.next();
            BAGBuildingType type = getBuildingType(buildingUnit);
            stats.add(type, buildingUnit.getArea());
        }
        Stat largest = stats.getLargest();
        BAGBuildingType type = BAGBuildingType.UNCLASSIFIED;
        if (largest.percentage > 0.75) {
            switch (largest.type) {
            case HOUSE:
                type = APARTMENTS;
                break;
            case PRISON:
            case RETAIL:
            case OFFICE:
                type = largest.type;
                break;
            default:
                type = UNCLASSIFIED;
            }
        }
        return type;
    }

    private static BAGBuildingType getBuildingType(BagBuildingUnit buildingUnit) {
        NLAddress mainAddress = buildingUnit.getMainAddressNode().getAddress();
        String extra = mainAddress.getHouseNumber().getHouseNumberExtra();
        if (extra != null) {
            extra = extra.toUpperCase();
            if (trafo.contains(extra)) {
                return SUBSTATION;
            }
            else if (garage.contains(extra)) {
                return GARAGE;
            }
        }
        switch (buildingUnit.getGebruiksdoel().toLowerCase()) {
        case "woonfunctie":
            return HOUSE;
        case "overige gebruiksfunctie":
            return UNCLASSIFIED;
        case "industriefunctie":
            return INDUSTRIAL;
        case "winkelfunctie":
            return RETAIL;
        case "kantoorfunctie":
            return OFFICE;
        case "celfunctie":
            return PRISON;
        default:
            return UNCLASSIFIED;
        }
    }

    class Statistics {
        private final Map<BAGBuildingType, Row> rows = new HashMap<>();
        private double totalArea = 0.0;

        public void add(BAGBuildingType type, double area) {
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
            for (Entry<BAGBuildingType, Row> entry : rows.entrySet()) {
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
            BAGBuildingType type;
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
