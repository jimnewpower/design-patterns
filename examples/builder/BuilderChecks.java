import java.util.ArrayList;
import java.util.List;

/** Dependency-free behavioral checks; no -ea flag required. */
public class BuilderChecks {
    public static void main(String[] args) {
        var director = new BuilderDemo.ReportDirector();
        var text = new BuilderDemo.TextReportBuilder();
        director.constructEngineeringReport(text);
        equal("Engineering Tools\n=================\n\nTools\n- Cameo\n- Windchill\n- GitLab", text.build());
        var html = new BuilderDemo.HtmlReportBuilder();
        director.constructEngineeringReport(html);
        equal("<h1>Engineering Tools</h1>\n<ul>\n  <li>Cameo</li>\n  <li>Windchill</li>\n  <li>GitLab</li>\n</ul>", html.build());
        equal("<h1>R&amp;D &lt;Tools&gt;</h1>\n<ul>\n  <li>A&amp;B</li>\n  <li>&lt;script&gt;</li>\n</ul>",
                html.title("R&D <Tools>").tools(List.of("A&B", "<script>")).build());
        equal("Minimal\n=======\n\nTools", new BuilderDemo.TextReportBuilder().title("Minimal").build());
        equal("<h1>Minimal</h1>\n<ul>\n</ul>", new BuilderDemo.HtmlReportBuilder().title("Minimal").build());
        var input = new ArrayList<>(List.of("Cameo"));
        text.title("Snapshot").tools(input);
        input.add("Unexpected");
        String first = text.build();
        equal("Snapshot\n========\n\nTools\n- Cameo", first);
        equal(first, text.build());
        text.title("Later").tools(List.of("GitLab"));
        equal("Later\n=====\n\nTools\n- GitLab", text.build());
        equal("Snapshot\n========\n\nTools\n- Cameo", first);
        fails(IllegalStateException.class, () -> new BuilderDemo.TextReportBuilder().build());
        fails(IllegalStateException.class, () -> new BuilderDemo.HtmlReportBuilder().build());
        fails(IllegalArgumentException.class, () -> text.title("  "));
        fails(NullPointerException.class, () -> text.title(null));
        fails(NullPointerException.class, () -> text.tools(null));
        fails(NullPointerException.class, () -> text.tools(java.util.Arrays.asList("Cameo", null)));
        fails(IllegalArgumentException.class, () -> BuilderDemo.main(new String[]{"pdf"}));
        System.out.println("All Builder behavior checks passed.");
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
