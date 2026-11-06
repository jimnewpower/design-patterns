import java.util.Locale;
import java.util.Map;
import java.util.Objects;

/** Java 21: java SingletonDemo.java */
public class SingletonDemo {
    public static void main(String[] args) {
        FormatCatalog first = ReportFormats.INSTANCE;
        FormatCatalog second = ReportFormats.INSTANCE;
        ReportNamer namer = new ReportNamer(first);
        System.out.println("Same instance: " + (first == second));
        System.out.println(namer.fileName("engineering-tools", "text"));
        System.out.println(namer.fileName("engineering-tools", "HTML"));
    }

    // Consumer-facing abstraction: singleton lifetime is a wiring decision.
    interface FormatCatalog {
        String extensionFor(String format);
    }

    // One enum constant per defining class loader; immutable owned state.
    enum ReportFormats implements FormatCatalog {
        INSTANCE;
        private final Map<String, String> extensions = Map.of("text", ".txt", "html", ".html");

        @Override
        public String extensionFor(String format) {
            Objects.requireNonNull(format, "format");
            String extension = extensions.get(format.toLowerCase(Locale.ROOT));
            if (extension == null) throw new IllegalArgumentException("Unsupported format: " + format);
            return extension;
        }
    }

    // Client: accepts a dependency instead of looking up global state itself.
    static final class ReportNamer {
        private final FormatCatalog catalog;
        ReportNamer(FormatCatalog catalog) {
            this.catalog = Objects.requireNonNull(catalog, "catalog");
        }
        public String fileName(String baseName, String format) {
            Objects.requireNonNull(baseName, "baseName");
            if (baseName.isBlank()) throw new IllegalArgumentException("Base name must not be blank");
            return baseName + catalog.extensionFor(format);
        }
    }
}
