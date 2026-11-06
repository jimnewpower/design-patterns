import java.util.List;
import java.util.Objects;

/** Self-contained Java 21 teaching example. */
public class BridgeDemo {
    public static void main(String[] args) {
        var tools = List.of("Cameo", "GitLab");
        System.out.println(new DetailedReport(new TextRenderer()).render("Tools", tools));
        System.out.println(new SummaryReport(new HtmlRenderer()).render("Tools", tools));
    }

    interface Renderer {
        String heading(String title);
        String item(String value);
    }
    static final class TextRenderer implements Renderer {
        public String heading(String title) { return title; }
        public String item(String value) { return "- " + value; }
    }
    static final class HtmlRenderer implements Renderer {
        public String heading(String title) { return "<h1>" + escape(title) + "</h1>"; }
        public String item(String value) { return "<p>" + escape(value) + "</p>"; }
        private String escape(String value) {
            return value.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
        }
    }
    // Abstraction hierarchy varies independently from the renderer hierarchy.
    abstract static class Report {
        protected final Renderer renderer;
        Report(Renderer renderer) { this.renderer = Objects.requireNonNull(renderer); }
        public final String render(String title, List<String> tools) {
            Objects.requireNonNull(title);
            return compose(title, List.copyOf(tools));
        }
        protected abstract String compose(String title, List<String> tools);
    }
    static final class DetailedReport extends Report {
        DetailedReport(Renderer renderer) { super(renderer); }
        protected String compose(String title, List<String> tools) {
            StringBuilder output = new StringBuilder(renderer.heading(title));
            for (String tool : tools) output.append("\n").append(renderer.item(tool));
            return output.toString();
        }
    }
    static final class SummaryReport extends Report {
        SummaryReport(Renderer renderer) { super(renderer); }
        protected String compose(String title, List<String> tools) {
            return renderer.heading(title) + "\n" + renderer.item("Count: " + tools.size());
        }
    }
}
