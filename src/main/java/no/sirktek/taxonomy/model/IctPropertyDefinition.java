package no.sirktek.taxonomy.model;

/**
 * ICT-specific property definition with type detection.
 *
 * Maps the {@code ict:*} property ranges to a {@link PropertyType}. Cross-cutting
 * common ranges (e.g. {@code common:AssetValueEntry}) are resolved upstream by
 * {@code CommonPropertyDefinition} and are not handled here.
 */
public final class IctPropertyDefinition {

    private IctPropertyDefinition() {
        // Utility class — not instantiable.
    }

    /**
     * Convert an RDF range type to the ICT {@link PropertyType} equivalent.
     *
     * @param propertyDef the property definition
     * @return the corresponding PropertyType enum value
     */
    public static PropertyType getPropertyType(PropertyDefinition propertyDef) {
        String rangeType = propertyDef.rangeType();

        if (rangeType == null) {
            return PropertyType.STRING;
        }

        return switch (rangeType) {
            case "http://www.w3.org/2001/XMLSchema#string"  -> PropertyType.STRING;
            case "http://www.w3.org/2001/XMLSchema#decimal" -> PropertyType.DECIMAL;
            case "http://www.w3.org/2001/XMLSchema#integer" -> PropertyType.INTEGER;
            case "http://www.w3.org/2001/XMLSchema#date"    -> PropertyType.DATE;
            case "http://www.w3.org/2001/XMLSchema#boolean" -> PropertyType.BOOLEAN;
            case "http://www.w3.org/2001/XMLSchema#anyURI"  -> PropertyType.URL;
            default -> {
                // A category reference: range is a common Manufacturer/Model or
                // an ict:* class (e.g. ict:Network). Multi-valued (common:multiValued)
                // ⇒ MULTI_CATEGORY, otherwise a single CATEGORY reference.
                boolean categoryRef = rangeType.startsWith("http://taxonomy.sirktek.no/ict#")
                        || rangeType.contains("Manufacturer")
                        || rangeType.contains("Model");
                if (categoryRef) {
                    yield propertyDef.multiValued() ? PropertyType.MULTI_CATEGORY : PropertyType.CATEGORY;
                }
                yield PropertyType.STRING;
            }
        };
    }

    /**
     * Property types for the ICT taxonomy.
     */
    public enum PropertyType {
        /** String property type */
        STRING,
        /** Decimal property type */
        DECIMAL,
        /** Integer property type */
        INTEGER,
        /** Date property type */
        DATE,
        /** Boolean property type */
        BOOLEAN,
        /** URL property type */
        URL,
        /** Single category reference property type */
        CATEGORY,
        /** Multi-valued category reference property type */
        MULTI_CATEGORY
    }
}
