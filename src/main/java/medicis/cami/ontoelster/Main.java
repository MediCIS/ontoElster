package medicis.cami.ontoelster;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import org.semanticweb.owlapi.model.IRI;

/**
 * This program filters selected classes from an existing ontology, saving those
 * in a new ontology file (OWL in RDF/XML format).
 *
 * @author chantal
 */
public class Main {

    // remove extractor from signature
    public static void process(IRI ontology, List<IRI> classes, OntologyExtractor extractor)
            throws
            org.semanticweb.owlapi.model.OWLOntologyCreationException,
            org.semanticweb.owlapi.model.OWLOntologyStorageException {

        // Get a subontology based on selected classes from the reference ontology
        extractor.extractOntology(classes);
        String path = ontology.toURI().getPath();
        String basename = path.substring(path.lastIndexOf("/") + 1, path.lastIndexOf(".") + 1);
        // Save the subontology into a file as RDF document
        Path owl = Paths.get(basename + "owl");
        extractor.saveOntology(owl);
    }

    public static void main(String... arguments)
            throws
            java.io.IOException,
            org.semanticweb.owlapi.model.OWLOntologyCreationException,
            org.semanticweb.owlapi.model.OWLOntologyStorageException {
        
        // Read IRI of target ontology
        IRI ontology = IRI.create(arguments[0]);
        // Read file with selected classes       
        Path file = Paths.get(arguments[1]);
        List<IRI> classes = IRICreator.getIRIs(file);
        OntologyExtractor extractor = new OntologyExtractorWithEquivalences(ontology);
        process(ontology, classes, extractor);
    }
}
