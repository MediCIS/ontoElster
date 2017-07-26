package medicis.cami.ontoelster;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import org.semanticweb.owlapi.model.IRI;

/**
 *
 * @author javier
 */
public class IRICreator {

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
}
