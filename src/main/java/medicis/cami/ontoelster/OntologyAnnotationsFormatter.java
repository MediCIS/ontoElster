package medicis.cami.ontoelster;

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
public class OntologyAnnotationsFormatter {

    public static IRI DEFINITION = IRI.create("http://purl.obolibrary.org/obo/IAO_0000115");
    public static IRI ALT_LABEL = IRI.create("http://www.w3.org/2004/02/skos/core#altLabel");
    public static IRI PREF_LABEL = IRI.create("http://www.w3.org/2004/02/skos/core#prefLabel");

    /**
     * Obtain definition (IAO_0000115) and labels (SKOS "prefLabel", and
     * "altLabel") annotations in German, English, and French.
     *
     * @param ontology Reference from which annotations are extracted
     * @param classes List of classes into the ontology to get annotations
     * @return A list of ClassAnnotations of filtered classes
     */
    public static List<ClassAnnotations> getAnnotations(final OWLOntology ontology, final List<IRI> classes) {

        return ontology.classesInSignature()
                // filter classes by selection list get all fields of annotations
                .filter(c -> classes.contains(c.getIRI()))

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
}
