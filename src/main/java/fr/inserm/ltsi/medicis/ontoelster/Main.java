package fr.inserm.ltsi.medicis.ontoelster;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import org.semanticweb.owlapi.model.IRI;

/**
 * This program filters selected classes from an existing ontology, saving those
 * in a new ontology file (OWL in RDF/XML format).
 *
 * @author chantal
 */
public class Main {

    /**
     * Get list of IRIs as Strings and convert them to IRIs
     *
     * @param path Path of where the list of classes names (IRIs) are located.
     * @return A list of IRIs mapped from the input file.
     * @throws java.io.IOException
     */
    public static List<IRI> getIRIs(final Path path)
            throws
            java.io.IOException {

        return Files.readAllLines(path).stream()
                //  ignore lines stating with #
                .filter(s -> !s.startsWith("#"))
                .map(IRI::create)
                .collect(Collectors.toList());
    }

    /**
     * Extract and save the sub-ontology .
     *
     * The resulting ontology is saved as a XML/RDF file is the $CWD
     *
     * @param ontology IRI (also works as path) of the target ontology
     * @param classes List of IRIs to extract
     * @param extractor Implementation of an ontology extractor
     * @throws org.semanticweb.owlapi.model.OWLOntologyCreationException
     * @throws org.semanticweb.owlapi.model.OWLOntologyStorageException
     */
    public static void process(IRI ontology, List<IRI> classes, OntologyExtractor extractor)
            // TODO remove extractor from signature
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
        List<IRI> classes = getIRIs(file);
        // Instantiate a default extractor
        OntologyExtractor extractor = new OntologyExtractorWithEquivalences(ontology);
        if (arguments.length > 2) {

            int extractionLevel = Integer.valueOf(arguments[2]);
            switch (extractionLevel) {

                case 0:
                    extractor = new PlainOntologyExtractor(ontology);
                    break;
                case 1:
                    extractor = new OntologyExtractorWithEquivalences(ontology);
                    break;
                case 2:
                    extractor = new OntologyExtractorWithInferences(ontology);
                    break;
            }
        }

        // Extract and save ontology
        process(ontology, classes, extractor);
    }
}
