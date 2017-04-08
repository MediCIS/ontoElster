package medicis.cami.ontoelster;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.formats.RDFXMLDocumentFormat;
import org.semanticweb.owlapi.io.FileDocumentSource;
import org.semanticweb.owlapi.io.FileDocumentTarget;
import org.semanticweb.owlapi.io.OWLOntologyDocumentSource;
import org.semanticweb.owlapi.io.OWLOntologyDocumentTarget;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.MissingImportHandlingStrategy;
import org.semanticweb.owlapi.model.OWLDocumentFormat;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyLoaderConfiguration;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

/**
 * This program filters selected classes from an existing Ontology, saving those
 * in a new (Sub)Ontology File and save them in a CVS file.
 * 
 *  @author chantal
 */
public class Main {

    public static void main(String... arguments)
            throws OWLOntologyCreationException, IOException, OWLOntologyStorageException {

        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        // // Load ontology from Web
        // IRI iri = IRI.create("https://medicis.univ-rennes1.fr/ontologies/ontospm/OntoSPM.owl");
        // OWLOntology ontology = manager.loadOntologyFromOntologyDocument(iri);

        // Load ontology from local file
        File ontologyFile = new File("C:\\Users\\medicis\\Documents\\Data\\OWLs\\151112_OntoSPM_RDF2.owl");
        OWLOntologyDocumentSource source = new FileDocumentSource(ontologyFile);

        // Load ontology ignoring imports 
        OWLOntologyLoaderConfiguration configuration = new OWLOntologyLoaderConfiguration();
        configuration = configuration.setMissingImportHandlingStrategy(MissingImportHandlingStrategy.SILENT);
        OWLOntology ontology = manager.loadOntologyFromOntologyDocument(source, configuration);

        // Read file with selected classes
        String selectionFileName = "C:\\Users\\medicis\\IdeaProjects\\ontoElster\\src\\main\\java\\selection.txt";
        List<String> selectedClasses = Files.readAllLines(Paths.get(selectionFileName));

        // Get a subontology based on selected classes from the reference ontology
        OntologyExtractor extractor = new OntologyExtractor(manager);
        IRI subontologyIRI = IRI.create("https://medicis.univ-rennes1.fr/ontologies/ontospm/subOntoSPM.owl");
        OWLOntology subontology = extractor.getSubontology(ontology, subontologyIRI, selectedClasses);

        // Save the subontology into a file as RDF document
        File file = new File("C:\\Users\\medicis\\IdeaProjects\\ontoElster\\src\\main\\java\\excerpt.owl");
        OWLDocumentFormat format = new RDFXMLDocumentFormat();
        OWLOntologyDocumentTarget target = new FileDocumentTarget(file);
        manager.saveOntology(subontology, format, target);

        // Collection of all annotations by class
        List<ClassAnnotations> annotations = OntologyAnnotationsFormatter.annotations(subontology, selectedClasses);

        // Save collection of annotations into a CSVs
        Path csv = Paths.get("C:\\Users\\medicis\\IdeaProjects\\ontoElster\\src\\main\\java\\excerpt.csv");
        OntologyAnnotationsFormatter.print(csv, annotations);
    }
}
