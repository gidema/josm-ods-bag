package org.openstreetmap.josm.plugins.ods.bag.gt.parsing;

import java.util.Arrays;
import java.util.List;

import org.opengis.feature.simple.SimpleFeature;
import org.openstreetmap.josm.plugins.ods.bag.entity.BagOdAddressNode;
import org.openstreetmap.josm.plugins.ods.bag.entity.BagOdBuildingUnit;
import org.openstreetmap.josm.plugins.ods.bag.relations.BuildingToBuildingUnitRelation;
import org.openstreetmap.josm.plugins.ods.domains.buildings.BuildingType;
import org.openstreetmap.josm.plugins.ods.domains.buildings.BuildingUnitStatus;
import org.openstreetmap.josm.plugins.ods.domains.buildings.OdAddress;
import org.openstreetmap.josm.plugins.ods.domains.buildings.OdAddressNode;
import org.openstreetmap.josm.plugins.ods.domains.buildings.OdBuildingUnit;
import org.openstreetmap.josm.plugins.ods.domains.buildings.impl.OdAddressNodeStore;
import org.openstreetmap.josm.plugins.ods.domains.buildings.impl.OdBuildingUnitStore;
import org.openstreetmap.josm.plugins.ods.entities.opendata.FeatureUtil;
import org.openstreetmap.josm.plugins.ods.io.DownloadResponse;
import org.openstreetmap.josm.tools.Logging;

import com.vividsolutions.jts.geom.Geometry;

public class BagPdokVerblijfsobjectParser extends BagFeatureParser {
    private final static List<String> trafo =
            Arrays.asList("TRAF","TRAN","TRFO","TRNS");
    private final static List<String> garage =
            Arrays.asList("GAR","GRG");

    private final OdBuildingUnitStore buildingUnitStore;
    private final OdAddressNodeStore addressNodeStore;
    private final BuildingToBuildingUnitRelation buildingToBuildingUnitRelation;

    public BagPdokVerblijfsobjectParser(
            OdBuildingUnitStore buildingUnitStore,
            OdAddressNodeStore addressNodeStore,
            BuildingToBuildingUnitRelation buildingToBuildingUnitRelation) {
        super();
        this.buildingUnitStore = buildingUnitStore;
        this.addressNodeStore = addressNodeStore;
        this.buildingToBuildingUnitRelation = buildingToBuildingUnitRelation;
    }

    /**
     * Parse the Verblijfsobject feature.
     * The verblijfsobject as provided by the PDOK WFS server is a composed object of 3
     * feature types:
     * - A building unit
     * - The building unit's main address
     * - One related building
     *
     * A verblijfsobject can be located in more than one related building. If this is the case, the
     * WFS server will return duplicate verblijfsobjects with different values in the 'related building'
     * field. To deal with this, each unique verblijfsobject is stored only once as a BuildingUnit,
     * without the building reference. A separate registry is kept for the BuildingUnit to Building
     * relations.
     * After all buildings and BuildingUnits have been retrieved, the Buildings and BuildingUnits
     * will be updated with there mutual reference.
     *
     * @param feature
     * @param response
     */
    @Override
    public void parse(SimpleFeature feature, DownloadResponse response) {
        BagOdBuildingUnit buildingUnit = parseBuildingUnit(feature, response);
        if (buildingUnitStore.add(buildingUnit)) {
            OdAddressNode addressNode = parseAddressNode(feature, buildingUnit, response);
            addressNodeStore.add(addressNode);
            buildingUnit.setMainAddressNode(addressNode);
        }
        BuildingToBuildingUnitRelation.Tuple pair = parseBuilding_BuildingUnitPair(feature);
        buildingToBuildingUnitRelation.add(pair);
    }

    public BagOdBuildingUnit parseBuildingUnit(SimpleFeature feature, DownloadResponse response) {
        BagOdBuildingUnit buildingUnit = new BagOdBuildingUnit();
        super.parse(feature, buildingUnit, response);
        buildingUnit.setBuildingUnitId(parseId(feature));
        String status = FeatureUtil.getString(feature, "status");
        buildingUnit.setStatus(parseStatus(status));
        Double area = FeatureUtil.getBigDecimal(feature, "oppervlakte").doubleValue();
        buildingUnit.setArea(area);
        BuildingType buildingType = parseBuildingType(feature);
        buildingUnit.setBuildingType(buildingType);
        return buildingUnit;
    }

    private OdAddressNode parseAddressNode(SimpleFeature feature, OdBuildingUnit buildingUnit, DownloadResponse response) {
        BagOdAddressNode addressNode = new BagOdAddressNode();
        super.parse(feature, addressNode, response);
        // The PDOK WFS service doesn't provide the address id for the main address.
        // We use the buildingUnitId instead to get a unique Id;
        addressNode.setPrimaryId(buildingUnit.getBuildingUnitId());
        OdAddress address = parseAddress(feature);
        addressNode.setAddress(address);
        addressNode.setGeometry(buildingUnit.getGeometry());
        addressNode.setBuildinUnit(buildingUnit);
        return addressNode;
    }

    private static BuildingToBuildingUnitRelation.Tuple parseBuilding_BuildingUnitPair(SimpleFeature feature) {
        Long buildingUnitId = parseId(feature);
        Long buildingId = FeatureUtil.getLong(feature, "pandidentificatie");
        return new BuildingToBuildingUnitRelation.Tuple(buildingId, buildingUnitId);
    }

    private static Long parseId(SimpleFeature feature) {
        return FeatureUtil.getLong(feature, "identificatie");
    }

    @Override
    protected Geometry getGeometry(SimpleFeature feature) {
        return (Geometry) feature.getAttribute("geometrie");
    }

    private static BuildingUnitStatus parseStatus(String status) {
        switch (status) {
        case "Verblijfsobject gevormd":
            return BuildingUnitStatus.PROJECTED;
        case "Verblijfsobject in gebruik":
            return BuildingUnitStatus.FUNCTIONAL;
        case "Verblijfsobject buiten gebruik":
            return BuildingUnitStatus.DECLINED;
        case "Verblijfsobject in gebruik (niet ingemeten)":
            return BuildingUnitStatus.IN_USE_NOT_MEASURED;
        case "Verblijfsobject ingetrokken":
            return BuildingUnitStatus.NOT_ESTABLISHED;
        case "Niet gerealiseerd verblijfsobject":
        case "Verblijfsobject ten onrechte opgevoerd":
            return BuildingUnitStatus.WITHDRAW;
        case "Verbouwing verblijfsobject":
            return BuildingUnitStatus.UNDER_RECONSTRUCTION;
        default:
            Logging.warn("Unknown status voor Verblijfsobject: {0}", status);
            return BuildingUnitStatus.UNKNOWN;
        }
    }

    private static BuildingType parseBuildingType(SimpleFeature feature) {
        String extra = FeatureUtil.getString(feature, "toevoeging");
        if (extra != null) {
            extra = extra.toUpperCase();
            if (trafo.contains(extra)) {
                return BuildingType.SUBSTATION;
            }
            else if (garage.contains(extra)) {
                return BuildingType.GARAGE;
            }
        }
        String gebruiksdoel = FeatureUtil.getString(feature, "gebruiksdoel");
        switch (gebruiksdoel.toLowerCase()) {
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
}
