import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MediatorChecks {
    public static void main(String[] args) throws Exception {
        var mediator = new MediatorDemo.ExportMediator();
        var selector = new MediatorDemo.FormatSelector(mediator);
        var preview = new MediatorDemo.Preview(); mediator.connect(selector,preview);
        selector.select("html"); equal("Preview: <h1>Tools</h1>", preview.text()); equal("html",selector.format());
        selector.select("text"); equal("Preview: Tools",preview.text());
        fails(IllegalArgumentException.class, () -> selector.select("pdf"));
        equal("text", selector.format()); equal("Preview: Tools",preview.text());
        var other = new MediatorDemo.FormatSelector(mediator);
        fails(IllegalStateException.class, () -> other.select("html"));
        var otherPreview = new MediatorDemo.Preview(); mediator.connect(other,otherPreview);
        other.select("html"); equal("Preview: Tools",preview.text());
        fails(NullPointerException.class, () -> selector.select(null));
        System.out.println("All Mediator behavior checks passed.");
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
