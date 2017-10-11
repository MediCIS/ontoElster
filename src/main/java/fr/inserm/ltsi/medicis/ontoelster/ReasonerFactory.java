package fr.inserm.ltsi.medicis.ontoelster;

import openllet.owlapi.OpenlletReasonerFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import uk.ac.manchester.cs.jfact.JFactFactory;

/**
 * @author javier
 */
public class ReasonerFactory {

    private final OWLReasonerFactory factory;

    private ReasonerFactory(final ReasonerImplementation implementation) {

        // TODO stackoverflow 3957637 or 12728985
        switch (implementation) {
            case JFACT:
                factory = new JFactFactory();
                break;
            case HERMIT:
                factory = new org.semanticweb.HermiT.ReasonerFactory();
                break;
            case OPENLLET:
                factory = OpenlletReasonerFactory.getInstance();
                break;
            default:
                throw new AssertionError();
        }
    }

    public OWLReasoner createReasoner(final OWLOntology ontology) {

        return factory.createReasoner(ontology);
    }

    public static ReasonerFactory getInstance(final ReasonerImplementation implementation) {

        return new ReasonerFactory(implementation);
    }
}
