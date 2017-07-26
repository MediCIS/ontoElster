package medicis.cami.ontoelster;

import java.util.List;
import java.util.stream.Stream;
import org.semanticweb.owlapi.model.IRI;

/**
 * Get a (sub)ontology from a reference using a collection of selected classes.
 *
 * @author chantal
 */
public class OntologyExtractorWithEquivalences
        extends AbstractOntologyExtractor
        implements OntologyExtractor {

    public OntologyExtractorWithEquivalences(final IRI iri)
            throws
            org.semanticweb.owlapi.model.OWLOntologyCreationException {
        super(iri);
    }

    /**
     * Get a sub-ontology based on a collection of selected classes from a
     * reference ontology.
     *
     * The resulting sub-ontology just includes all sub-class axioms and
     * annotations of the reference ontology in the same document format.
     *
     * @param classes List of selected classes
     * @throws org.semanticweb.owlapi.model.OWLOntologyCreationException
     */
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
