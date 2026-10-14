package no.sirktek.taxonomy;

import no.sirktek.taxonomy.model.CategoryInfo;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pins that ICT opts into no commons property: nothing from commons (accounting,
 * commercial terms, ...) attaches to any ICT class.
 */
class CommonPropertyOptInTest {

    private static final String NS = "http://taxonomy.sirktek.no/ict#";
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
    void noIctClassCarriesACommonsProperty() {
        List<CategoryInfo> all = allCategories();
        assertFalse(all.isEmpty());
        for (CategoryInfo c : all) {
            c.properties().forEach(p -> assertTrue(p.uri() != null && p.uri().startsWith(NS),
                    c.className() + " carries non-ICT property " + p.name() + " (" + p.uri() + ")"));
        }
    }

    @Test
    void noIctClassCarriesAccountingProperties() {
        for (CategoryInfo c : allCategories()) {
            c.properties().forEach(p -> assertFalse(ACCOUNTING.contains(p.name()),
                    c.className() + " carries accounting property " + p.name()));
        }
    }
}
