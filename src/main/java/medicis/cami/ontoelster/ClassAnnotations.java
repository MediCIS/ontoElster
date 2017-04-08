package medicis.cami.ontoelster;

import java.io.BufferedWriter;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.nio.file.StandardOpenOption.WRITE;
import java.text.MessageFormat;
import java.util.List;

/**
 * Simple structure of selected annotations of a ontology class.
 *
 * @author javier
 */
public class ClassAnnotations {

    public URI uri;
    public String definition = "";
    public String en = "";
    public String de = "";
    public String fr = "";
    public String altDe = "";
    public String altFr = "";
    public String altEn = "";

    @Override
    public String toString() {

        // Change default string to fit as a CSV format
        return MessageFormat.format("{0};{1};{2};{3};{4};{5};{6};{7}{8}",
                uri, en, fr, de, definition, altEn, altFr, altDe, System.lineSeparator());
    }

    /**
     * Print a collection of ClassAnnotations in to a file.
     *
     * @param file Path to a file system file
     * @param annotations List of ClassAnnotations to print
     * @throws java.io.IOException
     */
    public static void print(final Path file, final List<ClassAnnotations> annotations)
            throws
            java.io.IOException {

        OpenOption[] options = new OpenOption[]{WRITE, CREATE, TRUNCATE_EXISTING};
        try (BufferedWriter writer = Files.newBufferedWriter(file, options)) {

            for (ClassAnnotations annotation : annotations) {

                writer.write(annotation.toString());
            }
        }
    }
}
