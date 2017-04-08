package medicis.cami.ontoelster;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.nio.file.StandardOpenOption.WRITE;
import java.util.ArrayList;
import java.util.List;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.formats.RDFXMLDocumentFormat;
import org.semanticweb.owlapi.io.FileDocumentSource;
import org.semanticweb.owlapi.io.FileDocumentTarget;
import org.semanticweb.owlapi.io.OWLOntologyDocumentSource;
import org.semanticweb.owlapi.io.OWLOntologyDocumentTarget;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.MissingImportHandlingStrategy;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLDocumentFormat;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyLoaderConfiguration;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;


/**
 * This program filters selected classes from an existing Ontology, saving those in a new (Sub)Ontology File and save them in a CVS file.
 * Created by Chantal and Javier on 4/6/2017.
 */
public class Main {

    public static void main(String... arguments)
            throws OWLOntologyCreationException, IOException, OWLOntologyStorageException {

        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        // Load Ontology from Web
        IRI iri = IRI.create("https://medicis.univ-rennes1.fr/ontologies/ontospm/OntoSPM.owl");
        // OWLOntology ontology = manager.loadOntologyFromOntologyDocument(iri);
        // Load Ontology from local File
        File ontologyFile = new File("C:\\Users\\medicis\\Documents\\Data\\OWLs\\151112_OntoSPM_RDF2.owl");
        OWLOntologyDocumentSource source = new FileDocumentSource(ontologyFile);
        OWLOntologyLoaderConfiguration configuration = new OWLOntologyLoaderConfiguration();
        // Ignore Imports from Ontology
        configuration = configuration.setMissingImportHandlingStrategy(MissingImportHandlingStrategy.SILENT);
        OWLOntology ontology = manager.loadOntologyFromOntologyDocument(source, configuration);
        IRI excerptIRI = IRI.create("https://medicis.univ-rennes1.fr/ontologies/ontospm/subOntoSPM.owl");
        OWLOntology excerptOntology = manager.createOntology(excerptIRI);

        // Read File with selected classes
        String selectionFileName = "C:\\Users\\medicis\\IdeaProjects\\ontoElster\\src\\main\\java\\selection.txt";
        List<String> selectedClasses = Files.readAllLines(Paths.get(selectionFileName));

        // Get all declared classes of the ontology
        ontology.classesInSignature()
                // Filter selected classes from the Ontology
                .filter(c -> selectedClasses.contains(c.getIRI().toURI().getFragment()))
                .forEach(c ->
                {
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

        IRI definition = IRI.create("http://purl.obolibrary.org/obo/IAO_0000115");
        IRI altLabel = IRI.create("http://www.w3.org/2004/02/skos/core#altLabel");
        IRI prefLabel = IRI.create("http://www.w3.org/2004/02/skos/core#prefLabel");
        // Collection of all annotations by class
        List<ClassAnnotations> annotations = new ArrayList<>();
        // Get classes of SubOntology and writing them into a CVS file
        excerptOntology.classesInSignature()

                // filter classes by selection list get all fields of annotations
                .filter(c -> selectedClasses.contains(c.getIRI().toURI().getFragment()))
                .forEach(c -> {
                    ClassAnnotations ann = new ClassAnnotations();
                    ann.setUri(c.getIRI().toURI());
                    excerptOntology.annotationAssertionAxioms(c.getIRI())
                            .forEach(ca -> {

                                OWLAnnotation annotation = ca.getAnnotation();
                                IRI property = annotation.getProperty().getIRI();
                                OWLLiteral value = annotation.getValue().asLiteral().get();

                                // Get labels by language
                                if (property.equals(prefLabel)) {
                                    String literal = value.getLiteral();
                                    String language = value.getLang();
                                    if (language.equals("fr")) {
                                        ann.setFr(literal);
                                    } else if (language.equals("de")) {
                                        ann.setDe(literal);
                                    } else if (language.equals("en")) {
                                        ann.setEn(literal);
                                    }
                                }

                                // Get alternative labels by language
                                if (property.equals(altLabel)) {
                                    String literal = value.getLiteral();
                                    String language = value.getLang();
                                    if (language.equals("fr")) {
                                        ann.setAltFr(literal);
                                    } else if (language.equals("de")) {
                                        ann.setAltDe(literal);
                                    } else if (language.equals("en")) {
                                        ann.setAltEn(literal);
                                    }
                                }

                                // Get definition and reformatting text
                                if (property.equals(definition)) {

                                    // Replacing new lines by spaces
                                    String literal = value.getLiteral().replaceAll("[\\t|\\r?\\n]+", " ");
                                    ann.setDefinition(literal);
                                }
                            });
                    annotations.add(ann);
                });

        // Save collection of annotations into a CVS
        Path csv = Paths.get("C:\\Users\\medicis\\IdeaProjects\\ontoElster\\src\\main\\java\\excerpt.csv");
        OpenOption[] options = new OpenOption[]{WRITE, CREATE, TRUNCATE_EXISTING};
        try (BufferedWriter writer = Files.newBufferedWriter(csv, options)) {
            for (ClassAnnotations annotation : annotations)
                writer.write(annotation.toString());
        }
    }
}
