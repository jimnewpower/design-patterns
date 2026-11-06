import java.util.Objects;

public class DecoratorChecks {
    public static void main(String[] args) throws Exception {
        var plain = new DecoratorDemo.PlainReport("Body");
        equal("Title\nBody\n[Note]", new DecoratorDemo.Footer(new DecoratorDemo.Header(plain, "Title"), "Note").render());
        equal("Body", plain.render());
        equal("Outer\nInner\nBody", new DecoratorDemo.Header(new DecoratorDemo.Header(plain, "Inner"), "Outer").render());
        equal("Body\n[A]\n[B]", new DecoratorDemo.Footer(new DecoratorDemo.Footer(plain, "A"), "B").render());
        fails(NullPointerException.class, () -> new DecoratorDemo.Header(null, "x"));
        fails(NullPointerException.class, () -> new DecoratorDemo.Footer(plain, null));
        System.out.println("All Decorator behavior checks passed.");
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
