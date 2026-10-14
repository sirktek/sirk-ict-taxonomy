package no.sirktek.taxonomy;

import no.sirktek.taxonomy.model.CategoryInfo;
import no.sirktek.taxonomy.model.TaxonomyTree;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class TaxonomyServiceTest {

    private IctTaxonomyService taxonomyService;

    @BeforeEach
    void setUp() {
        taxonomyService = new IctTaxonomyService();
    }

    @Test
    void shouldLoadBaseTaxonomy() {
        TaxonomyTree taxonomy = taxonomyService.loadBaseTaxonomy();

        assertNotNull(taxonomy);
        assertNotNull(taxonomy.rootCategories());
        assertFalse(taxonomy.rootCategories().isEmpty());
    }

    @Test
    void ictIsTheSingleRoot() {
        var roots = taxonomyService.loadBaseTaxonomy().rootCategories();
        assertEquals(1, roots.size());
        assertEquals("ICT", roots.get(0).className());
        assertEquals("ICT", roots.get(0).englishName());
        assertEquals("IKT", roots.get(0).norwegianName());
    }

    @Test
    void hardwareSoftwareAndNetworkSitUnderIct() {
        CategoryInfo ict = taxonomyService.getCategoryByClassName("ICT").orElseThrow();
        for (String className : new String[]{"Hardware", "Software", "Network"}) {
            CategoryInfo c = taxonomyService.getCategoryByClassName(className).orElseThrow();
            assertEquals("ICT", c.parentClassName());
            assertFalse(c.isRoot(), className + " should not be a root");
            assertTrue(ict.children().stream().anyMatch(ch -> className.equals(ch.className())),
                    className + " should be among ICT's children");
        }
    }

    @Test
    void shouldDistinguishNetworkGroupingFromNetworkEquipment() {
        // The logical "Network" grouping root is separate from the
        // "NetworkEquipment" device type (which lives under Hardware).
        assertTrue(taxonomyService.getCategoryByClassName("Network").isPresent());
        Optional<CategoryInfo> equipment = taxonomyService.getCategoryByClassName("NetworkEquipment");
        assertTrue(equipment.isPresent());
        assertEquals("Hardware", equipment.get().parentClassName());
        assertNotEquals(
                taxonomyService.getCategoryByClassName("Network").orElseThrow().uri(),
                equipment.get().uri());
    }

    @Test
    void shouldFindHardwareSubcategories() {
        for (String className : new String[]{
                "Computer", "Desktop", "Laptop", "Server",
                "NetworkEquipment", "Router", "Switch", "AccessPoint", "Firewall",
                "Peripheral", "Monitor", "Printer"}) {
            assertTrue(taxonomyService.getCategoryByClassName(className).isPresent(),
                    "Expected Hardware subcategory present: " + className);
        }
    }

    @Test
    void shouldFindSoftwareSubcategories() {
        for (String className : new String[]{"Application", "OperatingSystem", "SoftwareLicense"}) {
            assertTrue(taxonomyService.getCategoryByClassName(className).isPresent(),
                    "Expected Software subcategory present: " + className);
        }
    }

    @Test
    void computerHasNoPcAltLabel() {
        CategoryInfo computer = taxonomyService.getCategoryByClassName("Computer").orElseThrow();
        assertFalse(computer.englishAltLabels().contains("PC"),
                "Computer should not carry a 'PC' alt-label (it also covers servers)");
    }

    @Test
    void allClassUrisAreInIctNamespace() {
        taxonomyService.loadBaseTaxonomy().rootCategories().forEach(this::assertNamespaceRecursively);
    }

    private void assertNamespaceRecursively(CategoryInfo cat) {
        assertTrue(cat.uri() != null && cat.uri().startsWith("http://taxonomy.sirktek.no/ict#"),
                "Unexpected namespace for " + cat.className() + ": " + cat.uri());
        cat.children().forEach(this::assertNamespaceRecursively);
    }

    @Test
    void shouldReturnEmptyForNonExistentCategory() {
        assertTrue(taxonomyService.getCategoryByClassName("NonExistent").isEmpty());
    }
}
