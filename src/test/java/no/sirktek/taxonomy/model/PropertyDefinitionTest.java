package no.sirktek.taxonomy.model;

import org.junit.jupiter.api.Test;

import static no.sirktek.taxonomy.model.IctPropertyDefinition.getPropertyType;
import static org.junit.jupiter.api.Assertions.assertEquals;

class PropertyDefinitionTest {

    private static PropertyDefinition prop(String name, String rangeType) {
        return PropertyDefinition.builder().name(name).rangeType(rangeType).build();
    }

    @Test
    void detectsStringRange() {
        assertEquals(IctPropertyDefinition.PropertyType.STRING,
                getPropertyType(prop("serialNumber", "http://www.w3.org/2001/XMLSchema#string")));
    }

    @Test
    void detectsIntegerRange() {
        assertEquals(IctPropertyDefinition.PropertyType.INTEGER,
                getPropertyType(prop("ramGb", "http://www.w3.org/2001/XMLSchema#integer")));
    }

    @Test
    void detectsDateRange() {
        assertEquals(IctPropertyDefinition.PropertyType.DATE,
                getPropertyType(prop("purchaseDate", "http://www.w3.org/2001/XMLSchema#date")));
    }

    @Test
    void detectsDecimalRange() {
        assertEquals(IctPropertyDefinition.PropertyType.DECIMAL,
                getPropertyType(prop("x", "http://www.w3.org/2001/XMLSchema#decimal")));
    }

    @Test
    void detectsCategoryRangeForManufacturerAndModel() {
        assertEquals(IctPropertyDefinition.PropertyType.CATEGORY,
                getPropertyType(prop("manufacturer", "http://taxonomy.sirktek.no/common#Manufacturer")));
        assertEquals(IctPropertyDefinition.PropertyType.CATEGORY,
                getPropertyType(prop("model", "http://taxonomy.sirktek.no/common#Model")));
    }

    @Test
    void nullRangeFallsBackToString() {
        assertEquals(IctPropertyDefinition.PropertyType.STRING, getPropertyType(prop("x", null)));
    }

    @Test
    void shouldExposeExpectedPropertyTypeEnumValues() {
        assertEquals(7, IctPropertyDefinition.PropertyType.values().length);
    }
}
