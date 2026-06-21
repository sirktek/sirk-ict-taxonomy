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
    void shouldFindInformationTechnologyRoot() {
        Optional<CategoryInfo> it = taxonomyService.getCategoryByClassName("InformationTechnology");

        assertTrue(it.isPresent());
        assertEquals("InformationTechnology", it.get().className());
        assertTrue(it.get().uri().startsWith("http://taxonomy.sirktek.no/ict#"));
    }

    @Test
    void shouldFindLevel1Categories() {
        for (String className : new String[]{"Computer", "NetworkEquipment", "Network"}) {
            assertTrue(taxonomyService.getCategoryByClassName(className).isPresent(),
                    "Expected level-1 category present: " + className);
        }
    }

    @Test
    void shouldDistinguishNetworkGroupingFromNetworkEquipment() {
        // The logical "Network" grouping is a separate class from the
        // "NetworkEquipment" device type.
        assertTrue(taxonomyService.getCategoryByClassName("Network").isPresent());
        assertTrue(taxonomyService.getCategoryByClassName("NetworkEquipment").isPresent());
        assertNotEquals(
                taxonomyService.getCategoryByClassName("Network").orElseThrow().uri(),
                taxonomyService.getCategoryByClassName("NetworkEquipment").orElseThrow().uri());
    }

    @Test
    void shouldFindComputerSubcategories() {
        assertTrue(taxonomyService.getCategoryByClassName("Desktop").isPresent());
        assertTrue(taxonomyService.getCategoryByClassName("Laptop").isPresent());
        assertTrue(taxonomyService.getCategoryByClassName("Server").isPresent());
    }

    @Test
    void shouldFindNetworkEquipmentSubcategories() {
        for (String className : new String[]{"Router", "Switch", "AccessPoint", "Firewall"}) {
            assertTrue(taxonomyService.getCategoryByClassName(className).isPresent(),
                    "Expected NetworkEquipment subcategory present: " + className);
        }
    }

    @Test
    void allClassUrisAreInIctNamespace() {
        for (CategoryInfo cat : taxonomyService.loadBaseTaxonomy().rootCategories()) {
            assertTrue(cat.uri() != null && cat.uri().startsWith("http://taxonomy.sirktek.no/ict#"),
                    "Unexpected namespace for " + cat.className() + ": " + cat.uri());
        }
    }

    @Test
    void shouldReturnEmptyForNonExistentCategory() {
        assertTrue(taxonomyService.getCategoryByClassName("NonExistent").isEmpty());
    }
}
