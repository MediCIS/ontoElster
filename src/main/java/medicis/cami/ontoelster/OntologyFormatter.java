package medicis.cami.ontoelster;

import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLOntology;

/**
 * Get annotations from classes of a given ontology as a collection.
 *
 * @author javier
 */
public class OntologyFormatter {

    public static IRI DEFINITION = IRI.create("http://purl.obolibrary.org/obo/IAO_0000115");
    public static IRI ALT_LABEL = IRI.create("http://www.w3.org/2004/02/skos/core#altLabel");
    public static IRI PREF_LABEL = IRI.create("http://www.w3.org/2004/02/skos/core#prefLabel");

    private final OWLOntology ontology;

    public OntologyFormatter(OWLOntology ontology) {

        this.ontology = ontology;
    }

    /**
     * Obtain definition (IAO_0000115) and labels (SKOS "prefLabel", and
     * "altLabel") annotations in German, English, and French.
     *
     * @return A list of ClassAnnotations of filtered classes
     */
    private List<ClassAnnotations> getAnnotations() {

        return ontology.classesInSignature()
                .map(c -> {

                    ClassAnnotations classAnnotations = new ClassAnnotations();
                    classAnnotations.uri = c.getIRI().toURI();
                    ontology.annotationAssertionAxioms(c.getIRI())
                    .forEach(ca -> {

                        OWLAnnotation annotation = ca.getAnnotation();
                        IRI property = annotation.getProperty().getIRI();
                        OWLLiteral value = annotation.getValue().asLiteral().get();
                        String literal = value.getLiteral();

                        // Get labels by language
                        if (property.equals(PREF_LABEL)) {

                            String language = value.getLang();
                            switch (language) {
                                case "fr":
                                    classAnnotations.fr = literal;
                                    break;
                                case "de":
                                    classAnnotations.de = literal;
                                    break;
                                case "en":
                                    classAnnotations.en = literal;
                                    break;
                            }
                        }

                        // Get alternative labels by language
                        if (property.equals(ALT_LABEL)) {

                            String language = value.getLang();
                            switch (language) {
                                case "fr":
                                    classAnnotations.altFr = literal;
                                    break;
                                case "de":
                                    classAnnotations.altDe = literal;
                                    break;
                                case "en":
                                    classAnnotations.altEn = literal;
                                    break;
                            }
                        }

                        // Get definition and reformatting text
                        if (property.equals(DEFINITION)) {

                            // Replacing new lines by spaces
                            literal = literal.replaceAll("[\\t|\\r?\\n]+", " ");
                            classAnnotations.definition = literal;
                        }
                    });

                    return classAnnotations;
                })
                .collect(Collectors.toList());
    }

    /**
     * Export the sub-ontology as a comma-separated values file.
     *
     * @param path Destination path of the CSV file.
     * @throws java.io.IOException
     */
    public void exportAsCSV(final Path path)
            throws
            java.io.IOException {

        // Collection of all annotations by class filtering
        List<ClassAnnotations> annotations = getAnnotations();

        // Save collection of annotations into a CSV file        
        ClassAnnotations.print(path, annotations);
    }
}
