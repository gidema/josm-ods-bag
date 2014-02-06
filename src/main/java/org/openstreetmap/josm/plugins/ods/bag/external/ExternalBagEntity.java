package org.openstreetmap.josm.plugins.ods.bag.external;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

import org.opengis.feature.simple.SimpleFeature;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.plugins.ods.PrimitiveBuilder;
import org.openstreetmap.josm.plugins.ods.entities.BuildException;
import org.openstreetmap.josm.plugins.ods.entities.external.ExternalEntity;
import org.openstreetmap.josm.plugins.ods.entities.external.FeatureUtil;
import org.openstreetmap.josm.plugins.ods.issue.ImportIssue;
import org.openstreetmap.josm.plugins.ods.issue.Issue;
import org.openstreetmap.josm.plugins.ods.metadata.MetaData;
import org.openstreetmap.josm.plugins.ods.metadata.MetaDataException;

import com.vividsolutions.jts.geom.Geometry;

public abstract class ExternalBagEntity implements ExternalEntity {
	private final static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	
	protected long identificatie;
	protected Date sourceDate;
	protected SimpleFeature feature;
	protected Collection<OsmPrimitive> primitives;
	protected Geometry geometry;
	
	public ExternalBagEntity(SimpleFeature feature) {
		this.feature = feature;
	}
	
	@Override
    public void init(MetaData metaData) throws BuildException {
		identificatie = FeatureUtil.getLong(feature, "identificatie");
		try {
			sourceDate = (Date) metaData.get("bag.source.date");
		} catch (MetaDataException e) {
			Issue issue = new ImportIssue(feature.getID(), e);
			throw new BuildException(issue);
		}
	}
	
    @Override
    public Long getId() {
        // Use identificatie instead of feature.getID() because the BAG
        // WFS may return several records per building which differ only
        // in 'gebruiksfunctie'
        return identificatie;
    }	
	
    @Override
    public boolean hasReferenceId() {
        return true;
    }

    @Override
    public Object getReferenceId() {
        return identificatie;
    }


	@Override
    public Geometry getGeometry() {
		return geometry;
	}

	public void setGeometry(Geometry geometry) {
		this.geometry = geometry;
	}

	@Override
	public void build() throws BuildException {
		// TODO Do we still need this method?
	}
	
	@Override
	public String getSource() {
		return "BAG";
	}

	@Override
	public boolean isInternal() {
		return false;
	}

    @Override
    public void createPrimitives(PrimitiveBuilder builder) {
        if (getPrimitives() == null && getGeometry() != null) {
            primitives = builder.build(getGeometry());
            for (OsmPrimitive primitive : primitives) {
                buildTags(primitive);
            }
        }
    }

	@Override
	public Collection<OsmPrimitive> getPrimitives() {
		return primitives;
	}

	@Override
    public void buildTags(OsmPrimitive primitive) {
		primitive.put("source", getSource());
		primitive.put("source:date", dateFormat.format(sourceDate));
	}
}
