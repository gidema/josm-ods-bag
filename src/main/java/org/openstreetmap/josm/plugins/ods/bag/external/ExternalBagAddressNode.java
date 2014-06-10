package org.openstreetmap.josm.plugins.ods.bag.external;

//import java.io.Serializable;
import java.util.Date;

import org.opengis.feature.simple.SimpleFeature;
import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.plugins.ods.crs.CRSException;
import org.openstreetmap.josm.plugins.ods.crs.CRSUtil;
import org.openstreetmap.josm.plugins.ods.entities.BuildException;
import org.openstreetmap.josm.plugins.ods.entities.Entity;
import org.openstreetmap.josm.plugins.ods.entities.builtenvironment.AddressNode;
import org.openstreetmap.josm.plugins.ods.entities.builtenvironment.Block;
import org.openstreetmap.josm.plugins.ods.entities.builtenvironment.Building;
import org.openstreetmap.josm.plugins.ods.issue.ImportIssue;
import org.openstreetmap.josm.plugins.ods.issue.Issue;
import org.openstreetmap.josm.plugins.ods.metadata.MetaData;

import com.vividsolutions.jts.geom.Point;

public class ExternalBagAddressNode extends ExternalBagEntity implements
        AddressNode {
    private ExternalBagAddress address;
    private Building building;
    private Block block;
    private String status;
    private String gebruiksdoel;
    private double oppervlakte;
    private Long gerelateerdPand;

    public ExternalBagAddressNode(SimpleFeature feature) {
        super(feature);
    }

    @Override
    public Class<? extends Entity> getType() {
        return AddressNode.class;
    }

    public void init(MetaData metaData) throws BuildException {
        super.init(metaData);
        address = new ExternalBagAddress(feature);
        address.init(metaData);
        status = (String) feature.getProperty("status").getValue();
        gebruiksdoel = (String) feature.getProperty("gebruiksdoel").getValue();
        oppervlakte = (Double) feature.getProperty("oppervlakte").getValue();
        gerelateerdPand = ((Double) feature.getProperty("pandidentificatie")
                .getValue()).longValue();
        try {
            geometry = (Point) CRSUtil.getInstance().transform(feature);
        } catch (CRSException e) {
            // TODO Auto-generated catch block
            Issue issue = new ImportIssue(feature.getID(), e);
            throw new BuildException(issue);
        }
    }

    @Override
    public ExternalBagAddress getAddress() {
        return address;
    }

    @Override
    public Block getBlock() {
        return block;
    }

    @Override
    public void setBuilding(Building building) {
        this.building = building;
    }

    @Override
    public Building getBuilding() {
        return building;
    }

    public String getStatus() {
        return status;
    }

    public Double getArea() {
        return oppervlakte;
    }

    @Override
    public boolean isIncomplete() {
        if (building != null) {
            return building.isIncomplete();
        }
        Main.warn("Address without a building: {0}", getId());
        return false;
    }

    @Override
    public boolean isDeleted() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public String getName() {
        return null;
    }

    public String getGebruiksdoel() {
        return gebruiksdoel;
    }

    public Object getBuildingRef() {
        return gerelateerdPand;
    }

    public Date getBagExtract() {
        return sourceDate;
    }

    public Point getGeometry() {
        return (Point) geometry;
    }

    @Override
    public void buildTags(OsmPrimitive primitive) {
        super.buildTags(primitive);
        ((ExternalBagAddress) address).buildTags(primitive);
    }
}
