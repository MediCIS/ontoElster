package medicis.cami.ontoelster;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.IRIDocumentSource;
import org.semanticweb.owlapi.io.OWLOntologyDocumentSource;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.MissingImportHandlingStrategy;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyLoaderConfiguration;
import org.semanticweb.owlapi.model.OWLOntologyManager;

/**
 *
 * @author javier
 */
public class OntologyFacility {

    private final OWLOntologyDocumentSource source;
    private final OWLOntologyManager manager;
    private final OWLOntology ontology;

    public OntologyFacility(final IRI iri)
            throws
            org.semanticweb.owlapi.model.OWLOntologyCreationException {

        source = new IRIDocumentSource(iri);
        manager = OWLManager.createOWLOntologyManager();
        // ignore imports while loading
        OWLOntologyLoaderConfiguration configuration = new OWLOntologyLoaderConfiguration();
        configuration = configuration.setMissingImportHandlingStrategy(MissingImportHandlingStrategy.SILENT);
        ontology = manager.loadOntologyFromOntologyDocument(source, configuration);
    }

    public OWLOntology getOntology() {

        return ontology;
    }

    public OWLClass createOntologyClass(final IRI iri) {

        OWLDataFactory dataFactory = manager.getOWLDataFactory();
        OWLClass ontologyClass = dataFactory.getOWLClass(iri);

        return ontologyClass;
    }
}
