import java.util.List;

/** Dependency-free behavior checks. Throw on failure; assertions need no -ea flag. */
public class AbstractFactoryChecks {
    public static void main(String[] args) {
        var text = new AbstractFactoryDemo.ReportGenerator(new AbstractFactoryDemo.TextReportFactory());
        equal("Tools\n=====\n\nTool\n- Cameo\n- GitLab", text.generate("Tools", List.of("Cameo", "GitLab")));
        var html = new AbstractFactoryDemo.ReportGenerator(new AbstractFactoryDemo.HtmlReportFactory());
        equal("<h1>R&amp;D &lt;Tools&gt;</h1>\n\n<table>\n  <tr><th>Tool</th></tr>\n  <tr><td>A&amp;B</td></tr>\n  <tr><td>&lt;script&gt;</td></tr>\n</table>",
                html.generate("R&D <Tools>", List.of("A&B", "<script>")));
        equal("Column", new AbstractFactoryDemo.TextTableRenderer().render("Column", List.of()));
        equal("<table>\n  <tr><th>&lt;Column&gt;</th></tr>\n</table>",
                new AbstractFactoryDemo.HtmlTableRenderer().render("<Column>", List.of()));
        try {
            AbstractFactoryDemo.main(new String[]{"pdf"});
            throw new AssertionError("Unsupported format should fail");
        } catch (IllegalArgumentException expected) { }
        System.out.println("All Abstract Factory behavior checks passed.");
    }
    static void equal(String expected, String actual) {
        if (!expected.equals(actual)) throw new AssertionError("Expected:\n" + expected + "\nActual:\n" + actual);
    }
}
