package medicis.cami.ontoelster;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.IRIDocumentSource;
import org.semanticweb.owlapi.io.OWLOntologyDocumentSource;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.MissingImportHandlingStrategy;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyLoaderConfiguration;
import org.semanticweb.owlapi.model.OWLOntologyManager;

/**
 * This program filters selected classes from an existing ontology, saving those
 * in a new ontology file (OWL in RDF/XML format) and also in a CVS file.
 *
 * @author chantal
 */
public class Main {

    public static void main(String... arguments)
            throws
            java.io.IOException,
            org.semanticweb.owlapi.model.OWLOntologyCreationException,
            org.semanticweb.owlapi.model.OWLOntologyStorageException {

        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();

//        // Load ontology from local file
//        Path path = Paths.get("C:\\Users\\medicis\\Documents\\Data\\OWLs\\151112_OntoSPM_RDF2.owl");
//        IRI iri = IRI.create(path.toFile());
        // Load ontology from Web
        IRI iri = IRI.create("http://neurolog.unice.fr/ontoneurolog/v3.1/instrument.owl");
        OWLOntologyDocumentSource source = new IRIDocumentSource(iri);

        // ignore imports while loading
        OWLOntologyLoaderConfiguration configuration = new OWLOntologyLoaderConfiguration();
        configuration = configuration.setMissingImportHandlingStrategy(MissingImportHandlingStrategy.SILENT);
        OWLOntology ontology = manager.loadOntologyFromOntologyDocument(source, configuration);

        // Read file with selected classes
        Path txt = Paths.get("src", "main", "resources", "classes.txt");
        List<IRI> selection = getIRIs(txt);

        // Get a subontology based on selected classes from the reference ontology
        OntologyExtractor extractor = new OntologyExtractor(ontology);
        extractor.extractSubOntology(selection);

        String path = iri.toURI().getPath();
        String basename = path.substring(path.lastIndexOf("/") + 1, path.lastIndexOf(".") + 1);

        // Save the subontology into a file as RDF document
        Path owl = Paths.get(basename + "owl");
        extractor.saveOntology(owl);

        // Save annotations into a CSV file   
        OntologyFormatter formatter = new OntologyFormatter(extractor.getSubOntology());
        Path csv = Paths.get(basename + "csv");
        formatter.exportAsCSV(csv);
    }

    /**
     * Get list of IRIs as Strings and convert them to IRIs
     *
     * @param path Path of where the list of classes names (IRIs) are located.
     * @return A list of IRIs mapped from the input file.
     * @throws java.io.IOException
     */
    private static List<IRI> getIRIs(Path path)
            throws
            java.io.IOException {

        return Files.readAllLines(path).stream()
                .map(IRI::create)
                .collect(Collectors.toList());
    }
}
