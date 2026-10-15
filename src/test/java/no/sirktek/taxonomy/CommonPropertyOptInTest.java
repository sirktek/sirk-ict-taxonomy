package no.sirktek.taxonomy;

import no.sirktek.taxonomy.model.CategoryInfo;
import no.sirktek.taxonomy.model.PropertyDefinition;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pins that ICT opts into only the shared asset properties from commons (manufacturer,
 * model, serialNumber); nothing else from commons (accounting, commercial terms, ...)
 * attaches to any ICT class.
 */
class CommonPropertyOptInTest {

    private static final String NS = "http://taxonomy.sirktek.no/ict#";
    private static final String COMMON_NS = "http://taxonomy.sirktek.no/common#";
    private static final Set<String> SHARED = Set.of("manufacturer", "model", "serialNumber");
    private static final Set<String> ACCOUNTING = Set.of(
            "ledgerAccount", "depreciationGroup", "valuations", "bookValue", "taxValue", "wealthTaxValue");

    private final IctTaxonomyService service = new IctTaxonomyService();

    private List<CategoryInfo> allCategories() {
        List<CategoryInfo> all = new ArrayList<>();
        service.loadBaseTaxonomy().rootCategories().forEach(r -> collect(r, all));
        return all;
    }

    private void collect(CategoryInfo c, List<CategoryInfo> out) {
        out.add(c);
        c.children().forEach(ch -> collect(ch, out));
    }

    @Test
    void noIctClassCarriesACommonsPropertyOtherThanTheSharedOnes() {
        List<CategoryInfo> all = allCategories();
        assertFalse(all.isEmpty());
        for (CategoryInfo c : all) {
            c.properties().forEach(p -> assertTrue(
                    (p.uri() != null && p.uri().startsWith(NS))
                            || (p.uri() != null && p.uri().equals(COMMON_NS + p.name()) && SHARED.contains(p.name())),
                    c.className() + " carries non-ICT property " + p.name() + " (" + p.uri() + ")"));
        }
    }

    @Test
    void sharedPropertiesComeFromCommonsOnHardwareAndSoftware() {
        assertSharedOnce("Hardware", "manufacturer");
        assertSharedOnce("Hardware", "model");
        assertSharedOnce("Software", "manufacturer");
        assertSharedOnce("Software", "model");
        assertSharedOnce("Hardware", "serialNumber");
        assertFalse(propertyNames("Software").contains("serialNumber"));
    }

    private void assertSharedOnce(String className, String name) {
        CategoryInfo c = allCategories().stream()
                .filter(x -> className.equals(x.className())).findFirst().orElseThrow();
        List<PropertyDefinition> matches = c.properties().stream()
                .filter(p -> name.equals(p.name())).toList();
        assertEquals(1, matches.size(), className + " should carry " + name + " once");
        assertEquals(COMMON_NS + name, matches.get(0).uri(), className + "." + name + " IRI");
    }

    private List<String> propertyNames(String className) {
        return allCategories().stream().filter(x -> className.equals(x.className())).findFirst()
                .orElseThrow().properties().stream().map(p -> p.name()).toList();
    }

    @Test
    void noIctClassCarriesAccountingProperties() {
        for (CategoryInfo c : allCategories()) {
            c.properties().forEach(p -> assertFalse(ACCOUNTING.contains(p.name()),
                    c.className() + " carries accounting property " + p.name()));
        }
    }
}
