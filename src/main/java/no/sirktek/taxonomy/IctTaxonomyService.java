package no.sirktek.taxonomy;

import no.sirktek.taxonomy.loader.IctRdfsTaxonomyLoader;

/**
 * Main service for accessing the ICT (information & communications technology)
 * taxonomy data.
 */
public class IctTaxonomyService extends TaxonomyService {

    /**
     * Default constructor using the ICT taxonomy loader.
     */
    public IctTaxonomyService() {
        super(new IctRdfsTaxonomyLoader());
    }
}
