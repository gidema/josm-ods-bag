package org.openstreetmap.josm.plugins.ods.bag.importing;

import java.util.ArrayList;
import java.util.List;

import org.openstreetmap.josm.plugins.ods.context.OdsContextJob;
import org.openstreetmap.josm.plugins.ods.matching.OdsMatchingJob;
import org.openstreetmap.josm.plugins.ods.matching.update.OdsImportContext;

// TODO integrate in to normal context
public class BagImportContext implements OdsImportContext {
    private final List<OdsContextJob> postImportJobs = new ArrayList<>();

    public BagImportContext() {
        postImportJobs.add(new PrimitiveNeighbourAligner());
        postImportJobs.add(new OdsMatchingJob());
    }

    @Override
    public List<OdsContextJob> getPostImportJobs() {
        return postImportJobs;
    }
}
