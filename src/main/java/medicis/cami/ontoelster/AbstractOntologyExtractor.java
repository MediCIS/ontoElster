package medicis.cami.ontoelster;

import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.formats.RDFXMLDocumentFormat;
import org.semanticweb.owlapi.io.FileDocumentTarget;
import org.semanticweb.owlapi.io.OWLOntologyDocumentTarget;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLDocumentFormat;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;

/**
 *
 * @author chantal
 */
public abstract class AbstractOntologyExtractor
        implements OntologyExtractor {

    protected final OWLOntologyManager manager;
    protected final OWLOntology reference;
    protected final OWLOntology ontology;
    protected final OntologyFacility facility;

    public AbstractOntologyExtractor(final IRI iri)
            throws
            org.semanticweb.owlapi.model.OWLOntologyCreationException {

        facility = new OntologyFacility(iri);
        reference = facility.getOntology();
        manager = OWLManager.createOWLOntologyManager();
        ontology = manager.createOntology(reference.getOntologyID());
    }

    @Override
    public final OWLOntology getOntology() {

        return ontology;
    }

    /**
     * Save the sub-ontology into a file as RDF document.
     *
     * @param path Destination file system path of the ontology.
     * @throws org.semanticweb.owlapi.model.OWLOntologyStorageException
     */
    @Override
    public final void saveOntology(final Path path)
            throws
            org.semanticweb.owlapi.model.OWLOntologyStorageException {

        OWLDocumentFormat format = new RDFXMLDocumentFormat();
        OWLOntologyDocumentTarget target = new FileDocumentTarget(path.toFile());
        manager.saveOntology(ontology, format, target);
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

        classes.stream()
                .map(facility::createOntologyClass)
                .forEach(c -> {
                    // Get class definition of add it to the sub-ontology
                    Stream declaration = reference.declarationAxioms(c);
                    addAxioms(declaration);
                    // Set class hierarchy (?)
                    Stream subclasses = reference.subClassAxiomsForSubClass(c);
                    addAxioms(subclasses);
                    // Get class annotations
                    Stream annotations = reference.annotationAssertionAxioms(c.getIRI());
                    addAxioms(annotations);
                });
    }

    /**
     * Use a stream to apply changes into the sub-ontology member class.
     *
     * @param <T> Sub-type of OWLAxiom.
     * @param axiomStream Stream from where the axioms are obtained and update
     * the changes using the manager of the class.
     */
    protected <T extends OWLAxiom> void addAxioms(Stream<T> axiomStream) {

        axiomStream
                .map(a -> new AddAxiom(ontology, a))
                .forEach(manager::applyChange);
    }
}
