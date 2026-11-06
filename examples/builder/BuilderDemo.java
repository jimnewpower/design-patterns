import java.util.List;
import java.util.Locale;
import java.util.Objects;

/** Java 21: java BuilderDemo.java text (or html). */
public class BuilderDemo {
    public static void main(String[] args) {
        String format = args.length == 0 ? "text" : args[0];
        ReportBuilder builder = switch (format.toLowerCase(Locale.ROOT)) {
            case "text" -> new TextReportBuilder();
            case "html" -> new HtmlReportBuilder();
            default -> throw new IllegalArgumentException("Use html or text: " + format);
        };
        new ReportDirector().constructEngineeringReport(builder);
        System.out.println(builder.build());
    }

    // Builder: construction steps plus common validation and state.
    abstract static class ReportBuilder {
        private String title;
        private List<String> tools = List.of();

        public final ReportBuilder title(String title) {
            Objects.requireNonNull(title, "title");
            if (title.isBlank()) throw new IllegalArgumentException("Title must not be blank");
            this.title = title;
            return this;
        }

        public final ReportBuilder tools(List<String> tools) {
            this.tools = List.copyOf(tools);
            return this;
        }

        protected final String title() {
            if (title == null) throw new IllegalStateException("Set a title before build()");
            return title;
        }

        protected final List<String> tools() { return tools; }
        public abstract String build();
    }

    // Concrete builders: two representations of the completed report.
    static final class TextReportBuilder extends ReportBuilder {
        @Override
        public String build() {
            String title = title();
            StringBuilder output = new StringBuilder(title)
                    .append("\n").append("=".repeat(title.length())).append("\n\nTools");
            for (String tool : tools()) output.append("\n- ").append(tool);
            return output.toString();
        }
    }

    static final class HtmlReportBuilder extends ReportBuilder {
        @Override
        public String build() {
            StringBuilder output = new StringBuilder("<h1>")
                    .append(escape(title())).append("</h1>\n<ul>\n");
            for (String tool : tools()) {
                output.append("  <li>").append(escape(tool)).append("</li>\n");
            }
            return output.append("</ul>").toString();
        }

        // HTML text-node escaping, not an attribute escaper or general sanitizer.
        private static String escape(String text) {
            return text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
        }
    }

    // Director: a repeatable recipe, independent of output representation.
    static final class ReportDirector {
        public void constructEngineeringReport(ReportBuilder builder) {
            builder.title("Engineering Tools")
                   .tools(List.of("Cameo", "Windchill", "GitLab"));
        }
    }
}
