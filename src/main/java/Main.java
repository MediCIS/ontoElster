import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.formats.RDFXMLDocumentFormat;
import org.semanticweb.owlapi.io.FileDocumentTarget;
import org.semanticweb.owlapi.io.OWLOntologyDocumentTarget;
import org.semanticweb.owlapi.io.SystemOutDocumentTarget;
import org.semanticweb.owlapi.model.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * Created by medicis on 4/6/2017.
 */
public class Main {

    public static void main(String... arguments)
            throws OWLOntologyCreationException, IOException, OWLOntologyStorageException {

        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        IRI iri = IRI.create("https://medicis.univ-rennes1.fr/ontologies/ontospm/OntoSPM.owl");
        OWLOntology ontology = manager.loadOntologyFromOntologyDocument(iri);
        IRI excerptIRI = IRI.create("https://medicis.univ-rennes1.fr/ontologies/ontospm/subOntoSPM.owl");
        OWLOntology excerptOntology = manager.createOntology(excerptIRI);

        // Read File with selected classes
        String selectionFileName = "C:\\Users\\medicis\\IdeaProjects\\ontoElster\\src\\main\\java\\selection.txt";
        List<String> selectedClasses = Files.readAllLines(Paths.get(selectionFileName));

        OWLDataFactory dataFactory = manager.getOWLDataFactory();

        // Get all declared classes of the ontology
        ontology.classesInSignature()
                // Filter selected classes from the Ontology
                .filter(c -> selectedClasses.contains(c.getIRI().toURI().getFragment()))
                .forEach(c ->
                {
                    System.out.println(c);

                    // Get subclasses of current ontology class and add to the excerpt ontology
                    ontology.subClassAxiomsForSubClass(c)
                            .forEach(sc -> {
                                AddAxiom addAxiom = new AddAxiom(excerptOntology, sc);
                                manager.applyChange(addAxiom);
                            });
                    //Get all annotations of the current class and add to the excerpt ontology
                    ontology.annotationAssertionAxioms(c.getIRI())
                            .forEach(ca -> {
                                AddAxiom addAxiom = new AddAxiom(excerptOntology, ca);
                                manager.applyChange(addAxiom);
                            });
                });

        File file = new File("C:\\Users\\medicis\\IdeaProjects\\ontoElster\\src\\main\\java\\excerpt.owl");
        OWLDocumentFormat format = new RDFXMLDocumentFormat();
        OWLOntologyDocumentTarget target = new FileDocumentTarget(file);
        manager.saveOntology(excerptOntology, format, target);
    }
}
