package medicis.cami.ontoelster;

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

        Path path = Paths.get("/home/javier/workspace/cominlabs/ontospm/ontos3pm/OntoS3PM.owl");

        return new Object[][]{
            // {IRI.create("http://neurolog.unice.fr/ontoneurolog/v3.1/instrument.owl"),
            //     Paths.get("src", "main", "resources", "instrument.txt")},
            {IRI.create("http://medicis.univ-rennes1.fr/ontologies/ontospm/OntoSPM.owl"),
                Paths.get("src", "main", "resources", "ontospm.txt")},
            {IRI.create("http://purl.obolibrary.org/obo/fma.owl"),
                Paths.get("src", "main", "resources", "fma.txt")},
            {IRI.create(path.toFile()),
                Paths.get("src", "main", "resources", "ontos3pm.txt")}
        };
    }

    @Test(enabled = false, dataProvider = "getInformation")
    public void testPlainOntologyExtractor(IRI ontology, Path file)
            throws Exception {

        List<IRI> classes = IRICreator.getIRIs(file);
        OntologyExtractor extractor = new PlainOntologyExtractor(ontology);
        Main.process(ontology, classes, extractor);
    }

    @Test(enabled = true, dataProvider = "getInformation")
    public void testOntologyExtractorWithEquivalences(IRI ontology, Path file)
            throws Exception {
        List<IRI> classes = IRICreator.getIRIs(file);
        OntologyExtractor extractor = new OntologyExtractorWithEquivalences(ontology);
        Main.process(ontology, classes, extractor);
    }

    @Test(enabled = true, dataProvider = "getInformation")
    public void testOntologyExtractorWithInferences(IRI ontology, Path file)
            throws Exception {
        List<IRI> classes = IRICreator.getIRIs(file);
        OntologyExtractor extractor = new OntologyExtractorWithInferences(ontology);
        Main.process(ontology, classes, extractor);
    }
}
