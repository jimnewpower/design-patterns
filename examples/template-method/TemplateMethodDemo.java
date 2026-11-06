import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/** Self-contained Java 21 teaching example. */
public class TemplateMethodDemo {
    public static void main(String[] args) {
        System.out.println(new UppercaseImport().run(List.of(" cameo ", " gitlab ")));
        System.out.println(new TrimImport().run(List.of(" cameo ", " gitlab ")));
    }

    abstract static class ImportJob {
        public final List<String> run(List<String> raw) {
            var result = new ArrayList<String>();
            for (String value : List.copyOf(raw)) {
                String normalized = java.util.Objects.requireNonNull(normalize(value));
                validate(normalized); result.add(normalized);
            }
            List<String> completed = List.copyOf(result);
            afterImport(completed);
            return completed;
        }
        protected abstract String normalize(String value);
        protected void validate(String value) {
            if (value.isBlank()) throw new IllegalArgumentException("Blank tool");
        }
        protected void afterImport(List<String> completed) { }
    }
    static final class UppercaseImport extends ImportJob {
        protected String normalize(String value) { return value.strip().toUpperCase(Locale.ROOT); }
    }
    static final class TrimImport extends ImportJob {
        protected String normalize(String value) { return value.strip(); }
    }
}
