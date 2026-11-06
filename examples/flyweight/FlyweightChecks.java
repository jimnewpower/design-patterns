import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class FlyweightChecks {
    public static void main(String[] args) throws Exception {
        var factory = new FlyweightDemo.StyleFactory();
        var first = factory.get("Mono", 12);
        if (first != factory.get("Mono", 12)) throw new AssertionError("Style not reused");
        if (first == factory.get("Mono", 14)) throw new AssertionError("Key incomplete");
        equal(2, factory.size());
        equal("A @10 [Mono 12]", new FlyweightDemo.Label("A", 10, first).draw());
        equal("B @20 [Mono 12]", new FlyweightDemo.Label("B", 20, first).draw());
        fails(IllegalArgumentException.class, () -> factory.get("Mono", 0));
        fails(IllegalArgumentException.class, () -> factory.get(" ", 12));
        fails(NullPointerException.class, () -> factory.get(null, 12));
        System.out.println("All Flyweight behavior checks passed.");
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
