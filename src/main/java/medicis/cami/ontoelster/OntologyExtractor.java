package medicis.cami.ontoelster;

import java.nio.file.Path;
import java.util.List;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;

/**
 *
 * @author javier
 */
public interface OntologyExtractor {

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
    void extractOntology(final List<IRI> classes) 
            throws org.semanticweb.owlapi.model.OWLOntologyCreationException;

    OWLOntology getOntology();

    /**
     * Save the sub-ontology into a file as RDF document.
     *
     * @param path Destination file system path of the ontology.
     * @throws org.semanticweb.owlapi.model.OWLOntologyStorageException
     */
    void saveOntology(final Path path)
            throws org.semanticweb.owlapi.model.OWLOntologyStorageException;    
}
