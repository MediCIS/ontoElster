package fr.inserm.ltsi.medicis.ontoelster;

import java.nio.file.Path;
import java.util.List;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;

/**
 *
 * @author javier
 */
public interface OntologyExtractor {

    OWLOntology getOntology();

    void extractOntology(final List<IRI> classes)
            throws
            org.semanticweb.owlapi.model.OWLOntologyCreationException;

    void saveOntology(final Path path)
            throws
            org.semanticweb.owlapi.model.OWLOntologyStorageException;
}
