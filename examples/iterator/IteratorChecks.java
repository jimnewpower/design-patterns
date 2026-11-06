import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

public class IteratorChecks {
    public static void main(String[] args) throws Exception {
        var input = new java.util.ArrayList<>(List.of("A","B"));
        var catalog = new IteratorDemo.ToolCatalog(input); input.add("C");
        var a = catalog.iterator(); var b = catalog.iterator();
        equal(true, a.hasNext()); equal(true, a.hasNext());
        equal("A", a.next()); equal("A", b.next());
        equal("B", a.next()); equal(false, a.hasNext());
        fails(NoSuchElementException.class, a::next);
        equal("B", b.next()); fails(UnsupportedOperationException.class, b::remove);
        var empty = new IteratorDemo.ToolCatalog(List.of()).iterator();
        equal(false, empty.hasNext()); fails(NoSuchElementException.class, empty::next);
        var traversed = new java.util.ArrayList<String>(); catalog.forEach(traversed::add);
        equal(List.of("A","B"), traversed);
        fails(NullPointerException.class, () -> new IteratorDemo.ToolCatalog(null));
        System.out.println("All Iterator behavior checks passed.");
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
