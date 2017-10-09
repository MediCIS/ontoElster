package fr.inserm.ltsi.medicis.ontoelster;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import org.semanticweb.owlapi.model.IRI;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 *
 * @author javier
 */
public class OntologyExtractorTest {

    @DataProvider
    private Object[][] getInformation() {

        final Path local = Paths.get("/home/javier/workspace/ontospm/ontos3pm/OntoS3PM.owl");
        final Path directory = Paths.get("src", "test", "resources");

        return new Object[][]{
            {IRI.create("http://neurolog.unice.fr/ontoneurolog/v3.1/instrument.owl"),
                directory.resolve("instrument.txt")},
            {IRI.create("http://medicis.univ-rennes1.fr/ontologies/ontospm/OntoSPM.owl"),
                directory.resolve("ontospm.txt")},
            {IRI.create("http://purl.obolibrary.org/obo/fma.owl"),
                directory.resolve("fma.txt")},
            {IRI.create(local.toFile()),
                directory.resolve("ontos3pm.txt")}
        };
    }

    @Test(enabled = false, dataProvider = "getInformation")
    public void testPlainOntologyExtractor(IRI ontology, Path file)
            throws Exception {

        List<IRI> classes = Main.getIRIs(file);
        OntologyExtractor extractor = new PlainOntologyExtractor(ontology);
        Main.process(ontology, classes, extractor);
    }

    @Test(enabled = false, dataProvider = "getInformation")
    public void testOntologyExtractorWithEquivalences(IRI ontology, Path file)
            throws Exception {

        List<IRI> classes = Main.getIRIs(file);
        OntologyExtractor extractor = new OntologyExtractorWithEquivalences(ontology);
        Main.process(ontology, classes, extractor);
    }

    @Test(enabled = true, dataProvider = "getInformation")
    public void testOntologyExtractorWithInferences(IRI ontology, Path file)
            throws Exception {

        List<IRI> classes = Main.getIRIs(file);
        OntologyExtractor extractor = new OntologyExtractorWithInferences(ontology, ReasonerImplementation.HERMIT);
        Main.process(ontology, classes, extractor);
    }
}
