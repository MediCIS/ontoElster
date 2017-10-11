package fr.inserm.ltsi.medicis.ontoelster;

import org.semanticweb.owlapi.model.IRI;

/**
 * @author javier
 */
public class OntologyExtractorFactory {

    private final IRI ontology;
    private ReasonerImplementation implementation;

    public OntologyExtractorFactory(final IRI ontology, final ReasonerImplementation implementation) {

        this.ontology = ontology;
        this.implementation = implementation;
    }

    public OntologyExtractorFactory(final IRI ontology) {

        this(ontology, null);
    }

    public void setImplementation(final ReasonerImplementation implementation) {

        this.implementation = implementation;
    }

    public OntologyExtractor getOntologyExtractor(final int level)
            throws
            org.semanticweb.owlapi.model.OWLOntologyCreationException {

        OntologyExtractor extractor = null;
        switch (level) {
            case 0:
                extractor = new OntologyExtractorWithEquivalences(ontology);
                break;
            case 1:
                extractor = new OntologyExtractorWithInferences(ontology, implementation);
                break;
            default:
                throw new AssertionError();
        }

        return extractor;
    }
}
