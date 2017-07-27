package fr.inserm.ltsi.medicis.ontoelster;

import org.semanticweb.owlapi.model.IRI;

/**
 *
 * @author javier
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
