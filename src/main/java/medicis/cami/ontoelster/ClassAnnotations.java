package medicis.cami.ontoelster;

import java.net.URI;
import java.text.MessageFormat;

/**
 *  @author javier
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

        return MessageFormat.format("{0};{1};{2};{3};{4};{5};{6};{7}{8}",
                uri, en, fr, de, definition, altEn, altFr, altDe, System.lineSeparator());
    }
}
