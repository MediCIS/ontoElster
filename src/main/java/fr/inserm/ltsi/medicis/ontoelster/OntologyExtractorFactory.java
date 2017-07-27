package fr.inserm.ltsi.medicis.ontoelster;

import org.semanticweb.owlapi.model.IRI;

/**
 *
 * @author javier
 */
public class OntologyExtractorFactory {

    private final IRI ontology;

    public OntologyExtractorFactory(final IRI ontology) {

        this.ontology = ontology;
    }

    public OntologyExtractor getOntologyExtractor(final byte level)
            throws
            org.semanticweb.owlapi.model.OWLOntologyCreationException {

        OntologyExtractor extractor = null;
        switch (level) {
            case 0:
                extractor = new PlainOntologyExtractor(ontology);
                break;
            case 1:
                extractor = new OntologyExtractorWithEquivalences(ontology);
                break;
            case 2:
                extractor = new OntologyExtractorWithInferences(ontology);
                break;
            default:
                throw new AssertionError();
        }

        return extractor;
    }
}
