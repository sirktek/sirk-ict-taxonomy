package no.sirktek.taxonomy.loader;

/**
 * Loads the ICT taxonomy from RDF-S Turtle files using Apache Jena.
 */
public class IctRdfsTaxonomyLoader extends RdfsTaxonomyLoader {

    private static final String ICT_NAMESPACE = "http://taxonomy.sirktek.no/ict#";
    private static final String RESOURCE_PATH = "/taxonomy/ict-base.ttl";

    /**
     * Default constructor.
     */
    public IctRdfsTaxonomyLoader() {
        super();
    }

    @Override
    protected String getNamespace() {
        return ICT_NAMESPACE;
    }

    @Override
    protected String getResourcePath() {
        return RESOURCE_PATH;
    }
}
