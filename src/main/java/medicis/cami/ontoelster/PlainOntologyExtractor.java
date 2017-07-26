package medicis.cami.ontoelster;

import org.semanticweb.owlapi.model.IRI;

/**
 * Get a (sub)ontology from a reference using a collection of selected classes.
 *
 * @author chantal
 */
public class PlainOntologyExtractor
        extends AbstractOntologyExtractor
        implements OntologyExtractor {

    public PlainOntologyExtractor(final IRI iri)
            throws
            org.semanticweb.owlapi.model.OWLOntologyCreationException {
        super(iri);      
    }
}
