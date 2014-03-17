package org.openstreetmap.josm.plugins.ods.bag;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.plugins.ods.entities.BuildException;
import org.openstreetmap.josm.plugins.ods.entities.Entity;

import com.vividsolutions.jts.geom.Geometry;

public abstract class BagEntity implements Entity {
    private boolean incomplete;
    private boolean deleted;
    private boolean hasReferenceId;
    private boolean hasName;
    private boolean internal;
    private String sourceDate;
    private String name;
    private String source;
    private Long identificatie;
    private Geometry geometry;
    private Collection<OsmPrimitive> primitives;
    private Map<String, String> otherTags = new HashMap<>();

    @Override
    public void build() throws BuildException {
        throw new UnsupportedOperationException("Deprecated");
    }

    public void setInternal(boolean internal) {
        this.internal = internal;
    }
    
    public void setIdentificatie(Long identificatie) {
        this.identificatie = identificatie;    
    }
    
    public String getSourceDate() {
        return sourceDate;
    }

    public void setSourceDate(String string) {
        this.sourceDate = string;
    }

    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }
    
    public void setSource(String source) {
        this.source = source;
    }
    
    @Override
    public String getSource() {
        return source;
    }

    @Override
    public boolean isInternal() {
        return internal;
    }

    @Override
    public boolean isIncomplete() {
        return incomplete;
    }

    @Override
    public boolean isDeleted() {
        return deleted;
    }

    @Override
    public boolean hasReferenceId() {
        return hasReferenceId;
    }

    @Override
    public Long getReferenceId() {
        return identificatie;
    }

    @Override
    public boolean hasGeometry() {
        return true;
    }

    @Override
    public Geometry getGeometry() {
        return geometry;
    }

    @Override
    public Map<String, String> getOtherTags() {
        return otherTags;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Comparable<Long> getId() {
        return identificatie;
    }

    @Override
    public boolean hasName() {
        return hasName;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Collection<OsmPrimitive> getPrimitives() {
        // TODO Auto-generated method stub
        return null;
    }
}
