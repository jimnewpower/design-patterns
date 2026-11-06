import java.util.Objects;
import java.util.Set;

public class InterpreterChecks {
    public static void main(String[] args) throws Exception {
        var a = new InterpreterDemo.Tag("a");
        var b = new InterpreterDemo.Tag("b");
        equal(true, new InterpreterDemo.And(a,b).evaluate(Set.of("a","b")));
        equal(false, new InterpreterDemo.And(a,b).evaluate(Set.of("a")));
        equal(true, new InterpreterDemo.Or(a,b).evaluate(Set.of("b")));
        equal(false, new InterpreterDemo.Or(a,b).evaluate(Set.of()));
        equal(true, new InterpreterDemo.Not(a).evaluate(Set.of()));
        equal(true, new InterpreterDemo.Not(new InterpreterDemo.Not(a)).evaluate(Set.of("a")));
        equal(false, new InterpreterDemo.And(a,new InterpreterDemo.Not(a)).evaluate(Set.of("a")));
        fails(IllegalArgumentException.class, () -> new InterpreterDemo.Tag(" "));
        fails(NullPointerException.class, () -> new InterpreterDemo.And(a,null));
        fails(NullPointerException.class, () -> new InterpreterDemo.Not(a).evaluate(null));
        System.out.println("All Interpreter behavior checks passed.");
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
