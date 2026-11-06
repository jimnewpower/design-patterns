import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TemplateMethodChecks {
    public static void main(String[] args) throws Exception {
        equal(List.of("CAMEO","GITLAB"),new TemplateMethodDemo.UppercaseImport().run(List.of(" cameo ","gitlab")));
        equal(List.of("Cameo"),new TemplateMethodDemo.TrimImport().run(List.of(" Cameo ")));
        equal(List.of(),new TemplateMethodDemo.TrimImport().run(List.of()));
        var events = new ArrayList<String>();
        var job = new TemplateMethodDemo.ImportJob() {
            protected String normalize(String value) { events.add("normalize:"+value); return value.strip(); }
            protected void afterImport(List<String> completed) { events.add("after:"+completed.size()); }
        };
        var result = job.run(List.of("A","B")); equal(List.of("normalize:A","normalize:B","after:2"),events);
        fails(UnsupportedOperationException.class, () -> result.add("C"));
        events.clear(); fails(IllegalArgumentException.class, () -> job.run(List.of("A"," ")));
        equal(List.of("normalize:A","normalize: "),events); // No completion hook on validation failure.
        fails(NullPointerException.class, () -> job.run(java.util.Arrays.asList("A",null)));
        System.out.println("All TemplateMethod behavior checks passed.");
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
