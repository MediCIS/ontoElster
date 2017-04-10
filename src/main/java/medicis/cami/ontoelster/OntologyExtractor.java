package medicis.cami.ontoelster;

import java.util.List;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;

/**
 * Get a (sub)ontology from a reference using a collection of selected classes.
 *
 * @author chantal
 */
public class OntologyExtractor {

    private final OWLOntologyManager manager;

    public OntologyExtractor(final OWLOntologyManager manager) {

        this.manager = manager;
    }

    /**
     * Get a sub-ontology based on a collection of selected classes from a
     * reference ontology.
     *
     * The resulting sub-ontology just includes all sub-class axioms and
     * annotations of the reference ontology in the same document format.
     *
     * @param ontology Reference ontology
     * @param iri IRI of created sub-ontology
     * @param classes List of selected classes
     * @return an ontology containing the selected classes
     * @throws org.semanticweb.owlapi.model.OWLOntologyCreationException
     */
    public OWLOntology getSubontology(final OWLOntology ontology, final IRI iri, final List<String> classes)
            throws
            org.semanticweb.owlapi.model.OWLOntologyCreationException {

        OWLOntology subontology = manager.createOntology(iri);

        // Get all declared classes of the ontology
        ontology.classesInSignature()
                // Filter selected classes from the Ontology
                .filter(c -> classes.contains(c.getIRI().toURI().getFragment()))
                .forEach(c -> {

                    // Get subclasses of current ontology class and add to the excerpt ontology
                    ontology.subClassAxiomsForSubClass(c)
                    .forEach(sc -> {

                        AddAxiom addAxiom = new AddAxiom(subontology, sc);
                        manager.applyChange(addAxiom);
                    });

                    //Get all annotations of the current class and add to the excerpt ontology
                    ontology.annotationAssertionAxioms(c.getIRI())
                    .forEach(ca -> {

                        AddAxiom addAxiom = new AddAxiom(subontology, ca);
                        manager.applyChange(addAxiom);
                    });
                });

        return subontology;
    }
}
