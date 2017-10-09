package fr.inserm.ltsi.medicis.ontoelster;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

/**
 * Get the (sub)ontology with a ontology reasoner.
 *
 * The ontology includes the selected classes and their super classes in
 * hierarchy. By default the Hermit reasoner is hard coded but it may changed by
 * changing the Reasoner implementation value.
 *
 * @author javier
 */
public class OntologyExtractorWithInferences
        extends PlainOntologyExtractor {

    private final OWLReasoner reasoner;

    public OntologyExtractorWithInferences(final IRI iri, final ReasonerImplementation implementation)
            throws
            org.semanticweb.owlapi.model.OWLOntologyCreationException {
        super(iri);

        ReasonerFactory factory = ReasonerFactory.getInstance(implementation);
        reasoner = factory.createReasoner(reference);
    }

    @Override
    public void extractOntology(final List<IRI> classes)
            throws
            org.semanticweb.owlapi.model.OWLOntologyCreationException {

        super.extractOntology(classes);

        // extract also the equivalent classes
        Stream stream = classes.stream()
                .map(facility::createOntologyClass)
                .flatMap(reference::equivalentClassesAxioms);
        addAxioms(stream);

//        List<IRI> equivalent = classes.stream()
//                .map(facility::createOntologyClass)
//                .flatMap(reference::equivalentClassesAxioms)
//                .flatMap(OWLEquivalentClassesAxiom::classExpressions)
//                .flatMap(OWLClassExpression::classesInSignature)
//                .map(OWLClass::getIRI)
//                .collect(Collectors.toList());
//
//        super.extractOntology(equivalent);
        List<IRI> superclasses = classes.stream()
                .map(facility::createOntologyClass)
                .flatMap(c -> reasoner.getSuperClasses(c).entities())
                .map(OWLClass::getIRI)
                .collect(Collectors.toList());

        super.extractOntology(superclasses);

        List<IRI> subclassesInSignature = classes.stream()
                .map(facility::createOntologyClass)
                .flatMap(reference::subClassAxiomsForSubClass)
                .flatMap(OWLSubClassOfAxiom::nestedClassExpressions)
                .flatMap(OWLClassExpression::classesInSignature)
                .map(OWLClass::getIRI)
                .collect(Collectors.toList());

        super.extractOntology(subclassesInSignature);
    }
}
