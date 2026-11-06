import java.util.List;
import java.util.Locale;
import java.util.Objects;

/** Java 21: java FactoryMethodDemo.java text (or html). */
public class FactoryMethodDemo {
    public static void main(String[] args) {
        String format = args.length == 0 ? "text" : args[0];
        ReportExport exporter = switch (format.toLowerCase(Locale.ROOT)) {
            case "text" -> new TextReportExport();
            case "html" -> new HtmlReportExport();
            default -> throw new IllegalArgumentException("Use html or text: " + format);
        };
        System.out.println(exporter.export("Engineering Tools", List.of("Cameo", "Windchill", "GitLab")));
    }

    // Product: the capability used by the creator's workflow.
    interface ReportFormatter {
        String format(String title, List<String> tools);
    }

    // Creator: shared workflow plus an overridable creation hook.
    abstract static class ReportExport {
        public final String export(String title, List<String> tools) {
            Objects.requireNonNull(title, "title");
            if (title.isBlank()) throw new IllegalArgumentException("Title must not be blank");
            List<String> snapshot = List.copyOf(tools);
            ReportFormatter formatter = Objects.requireNonNull(createFormatter(), "formatter");
            return formatter.format(title, snapshot);
        }
        protected abstract ReportFormatter createFormatter();
    }

    // Concrete creators: choose products, inherit the workflow.
    static final class TextReportExport extends ReportExport {
        @Override
        protected ReportFormatter createFormatter() { return new TextReportFormatter(); }
    }
    static final class HtmlReportExport extends ReportExport {
        @Override
        protected ReportFormatter createFormatter() { return new HtmlReportFormatter(); }
    }

    // Concrete products: implement formatting for one output representation.
    static final class TextReportFormatter implements ReportFormatter {
        @Override
        public String format(String title, List<String> tools) {
            StringBuilder output = new StringBuilder(title)
                    .append("\n").append("=".repeat(title.length())).append("\n\nTools");
            for (String tool : tools) output.append("\n- ").append(tool);
            return output.toString();
        }
    }
    static final class HtmlReportFormatter implements ReportFormatter {
        @Override
        public String format(String title, List<String> tools) {
            StringBuilder output = new StringBuilder("<h1>")
                    .append(escape(title)).append("</h1>\n<ul>\n");
            for (String tool : tools) output.append("  <li>").append(escape(tool)).append("</li>\n");
            return output.append("</ul>").toString();
        }
        // HTML text-node escaping, not an attribute escaper or general sanitizer.
        private static String escape(String text) {
            return text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
        }
    }
}
