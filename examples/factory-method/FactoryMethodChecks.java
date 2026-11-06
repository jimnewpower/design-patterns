import java.util.ArrayList;
import java.util.List;

/** Dependency-free behavioral checks; no -ea flag required. */
public class FactoryMethodChecks {
    public static void main(String[] args) {
        var tools = List.of("Cameo", "GitLab");
        var text = new FactoryMethodDemo.TextReportExport();
        equal("Tools\n=====\n\nTools\n- Cameo\n- GitLab", text.export("Tools", tools));
        var html = new FactoryMethodDemo.HtmlReportExport();
        equal("<h1>R&amp;D &lt;Tools&gt;</h1>\n<ul>\n  <li>A&amp;B</li>\n  <li>&lt;script&gt;</li>\n</ul>",
                html.export("R&D <Tools>", List.of("A&B", "<script>")));
        equal("Empty\n=====\n\nTools", text.export("Empty", List.of()));
        equal("<h1>Empty</h1>\n<ul>\n</ul>", html.export("Empty", List.of()));
        // A third creator inherits validation and receives a stable input snapshot.
        var input = new ArrayList<>(List.of("Cameo"));
        FactoryMethodDemo.ReportExport extension = new FactoryMethodDemo.ReportExport() {
            protected FactoryMethodDemo.ReportFormatter createFormatter() {
                return (title, snapshot) -> {
                    input.add("Later");
                    equal("[Cameo]", snapshot.toString());
                    fails(UnsupportedOperationException.class, () -> snapshot.add("Mutation"));
                    return title + ": " + String.join(", ", snapshot);
                };
            }
        };
        equal("Extension: Cameo", extension.export("Extension", input));
        for (var exporter : List.of(text, html, extension)) {
            fails(IllegalArgumentException.class, () -> exporter.export("  ", tools));
            fails(NullPointerException.class, () -> exporter.export(null, tools));
            fails(NullPointerException.class, () -> exporter.export("Tools", null));
            fails(NullPointerException.class, () -> exporter.export("Tools", java.util.Arrays.asList("Cameo", null)));
        }
        FactoryMethodDemo.ReportExport broken = new FactoryMethodDemo.ReportExport() {
            protected FactoryMethodDemo.ReportFormatter createFormatter() { return null; }
        };
        fails(NullPointerException.class, () -> broken.export("Tools", tools));
        equal("Again\n=====\n\nTools\n- GitLab", text.export("Again", List.of("GitLab")));
        fails(IllegalArgumentException.class, () -> FactoryMethodDemo.main(new String[]{"pdf"}));
        System.out.println("All Factory Method behavior checks passed.");
    }
    static void equal(String expected, String actual) {
        if (!expected.equals(actual)) throw new AssertionError("Expected:\n" + expected + "\nActual:\n" + actual);
    }
    static void fails(Class<? extends Throwable> type, Runnable action) {
        try { action.run(); }
        catch (Throwable error) {
            if (type.isInstance(error)) return;
            throw new AssertionError("Wrong exception type", error);
        }
        throw new AssertionError("Expected " + type.getSimpleName());
    }
}
