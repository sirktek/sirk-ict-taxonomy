package no.sirktek.taxonomy;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Verifies that the ICT taxonomy opts its asset classes into the cross-cutting
 * common properties. The opt-in is a {@code schema:domainIncludes} triple that
 * only the merged graph in the consumer sees, so it is checked at the Jena
 * level rather than through the domain loader.
 */
class CommonPropertyOptInTest {

    private static final String COMMON = "http://taxonomy.sirktek.no/common#";
    private static final String NS = "http://taxonomy.sirktek.no/ict#";
    private static Model model;

    @BeforeAll
    static void load() throws IOException {
        model = ModelFactory.createDefaultModel();
        try (InputStream in = CommonPropertyOptInTest.class.getResourceAsStream("/taxonomy/ict-base.ttl")) {
            assertNotNull(in, "ict-base.ttl missing from classpath");
            model.read(in, null, "TURTLE");
        }
    }

    @Test
    void wealthTaxValueIsNotStoredForOperatingAssets() {
        // formuesverdi for driftsmidler is the tax written-down value by rule, so it is derived
        Property domainIncludes = model.createProperty("https://schema.org/domainIncludes");
        assertFalse(model.contains(model.createResource(COMMON + "wealthTaxValue"), domainIncludes, (org.apache.jena.rdf.model.RDFNode) null));
    }

    @Test
    void assetClassesOptIntoAccountingProperties() {
        Property domainIncludes = model.createProperty("https://schema.org/domainIncludes");
        for (String cls : List.of("Hardware", "Software")) {
            Resource target = model.createResource(NS + cls);
            for (String prop : List.of("ledgerAccount", "bookValue", "taxValue", "depreciationGroup")) {
                assertTrue(model.contains(model.createResource(COMMON + prop), domainIncludes, target),
                        "common:" + prop + " should include " + cls + " in its domain");
            }
        }
    }
}
