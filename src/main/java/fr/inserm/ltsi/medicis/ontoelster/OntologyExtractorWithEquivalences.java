package fr.inserm.ltsi.medicis.ontoelster;

import org.semanticweb.owlapi.model.IRI;

import java.util.List;
import java.util.stream.Stream;

/**
 * @author javier
 */
public class OntologyExtractorWithEquivalences
        extends PlainOntologyExtractor {

    public OntologyExtractorWithEquivalences(final IRI iri)
            throws
            org.semanticweb.owlapi.model.OWLOntologyCreationException {
        super(iri);
    }

    @Override
    public void extractOntology(final List<IRI> classes)
            throws
            org.semanticweb.owlapi.model.OWLOntologyCreationException {

        super.extractOntology(classes);

        Stream stream = classes.stream()
                .map(facility::createOntologyClass)
                .flatMap(reference::equivalentClassesAxioms);
        addAxioms(stream);
    }
}
