import java.net.URI;
import java.text.MessageFormat;

/**
 * Created by chantal on 4/7/2017.
 */
public class ClassAnnotations {

    private URI uri;
    private String en;
    private String de;
    private String fr;
    private String definition;
    private String altDe;
    private String altFr;
    private String altEn;

    public ClassAnnotations() {
        en = "";
        de = "";
        fr = "";
        definition = "";
        altDe = "";
        altEn = "";
        altFr = "";
    }

    public void setUri(URI uri) {
        this.uri = uri;
    }

    public void setAltDe(String altDe) {
        this.altDe = altDe;
    }

    public void setAltEn(String altEn) {
        this.altEn = altEn;
    }

    public void setAltFr(String altFr) {
        this.altFr = altFr;
    }

    public void setDe(String de) {
        this.de = de;
    }

    public void setEn(String en) {
        this.en = en;
    }

    public void setFr(String fr) {
        this.fr = fr;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }

    @Override
    public String toString() {
        return MessageFormat.format("{0};{1};{2};{3};{4};{5};{6};{7}{8}", uri, en, fr, de, definition, altEn, altFr, altDe, System.lineSeparator());
    }
}
