import java.util.List;
import java.util.Objects;

public class CompositeChecks {
    public static void main(String[] args) throws Exception {
        var input = new java.util.ArrayList<CompositeDemo.Component>();
        input.add(new CompositeDemo.Part("A", 10));
        var group = new CompositeDemo.Assembly("Group", input);
        input.add(new CompositeDemo.Part("B", 20));
        equal(10L, group.grams());
        equal(0L, new CompositeDemo.Assembly("Empty", List.of()).grams());
        equal(15L, new CompositeDemo.Assembly("Nested", List.of(group, new CompositeDemo.Part("C", 5))).grams());
        fails(UnsupportedOperationException.class, () -> group.children().clear());
        fails(IllegalArgumentException.class, () -> new CompositeDemo.Part("Bad", -1));
        fails(NullPointerException.class, () -> new CompositeDemo.Assembly("Bad", java.util.Arrays.asList((CompositeDemo.Component)null)));
        fails(ArithmeticException.class, () -> new CompositeDemo.Assembly("Overflow", List.of(new CompositeDemo.Part("Huge", Long.MAX_VALUE), new CompositeDemo.Part("One", 1))).grams());
        System.out.println("All Composite behavior checks passed.");
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
