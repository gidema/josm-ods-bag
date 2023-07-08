package org.openstreetmap.josm.plugins.ods.bag.validation;

import static org.openstreetmap.josm.tools.I18n.tr;

import org.openstreetmap.josm.command.ChangePropertyCommand;
import org.openstreetmap.josm.command.Command;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.data.osm.Relation;
import org.openstreetmap.josm.data.osm.Way;
import org.openstreetmap.josm.data.validation.Severity;
import org.openstreetmap.josm.data.validation.Test;
import org.openstreetmap.josm.data.validation.TestError;

/**
 * Validation test to check BAG reference tags (ref:bag)
 * 
 * A decision was made in the community to include leading zero's for references with less than 16 digits.
 * 
 * @author gertjan
 *
 */
public class BAGReferences extends Test {

    public BAGReferences() {
        super(tr("BAG References"), tr("Checks BAG reference tags (ref:bag) not having 16 digits."));        // TODO Auto-generated constructor stub
    }

    @Override
    public void visit(Way w) {
        checkMissingLeadingZeros(w);    }

    @Override
    public void visit(Relation r) {
        checkMissingLeadingZeros(r);
    }

    protected TestError checkMissingLeadingZeros(OsmPrimitive p) {
        if (p.hasKey("ref:bag") && p.hasKey("building")) {
            String value = p.get("ref:bag");
            if (value.length() < 16) {
                TestError e = TestError.builder(this, Severity.WARNING, 99999)
                   .message(tr("BAG reference with mising leading zeros"))
                   .primitives(p)
                   .build();
                errors.add(e);
                return e;
            }
        }
        return null;
    }

    @Override
    public Command fixError(TestError testError) {
        OsmPrimitive p = testError.getPrimitives().stream().findFirst().get();
        StringBuilder sb = new StringBuilder(p.get("ref:bag"));
        while (sb.length() < 16) {
            sb.insert(0, "0");
        }
        return new ChangePropertyCommand(p, "ref:bag", sb.toString());
    }

    @Override
    public boolean isFixable(TestError testError) {
        return testError.getTester() instanceof BAGReferences;
    }
}
