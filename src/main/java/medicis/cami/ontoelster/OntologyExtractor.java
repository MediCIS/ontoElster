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
 * Get a (sub)ontology from a reference using a collection of selected classes.
 *
 * @author chantal
 */
public class OntologyExtractor {

    private final OWLOntologyManager manager;
    private final OWLOntology reference;
    private final OWLOntology subontology;

    public OntologyExtractor(final OWLOntology ontology)
            throws
            org.semanticweb.owlapi.model.OWLOntologyCreationException {

        this.reference = ontology;
        this.manager = OWLManager.createOWLOntologyManager();
        this.subontology = manager.createOntology(ontology.getOntologyID().getOntologyIRI().get());
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
    public void extractSubOntology(final List<IRI> classes)
            throws
            org.semanticweb.owlapi.model.OWLOntologyCreationException {

        // Get all declared classes of the ontology
        reference.classesInSignature()
                // Filter selected classes from the Ontology
                .filter(c -> classes.contains(c.getIRI()))
                .forEach(c -> {

                    // Get subclasses of current ontology class and add to the excerpt ontology
                    Stream subclasses = reference.subClassAxiomsForSubClass(c);
                    addAxioms(subclasses);

                    //Get all annotations of the current class and add to the excerpt ontology
                    Stream annotations = reference.annotationAssertionAxioms(c.getIRI());
                    addAxioms(annotations);
                });
    }

    /**
     * Use a stream to apply changes into the sub-ontology member class.
     *
     * @param <T> Sub-type of OWLAxiom.
     * @param stream Stream from where the axioms are obtained and update the
     * changes using the manager of the class.
     */
    private <T extends OWLAxiom> void addAxioms(Stream<T> stream) {

        stream.forEach(a -> {

            AddAxiom addAxiom = new AddAxiom(subontology, a);
            manager.applyChange(addAxiom);
        });
    }

    /**
     * Save the sub-ontology into a file as RDF document.
     *
     * @param path Destination file system path of the ontology.
     * @throws org.semanticweb.owlapi.model.OWLOntologyStorageException
     */
    public void saveOntology(final Path path)
            throws
            org.semanticweb.owlapi.model.OWLOntologyStorageException {

        OWLDocumentFormat format = new RDFXMLDocumentFormat();
        OWLOntologyDocumentTarget target = new FileDocumentTarget(path.toFile());
        manager.saveOntology(subontology, format, target);
    }

    /**
     * Export the sub-ontology as a comma-separated values file.
     *
     * @param path Destination path of the CSV file.
     * @param classes List of classes to export into the CSV file.
     * @throws java.io.IOException
     */
    public void exportAsCSV(final Path path, final List<IRI> classes)
            throws
            java.io.IOException {

        // Collection of all annotations by class filtering
        List<ClassAnnotations> annotations = OntologyAnnotationsFormatter.getAnnotations(subontology, classes);

        // Save collection of annotations into a CSV file        
        ClassAnnotations.print(path, annotations);
    }
}
