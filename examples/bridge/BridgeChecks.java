import java.util.List;
import java.util.Objects;

public class BridgeChecks {
    public static void main(String[] args) throws Exception {
        var tools = List.of("Cameo", "GitLab");
        equal("Tools\n- Cameo\n- GitLab", new BridgeDemo.DetailedReport(new BridgeDemo.TextRenderer()).render("Tools", tools));
        equal("Tools\n- Count: 2", new BridgeDemo.SummaryReport(new BridgeDemo.TextRenderer()).render("Tools", tools));
        equal("<h1>Tools</h1>\n<p>Cameo</p>\n<p>GitLab</p>", new BridgeDemo.DetailedReport(new BridgeDemo.HtmlRenderer()).render("Tools", tools));
        equal("<h1>&lt;Tools&gt;</h1>\n<p>Count: 0</p>", new BridgeDemo.SummaryReport(new BridgeDemo.HtmlRenderer()).render("<Tools>", List.of()));
        equal("<h1>R&amp;D</h1>\n<p>&lt;X&gt;</p>", new BridgeDemo.DetailedReport(new BridgeDemo.HtmlRenderer()).render("R&D", List.of("<X>")));
        fails(NullPointerException.class, () -> new BridgeDemo.DetailedReport(null));
        fails(NullPointerException.class, () -> new BridgeDemo.SummaryReport(new BridgeDemo.TextRenderer()).render("x", null));
        System.out.println("All Bridge behavior checks passed.");
    }

    static void equal(Object expected, Object actual) {
        if (!java.util.Objects.equals(expected, actual)) throw new AssertionError("Expected " + expected + ", got " + actual);
    }
    static void fails(Class<? extends Throwable> type, Runnable action) {
        try { action.run(); }
        catch (Throwable error) {
            if (type.isInstance(error)) return;
            throw new AssertionError("Wrong exception", error);
        }
        throw new AssertionError("Expected " + type.getSimpleName());
    }
}
