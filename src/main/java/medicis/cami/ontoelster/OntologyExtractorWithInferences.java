package medicis.cami.ontoelster;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.semanticweb.HermiT.ReasonerFactory;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

/**
 * Get a (sub)ontology from a reference using a collection of selected classes.
 *
 * @author chantal
 */
public class OntologyExtractorWithInferences
        extends AbstractOntologyExtractor
        implements OntologyExtractor {

    private final OWLReasoner reasoner;

    public OntologyExtractorWithInferences(final IRI iri)
            throws
            org.semanticweb.owlapi.model.OWLOntologyCreationException {
        super(iri);

        ReasonerFactory factory = new ReasonerFactory();
        reasoner = factory.createReasoner(reference);

        //TODO try another reasoner 
        //OWLReasonerFactory reasonerFactory = Reasoner//PelletReasonerFactory.getInstance();
        //Reasoner.ReasonerFactory().createReasoner(ontology);
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
